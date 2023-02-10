package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uos.capstone.dms.domain.security.RefreshToken;
import uos.capstone.dms.domain.security.TokenDTO;
import uos.capstone.dms.domain.user.LoginRequestDTO;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.repository.RefreshTokenRepository;
import uos.capstone.dms.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDTO createToken(LoginRequestDTO requestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDTO.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDTO tokenDTO = tokenProvider.createTokenDTO(authentication);
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(authentication.getName())
                .token(tokenDTO.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDTO;
    }

    @Transactional
    public TokenDTO refresh(TokenDTO tokenDTO) {
        if(!tokenProvider.validateToken(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getToken().equals(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        TokenDTO tokenDto = tokenProvider.createTokenDTO(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}
