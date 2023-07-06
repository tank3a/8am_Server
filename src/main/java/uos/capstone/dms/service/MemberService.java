package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import uos.capstone.dms.domain.token.TokenDTO;
import uos.capstone.dms.domain.user.*;
import uos.capstone.dms.mapper.MemberMapper;
import uos.capstone.dms.repository.MemberImageRepository;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.repository.PetRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberImageRepository imageRepository;
    private final TokenService tokenService;
    private final FileService fileService;
    private final AuthService authService;
    private final PetRepository petRepository;
    private final OAuth2UserService oAuth2UserService;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return memberRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("userId: " + userId + "를 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRoles().stream().map(Role::getType).collect(Collectors.joining(",")));

        return new User(
                member.getUserId(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    //이미지를 eager로 불러옴
    private Member findMemberByUserId(String userId) {
        return memberRepository.findByUserIdEagerLoadImage(userId)
                .orElseThrow(() -> new RuntimeException("해당 ID를 가진 사용자가 존재하지 않습니다."));
    }

    public MemberDTO findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("해당 email을 가진 사용자가 존재하지 않습니다."));
        return MemberMapper.INSTANCE.memberToMemberDTO(member);
    }

    public MemberDataDTO getMember(String userId) {
        Member member = findMemberByUserId(userId);
        MemberDataDTO dataDTO =  MemberMapper.INSTANCE.memberToMemberDataDTO(member);
        dataDTO.setCreatedDate(member.getCreatedDate().toLocalDate());
        return dataDTO;
    }

    @Transactional
    public void saveMember(MemberDTO memberDTO) {
        memberRepository.save(MemberMapper.INSTANCE.memberDTOToMember(memberDTO));
    }

    /**
     * UsernamePasswordAuthenticationToken을 통한 Spring Security인증 진행
     * 이후 tokenService에 userId값을 전달하여 토큰 생성
     * @param requestDTO
     * @return TokenDTO
     */
    @Transactional
    public TokenDTO login(IdPasswordDTO requestDTO) {
        authService.authenticatePassword(requestDTO);

        Member member = memberRepository.findByUserId(requestDTO.getUserId()).get();
        return tokenService.createToken(member);
    }

    @Transactional(readOnly = false)
    public void signup(MemberRequestDTO requestDTO) {
        if(memberRepository.existsByUserId(requestDTO.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = MemberMapper.INSTANCE.memberRequestDTOToMember(requestDTO);
        member.updateRole(Role.ROLE_USER);

        if(!(requestDTO.getMemberImage() == null)) {
            MemberImage memberImage = saveMemberImage(requestDTO.getMemberImage());
            member.updateMemberImage(memberImage);
        }

        memberRepository.save(member);

    }

    @Transactional(readOnly = false)
    public void updateMember(MemberRequestDTO memberRequestDTO, String userId) {

        Member member = memberRepository.findByUserIdEagerLoadImage(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        //중복가입  에러해결필요
        if(member.isSocial()) {
            if(!memberRequestDTO.getEmail().equals(member.getEmail())) {
                throw new RuntimeException("소셜회원은 이메일 변경이 불가합니다.");
            }
        }

        if((member.getMemberImage() != null) && (memberRequestDTO.getMemberImage() != null)) {
            imageRepository.deleteById(member.getMemberImage().getId());
        }
        MemberDTO memberDTO = MemberMapper.INSTANCE.requestDTOToMemberDTO(memberRequestDTO);
        memberDTO.setRoles(member.getRoles());
        memberDTO.setCreatedDate(member.getCreatedDate());
        memberDTO.setSocial(member.isSocial());
        memberDTO.setProvider(member.getProvider());

        Member updatedMember = MemberMapper.INSTANCE.memberDTOToMember(memberDTO);
        if(memberRequestDTO.getPassword() == null) {
            updatedMember.updatePassword(memberRequestDTO.getPassword());
        }
        else {
            updatedMember.updatePassword(member.getPassword());
        }
        if(!(memberRequestDTO.getMemberImage() == null)) {
            MemberImage memberImage = saveMemberImage(memberRequestDTO.getMemberImage());
            updatedMember.updateMemberImage(memberImage);
        }

        log.info("memberservice arrived");
        memberRepository.save(updatedMember);
    }

    @Transactional(readOnly = false)
    private MemberImage saveMemberImage(MultipartFile file) {
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



    @Transactional(readOnly = false)
    public HttpStatus deleteUser(String userId, String password) {
        Member member = memberRepository.findByUserIdEagerLoadImage(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        log.info(member);
        if(member.isSocial() == false) {
            IdPasswordDTO idPasswordDTO = IdPasswordDTO.builder()
                    .userId(userId)
                    .password(password)
                    .build();

            authService.authenticatePassword(idPasswordDTO);
        }

        else {
            try {
                oAuth2UserService.deleteSocialMember(password, member.getProvider().getProvider());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("소셜 연동 해제 중 오류가 발생하였습니다.");
            }
        }


        memberRepository.deleteByUserId(userId);
        return HttpStatus.OK;
    }

    @Transactional(readOnly = true)
    public String hasProfileImage(String uuid) {
        MemberImage memberImage = imageRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("존재하지 않는 이미지입니다."));
        return Paths.get(uploadPath, "member") + File.separator + memberImage.getUuid() + "_" + memberImage.getFileName();

    }


}
