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
import uos.capstone.dms.mapper.PetDogMapper;
import uos.capstone.dms.repository.MemberRepository;
import uos.capstone.dms.repository.PetImageRepository;
import uos.capstone.dms.repository.PetRepository;
import uos.capstone.dms.security.SecurityUtil;

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
    private final FileService fileService;
    private final PetImageRepository imageRepository;
    private final BreedService breedService;
    private final MemberRepository memberRepository;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    public List<PetDogDTO> loadMemberPets(String userId) {
        Member member = memberRepository.findByUserId(userId).get();
        List<PetDog> petDogs = petRepository.findAllByMember(member);

        return petDogs.stream()
                .map(petDog ->
                    PetDogMapper.INSTANCE.petDogToPetDogDTO(
                            petDog,
                            imageRepository.findByImageId(petDog.getProfileImageId())
                    )
                )
                .collect(Collectors.toList());
    }

    public List<PetImageDTO> loadPetImages(String petId) {
        return imageRepository.findAllPetImages(SecurityUtil.getCurrentUsername(), petId);
    }


    public PetDogDTO registerPet(PetDogRegisterDTO petDogRegisterDTO) {
        String userId = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByUserId(userId).get();
        if(petRepository.existsByNameAndMember(petDogRegisterDTO.getName(), member)) {
            throw new RuntimeException("이미 존재하는 이름입니다. 다른 이름으로 지어주세요");
        }

        PetDog petDog = PetDogMapper.INSTANCE.registerDTOToPetDog(petDogRegisterDTO, member);
        Breed breed = breedService.getBreed(petDogRegisterDTO.getBreedId()).orElseThrow(() -> new RuntimeException("잘못된 매개변수입니다: Breed ID"));
        petDog.setBreed(breed);
        petRepository.save(petDog);

        if(petDogRegisterDTO.getPetDogImage() != null) {
            PetImage petImage = savePetImage(petDogRegisterDTO.getPetDogImage(), petDog);
            petDog.setProfileImageId(petImage.getId());
            petRepository.save(petDog);
        }

        return PetDogMapper.INSTANCE.petDogToPetDogDTO(petDog, imageRepository.findByImageId(petDog.getProfileImageId()));
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
}
