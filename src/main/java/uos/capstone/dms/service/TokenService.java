package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
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
                .memberId(member.getId())
                .token(tokenDTO.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDTO;
    }

    public TokenDTO createToken(Member member) {
        TokenDTO tokenDTO = tokenProvider.createTokenDTO(member.getUserId(), member.getRoles());
        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(member.getId())
                .token(tokenDTO.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDTO;
    }

    public TokenDTO regenerateToken(String refreshToken, String userId) {
        refreshTokenRepository.deleteById(refreshToken);
        return createToken(memberRepository.findById(userId).get());

    }
}
