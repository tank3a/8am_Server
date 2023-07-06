package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uos.capstone.dms.domain.ImageDTO;
import uos.capstone.dms.domain.pet.*;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberImage;
import uos.capstone.dms.mapper.PetDogMapper;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.repository.PetImageRepository;
import uos.capstone.dms.repository.PetOwnRepository;
import uos.capstone.dms.repository.PetRepository;
import uos.capstone.dms.security.SecurityUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PetService {

    private final PetRepository petRepository;
    private final PetOwnRepository petOwnRepository;
    private final FileService fileService;
    private final PetImageRepository imageRepository;
    private final BreedService breedService;
    private final MemberRepository memberRepository;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    public List<PetDogDTO> loadMemberPets(String userId) {
        List<PetDog> petDogs = petOwnRepository.findAllByMember(userId);

        return petDogs.stream()
                .map(petDog -> PetDogMapper.INSTANCE.petDogToPetDogDTO(petDog))
                .collect(Collectors.toList());
    }

    //여기에 주인인지 여부 체크해야함
    public List<PetImageDTO> loadPetImages(Long petId) {
        return imageRepository.findAllPetImages(petId).stream()
                .map(petImage -> PetDogMapper.INSTANCE.petImageToPetImageDTO(petImage))
                .collect(Collectors.toList());
    }


    @Transactional
    public PetDogDTO registerPet(PetDogRegisterDTO petDogRegisterDTO) {
        String userId = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByUserId(userId).get();
        if(petRepository.existsById(petDogRegisterDTO.getPetId())) {
            throw new RuntimeException("이미 등록된 애완견입니다.");
        }

        PetDog petDog = PetDogMapper.INSTANCE.registerDTOToPetDog(petDogRegisterDTO);
        Breed breed = breedService.getBreed(petDogRegisterDTO.getBreedId()).orElseThrow(() -> new RuntimeException("잘못된 매개변수입니다: Breed ID"));
        petDog.setBreed(breed);
        petRepository.save(petDog);

        if(petDogRegisterDTO.getPetDogImage() != null) {
            PetImage petImage = savePetImage(petDogRegisterDTO.getPetDogImage(), petDog);
            petDog.setProfileImage(petImage);
            petRepository.save(petDog);
        }

        PetOwner petOwner = PetOwner.builder()
                .isOwner(true)
                .member(member)
                .petDog(petDog)
                .expireDateTime(null)
                .build();

        petOwnRepository.save(petOwner);

        return PetDogMapper.INSTANCE.petDogToPetDogDTO(petDog);
    }

    @Transactional
    private PetImage savePetImage(MultipartFile file, PetDog petDog) {
        String originalName = file.getOriginalFilename();
        Path root = Paths.get(uploadPath, "petDog");

        try {
            ImageDTO imageDTO =  fileService.createImageDTO(originalName, root);
            PetImage petImage = PetImage.builder()
                    .uuid(imageDTO.getUuid())
                    .fileName(imageDTO.getFileName())
                    .fileUrl(imageDTO.getFileUrl())
                    .petDog(petDog)
                    .build();

            file.transferTo(Paths.get(imageDTO.getFileUrl()));

            return imageRepository.save(petImage);
        } catch (IOException e) {
            log.warn("업로드 폴더 생성 실패: " + e.getMessage());
        }

        return null;
    }

    @Transactional
    public void deletePet(PetDog petDog) {
        List<PetImage> images = imageRepository.findAllPetImages(petDog.getPetId());
        petDog.setProfileImage(null);
        images.stream().forEach(petImage -> imageRepository.deleteById(petImage.getId()));
        petRepository.deleteById(petDog.getPetId());

    }

    @Transactional(readOnly = true)
    public String getImage(String uuid) {
        PetImage petImage = imageRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("존재하지 않는 이미지입니다."));
        return Paths.get(uploadPath, "petDog") + File.separator + petImage.getUuid() + "_" + petImage.getFileName();
    }

    @Transactional(readOnly = true)
    public PetDog findPetById(Long petId) {
        return petRepository.findById(petId).orElseThrow(() -> new RuntimeException("존재하지 않는 애완견입니다."));
    }

    @Transactional(readOnly = true)
    public boolean isOwner(String userId, Long petId) {
        return petOwnRepository.existsByMemberAndPet(userId, petId);
    }
}
