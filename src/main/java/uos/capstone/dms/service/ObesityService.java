package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uos.capstone.dms.domain.Image;
import uos.capstone.dms.domain.obesity.*;
import uos.capstone.dms.domain.pet.PetDog;
import uos.capstone.dms.domain.pet.PetDogDTO;
import uos.capstone.dms.mapper.ImageMapper;
import uos.capstone.dms.mapper.ObesityMapper;
import uos.capstone.dms.mapper.PetDogMapper;
import uos.capstone.dms.repository.ObesityImageRepository;
import uos.capstone.dms.repository.ObesityRepository;
import uos.capstone.dms.repository.ObesitySurveyRepository;
import uos.capstone.dms.security.SecurityUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObesityService {

    private final PetService petService;
    private final FileService fileService;
    private final ObesityImageRepository imageRepository;
    private final ObesitySurveyRepository surveyRepository;
    private final ObesityRepository obesityRepository;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Transactional
    public ObesityCurrentDTO register(ObesityRegisterDTO obesityRegisterDTO) {

        PetDog petDog = petService.findPetById(obesityRegisterDTO.getPetId());
        if(obesityRegisterDTO.getWeight() < 0) {
            throw new RuntimeException("비만도는 음수일 수 없습니다.");
        }
        petDog.setWeight(obesityRegisterDTO.getWeight());
        List<ObesityImage> images = obesityRegisterDTO.getImages().stream()
                .map(image -> {
                    try {
                        return fileService.createImageDTO(image.getOriginalFilename(), Paths.get(uploadPath, "obesity"));
                    } catch (IOException e) {
                        throw new RuntimeException("입출력 오류");
                    }
                })
                .map(imageDTO -> ImageMapper.INSTANCE.ImageDTOToObesityImage(imageDTO))
                .collect(Collectors.toList());

        ObesitySurvey survey = ObesityMapper.INSTANCE.registerDTOToObesityDTO(obesityRegisterDTO);
        /**중간에는 비만도 측정하는 로직으로 보내야함 현재는 default = 0 + 설문결과도 판단해야함**/
        survey.setResult(0);
        petDog.setObesity(0);

        Obesity obesity = Obesity.builder()
                .petDog(petDog)
                .weight(obesityRegisterDTO.getWeight())
                .obesity(petDog.getObesity())
                .build();

        Obesity obesitySaved = obesityRepository.save(obesity);
        survey.setObesity(obesitySaved);
        surveyRepository.save(survey);
        images.stream().forEach(obesityImage -> {
            obesityImage.setObesity(obesitySaved);
            imageRepository.save(obesityImage);
        });

        return ObesityMapper.INSTANCE.petDogToObesityCurrentDTO(petDog, obesitySaved);
    }

    @Transactional(readOnly = true)
    public Optional<ObesityCurrentDTO> getPetObesityDataFromPetId(Long petId) {
        PetDog petDog = petService.findPetById(petId);
        return obesityRepository.findAllByPetDog(petDog).stream().min(Comparator.comparing(Obesity::getCreatedDate)).map(obesity -> ObesityMapper.INSTANCE.petDogToObesityCurrentDTO(petDog, obesity));
    }

    @Transactional(readOnly = true)
    public ObesityDTO getPetObesityByObesityId(Long obesityId) {
        Obesity obesity = obesityRepository.findById(obesityId).orElseThrow(() -> new RuntimeException("존재하지 않는 비만도 ID입니다."));
        ObesitySurvey survey = surveyRepository.findByObesity(obesity).orElse(ObesitySurvey.builder().build());
        List<String> imageUuid = imageRepository.findAllByObesity(obesity).stream().map(Image::getUuid).collect(Collectors.toList());
        return ObesityDTO.builder()
                .imageUUid(imageUuid)
                .survey(survey)
                .createdDate(obesity.getCreatedDate())
                .weight(obesity.getWeight())
                .obesity(obesity.getObesity())
                .obesityId(obesity.getObesityId())
                .build();

    }

    @Transactional(readOnly = true)
    public ObesityDTO getPetObesityByPetId(Long petId) {
        PetDog petDog = petService.findPetById(petId);
        Obesity obesity = obesityRepository.findAllByPetDog(petDog).stream().sorted(Comparator.comparing(Obesity::getCreatedDate)).findFirst().orElseThrow(() -> new RuntimeException("비만도 데이터가 존재하지 않습니다."));
        ObesitySurvey survey = surveyRepository.findByObesity(obesity).orElse(ObesitySurvey.builder().build());
        List<String> imageUuid = imageRepository.findAllByObesity(obesity).stream().map(Image::getUuid).collect(Collectors.toList());
        return ObesityDTO.builder()
                .imageUUid(imageUuid)
                .survey(survey)
                .createdDate(obesity.getCreatedDate())
                .weight(obesity.getWeight())
                .obesity(obesity.getObesity())
                .obesityId(obesity.getObesityId())
                .build();

    }

    @Transactional
    public void deleteObesity(Long obesityId, String userId) {
        Obesity obesity = obesityRepository.findById(obesityId).orElseThrow(() -> new RuntimeException("존재하지 않는 비만도ID입니다."));
        if(!petService.isOwner(SecurityUtil.getCurrentUsername(), obesity.getPetDog().getPetId())) {
            throw new RuntimeException("애완견 소유주가 아닙니다.");
        }

        obesityRepository.deleteById(obesityId);
    }

    @Transactional(readOnly = true)
    public List<ObesityGraphDotDTO> getGraphData(Long petId) {
        List<Obesity> obesityList = obesityRepository.findAllByPetId(petId);

        return obesityList.stream().map(obesity -> ObesityMapper.INSTANCE.obesityToObesityGraphDotDTO(obesity)).sorted(Comparator.comparing(ObesityGraphDotDTO::getDate).reversed()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetDogDTO getPetfromObesity(Long obesityId) {
        PetDog petDog = obesityRepository.findPetDogByObesityId(obesityId).orElseThrow(() -> new RuntimeException("잘못된 접근입니다. 비만도에 해당하는 애완견이 존재하지 않습니다."));

        return PetDogMapper.INSTANCE.petDogToPetDogDTO(petDog);
    }

}
