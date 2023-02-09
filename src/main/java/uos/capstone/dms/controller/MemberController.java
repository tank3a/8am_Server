package uos.capstone.dms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uos.capstone.dms.domain.security.TokenDTO;
import uos.capstone.dms.domain.user.LoginRequestDTO;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberResponseDTO;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.MemberService;

@Controller
@Log4j2
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String memberSignup(@RequestBody MemberRequestDTO memberRequestDTO) {

        memberRequestDTO.setPassword(passwordEncoder.encode(memberRequestDTO.getPassword()));
        memberService.signup(memberRequestDTO);
        return "redirect:/user/login";
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> memberLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(memberService.login(loginRequestDTO));
    }

    @GetMapping("/getMemberData")
    public ResponseEntity<MemberResponseDTO> authenticate() {
        return ResponseEntity.ok(memberService.findMemberByUserId(SecurityUtil.getCurrentUsername()));
    }
}
