package uos.capstone.dms.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uos.capstone.dms.cookie.CookieUtil;
import uos.capstone.dms.domain.auth.Provider;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.token.TokenResponseDTO;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.domain.user.Role;
import uos.capstone.dms.mapper.MemberMapper;
import uos.capstone.dms.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.service.MemberService;
import uos.capstone.dms.service.TokenService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uos.capstone.dms.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String targetUrl = determineTargetUrl(request, response, authentication);

        MemberDTO memberDTO = memberRepository.findByEmail(oAuth2User.getAttribute("email").toString())
                .map(member -> MemberMapper.INSTANCE.memberToMemberDTO(member))
                .orElse(saveNewMember(oAuth2User));    //orElse에 계정저장

        //소셜이 아닌 회원이 이메일로 저장했을 때
        if (!memberDTO.isSocial()) {
            response.sendError(404, "해당 이메일을 가진 회원이 존재합니다.");
            clearAuthenticationAttributes(request, response);
        } else {
            TokenDTO tokenDTO = tokenService.createToken(memberDTO);
            ResponseCookie refreshTokenCookie = ResponseCookie
                    .from("refresh_token", tokenDTO.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .maxAge(tokenDTO.getDuration())
                    .path("/")
                    .build();

            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("accessToken", tokenDTO.getAccessToken()).build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    protected MemberDTO saveNewMember(OAuth2User oAuth2User) {

        //userId를 나중에 변경해야함
        String userId = oAuth2User.getAttribute("userId").toString().concat(oAuth2User.getAttribute("provider").toString());
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);

        Member member = Member.builder()
                .provider(Provider.of(oAuth2User.getAttribute("provider").toString()))
                .social(true)
                .email(oAuth2User.getAttribute("email"))
                .username(oAuth2User.getAttribute("username"))
                .userId(userId)
                .roles(roles)
                .build();

        memberRepository.save(member);

        return MemberMapper.INSTANCE.memberToMemberDTO(member);

    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUrl.orElse(getDefaultTargetUrl());

        return UriComponentsBuilder.fromUriString(targetUrl).toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
