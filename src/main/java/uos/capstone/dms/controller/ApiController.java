package uos.capstone.dms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.token.TokenResponseDTO;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.security.JwtTokenProvider;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.MemberService;
import uos.capstone.dms.service.OAuth2UserService;
import uos.capstone.dms.service.PetService;
import uos.capstone.dms.service.TokenService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    private final TokenService tokenService;
    private final OAuth2UserService oAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final PetService petService;

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, @RequestBody String accessToken) {
        String refreshToken = request.getHeader("Authorization").substring(7);

        if(!jwtTokenProvider.tokenMatches(accessToken, refreshToken)) {
            return ResponseEntity.badRequest().body("두 토큰의 소유주가 일치하지 않습니다.");
        }

        TokenDTO tokenDTO = tokenService.regenerateToken(refreshToken, SecurityUtil.getCurrentUsername());
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(tokenDTO.getDuration())
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()).build();
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
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.status((Integer) memberMap.get("status")).header("Set-Cookie", responseCookie.toString()).body(tokenResponseDTO);
    }

    @GetMapping("/member")
    public ResponseEntity<Resource> getMemberProfileImage(@RequestParam("uuid") String uuid) {

        String filePath = memberService.hasProfileImage(uuid);
        org.springframework.core.io.Resource resource = new FileSystemResource(filePath);

        HttpHeaders headers = new HttpHeaders();
        Path path = Paths.get(filePath);

        try {
            headers.add("Content-Type", Files.probeContentType(path));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("서버 파일 접근 오류");
        }

    }

    @GetMapping("/petDog")
    public ResponseEntity<Resource> getPetImage(@RequestParam("uuid") String uuid) {

        String filePath = petService.getImage(uuid);
        org.springframework.core.io.Resource resource = new FileSystemResource(filePath);

        HttpHeaders headers = new HttpHeaders();
        Path path = Paths.get(filePath);

        try {
            headers.add("Content-Type", Files.probeContentType(path));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("서버 파일 접근 오류");
        }

    }
}