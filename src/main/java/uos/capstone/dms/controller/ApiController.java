package uos.capstone.dms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.token.TokenResponseDTO;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.service.OAuth2UserService;
import uos.capstone.dms.service.TokenService;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final TokenService tokenService;
    private final OAuth2UserService oAuth2UserService;

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refreshToken")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(tokenService.refresh(tokenDTO));
    }

    @Operation(summary = "구글 소셜 로그인")
    @GetMapping("/oauth2/google")
    public ResponseEntity<TokenResponseDTO> oauth2Google(@RequestParam("id_token") String idToken) throws ParseException, JsonProcessingException {
        Map<String, Object> memberMap =  oAuth2UserService.findOrSaveMember(idToken, "google");
        TokenDTO tokenDTO = tokenService.createToken((MemberDTO) memberMap.get("dto"));

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

        return ResponseEntity.status((Integer) memberMap.get("status")).header("Set-Cookie", responseCookie.toString()).body(tokenResponseDTO);
    }


    //redis구현 후 생각
    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public void logout() {
    }
}