package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uos.capstone.dms.domain.security.TokenDTO;
import uos.capstone.dms.domain.user.LoginRequestDTO;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberResponseDTO;
import uos.capstone.dms.mapper.MemberMapper;
import uos.capstone.dms.repository.MemberRepository;

import java.util.Collections;

@Service
@Log4j2
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return memberRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("userId: " + userId + "를 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().toString());

        return new User(
                member.getUsername(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    public MemberResponseDTO findMemberByEntityId(Long entityId) {
        return memberRepository.findByEntityId(entityId)
                .map(member -> MemberMapper.INSTANCE.memberToMemberResponseDTO(member))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    public MemberResponseDTO findMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(member -> MemberMapper.INSTANCE.memberToMemberResponseDTO(member))
                .orElseThrow(() -> new RuntimeException("해당 ID를 가진 사용자가 존재하지 않습니다."));
    }

    @Transactional
    public TokenDTO login(LoginRequestDTO requestDTO) {
        return authService.createToken(requestDTO);
    }

    @Transactional
    public void signup(MemberRequestDTO requestDTO) {
        if(memberRepository.existsByUserId(requestDTO.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = MemberMapper.INSTANCE.MemberRequestDTOToMember(requestDTO);

        memberRepository.save(member);
    }
}
