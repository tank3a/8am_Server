package uos.capstone.dms.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import uos.capstone.dms.domain.auth.Provider;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.token.TokenResponseDTO;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.domain.user.Role;
import uos.capstone.dms.service.MemberService;
import uos.capstone.dms.service.TokenService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            MemberDTO memberDTO = memberService.findMemberByEmail((String) oAuth2User.getAttributes().get("email"));
            if(memberDTO.isSocial()) {
                TokenDTO tokenDTO = tokenService.createToken(memberDTO);

                ResponseCookie responseCookie = ResponseCookie
                        .from("refresh_token", tokenDTO.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .maxAge(tokenDTO.getDuration())
                        .path("/")
                        .build();

                TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                        .isNewMember(false)
                        .accessToken(tokenDTO.getAccessToken())
                        .build();

                response.addHeader("Set-Cookie", responseCookie.toString());

                String body = objectMapper.writeValueAsString(tokenResponseDTO);
                response.getWriter().write(body);
            }

            else {
                throw new RuntimeException("해당 email을 가진 회원이 존재합니다.");
            }

        } catch (Exception e) {
            //처음 로그인한 회원일 때
            String userId = oAuth2User.getAttribute("userId").toString().concat(oAuth2User.getAttribute("provider").toString());
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ROLE_USER);

            MemberDTO memberDTO = MemberDTO.builder()
                    .provider(Provider.of(oAuth2User.getAttribute("provider").toString()))
                    .social(true)
                    .email(oAuth2User.getAttribute("email"))
                    .username(oAuth2User.getAttribute("username"))
                    .userId(userId)
                    .roles(roles)
                    .build();

            memberService.saveMember(memberDTO);
            TokenDTO tokenDTO = tokenService.createToken(memberDTO);

            ResponseCookie responseCookie = ResponseCookie
                    .from("refresh_token", tokenDTO.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .maxAge(tokenDTO.getDuration())
                    .path("/")
                    .build();

            TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                    .isNewMember(false)
                    .accessToken(tokenDTO.getAccessToken())
                    .build();

            response.addHeader("Set-Cookie", responseCookie.toString());

            String body = objectMapper.writeValueAsString(tokenResponseDTO);
            response.getWriter().write(body);
        }
    }
}
