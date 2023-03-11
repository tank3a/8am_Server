package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import uos.capstone.dms.domain.user.LoginRequestDTO;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void authenticateLogin(LoginRequestDTO requestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDTO.toAuthentication();
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}
