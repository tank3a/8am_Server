package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uos.capstone.dms.domain.ImageDTO;
import uos.capstone.dms.domain.security.TokenDTO;
import uos.capstone.dms.domain.user.*;
import uos.capstone.dms.mapper.MemberMapper;
import uos.capstone.dms.repository.MemberImageRepository;
import uos.capstone.dms.repository.MemberRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Service
@Log4j2
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberImageRepository imageRepository;
    private final AuthService authService;
    private final FileService fileService;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return memberRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("userId: " + userId + "를 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().toString());

        return new User(
                member.getUserId(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    public MemberResponseDTO findMemberByEntityId(Long entityId) {
        return memberRepository.findByEntityId(entityId)
                .map(member -> MemberMapper.INSTANCE.toMemberResponseDTO(member))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    public MemberResponseDTO findMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(member -> MemberMapper.INSTANCE.toMemberResponseDTO(member))
                .orElseThrow(() -> new RuntimeException("해당 ID를 가진 사용자가 존재하지 않습니다."));
    }

    @Transactional
    public TokenDTO login(LoginRequestDTO requestDTO) {
        return authService.createToken(requestDTO);
    }

    @Transactional
    public void signup(MemberJoinRequestDTO requestDTO) {
        if(memberRepository.existsByUserId(requestDTO.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = MemberMapper.INSTANCE.joinRequestDTOToMember(requestDTO);
        memberRepository.save(member);

        if(!requestDTO.getMemberImage().isEmpty()) {
            MemberImage memberImage = saveMemberImage(requestDTO.getMemberImage());
            member.updateMemberImage(memberImage);
        }


    }

    @Transactional
    private MemberImage saveMemberImage(MultipartFile file) {
        if(file.getContentType().startsWith("image") == false) {
            log.warn("이미지 파일이 아닙니다.");
            return null;
        }

        String originalName = file.getOriginalFilename();
        Path root = Paths.get(uploadPath, "member");

        try {
            ImageDTO imageDTO =  fileService.createImageDTO(originalName, root);
            MemberImage memberImage = MemberImage.builder()
                    .uuid(imageDTO.getUuid())
                    .fileName(imageDTO.getFileName())
                    .fileUrl(imageDTO.getFileUrl())
                    .build();

            file.transferTo(Paths.get(imageDTO.getFileUrl()));

            return imageRepository.save(memberImage);
        } catch (IOException e) {
            log.warn("업로드 폴더 생성 실패: " + e.getMessage());
        }

        return null;
    }
}
