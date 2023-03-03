package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import uos.capstone.dms.domain.token.RefreshToken;
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberDTO;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.repository.RefreshTokenRepository;
import uos.capstone.dms.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public TokenDTO createToken(MemberDTO memberDTO) {
        TokenDTO tokenDTO = tokenProvider.createTokenDTO(memberDTO.getUserId(), memberDTO.getRoles());
        Member member = memberRepository.findByUserId(memberDTO.getUserId()).orElseThrow(() -> new RuntimeException("Wrong Access (member does not exist)"));
        RefreshToken refreshToken = RefreshToken.builder()
                .member(member)
                .token(tokenDTO.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDTO;
    }

    public TokenDTO refresh(TokenDTO tokenDTO) {
        if(!tokenProvider.validateToken(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByMember(memberRepository.findByUserId(authentication.getName()).get())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getToken().equals(tokenDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        Member member = memberRepository.findByUserId(refreshToken.getMember().getUserId()).orElseThrow(() -> new RuntimeException("존재하지 않는 계정입니다."));
        TokenDTO tokenDto = tokenProvider.createTokenDTO(member.getUserId(), member.getRoles());

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}
