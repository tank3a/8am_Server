package uos.capstone.dms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uos.capstone.dms.domain.security.TokenDTO;
import uos.capstone.dms.service.AuthService;

@Controller
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final AuthService authService;

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(authService.refresh(tokenDTO));
    }
}
