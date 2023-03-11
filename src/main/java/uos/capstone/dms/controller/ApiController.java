package uos.capstone.dms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.service.OAuth2UserService;
import uos.capstone.dms.service.TokenService;

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
    public ResponseEntity<MemberDTO> oauth2Google(@RequestParam("id_token") String idToken) throws ParseException, JsonProcessingException {
        MemberDTO memberDTO = oAuth2UserService.findOrSaveMember(idToken, "google");

        return ResponseEntity.ok(memberDTO);
    }
}
