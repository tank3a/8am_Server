package uos.capstone.dms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.token.TokenResponseDTO;
import uos.capstone.dms.domain.user.IdPasswordDTO;
import uos.capstone.dms.domain.user.MemberDataDTO;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.MemberService;

@Tag(name = "사용자 관련", description = "사용자 관련 API")
@RestController
@Log4j2
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> memberSignup(@ModelAttribute MemberRequestDTO memberRequestDTO) {
        memberRequestDTO.setPassword(passwordEncoder.encode(memberRequestDTO.getPassword()));
        memberService.signup(memberRequestDTO);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    //social여부 체크해야함
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<String> memberLogin(@ModelAttribute IdPasswordDTO idPasswordDTO, HttpServletResponse response) {
        log.info(idPasswordDTO);
        TokenDTO tokenDTO = memberService.login(idPasswordDTO);
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(tokenDTO.getDuration())
                .path("/")
                .build();

        response.setHeader("Authorization", "Bearer " + tokenDTO.getAccessToken());
        response.setHeader("Set-Cookie", cookie.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원정보 호출")
    @GetMapping("/getMemberData")
    public ResponseEntity<MemberDataDTO> loadMemberData() {
        return ResponseEntity.ok(memberService.getMember(SecurityUtil.getCurrentUsername()));
    }

    @Operation(summary = "회원정보 수정")
    @PostMapping("/modify")
    public ResponseEntity memberModify(@ModelAttribute MemberRequestDTO memberRequestDTO) {
        log.info(memberRequestDTO);

        if(!SecurityUtil.getCurrentUsername().equals(memberRequestDTO.getUserId())) {
            log.warn("잘못된 회원 ID로 접근하였습니다.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        memberRequestDTO.setPassword(passwordEncoder.encode(memberRequestDTO.getPassword()));
        memberService.updateMember(memberRequestDTO, SecurityUtil.getCurrentUsername());
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity memberDelete(@RequestBody String password) {
        String userId = SecurityUtil.getCurrentUsername();

        HttpStatus httpStatus = memberService.deleteUser(userId, password);

        return new ResponseEntity<>(httpStatus);
    }

    //로그아웃(access token 블랙리스트를 만들고 refresh token은 여부 체크)

    //아이디+비밀번호 찾기
}
