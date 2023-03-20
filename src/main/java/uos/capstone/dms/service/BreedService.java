package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uos.capstone.dms.domain.ImageDTO;
import uos.capstone.dms.domain.pet.*;
import uos.capstone.dms.mapper.PetDogMapper;
import uos.capstone.dms.repository.BreedImageRepository;
import uos.capstone.dms.repository.BreedRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BreedService {

    private final BreedRepository breedRepository;

    public List<BreedDTO> getBreedList(Pageable pageable) {
        Page<Breed> page = breedRepository.findAllByOrderByBreedNameAsc(pageable);

        return page.getContent().stream().map(breed -> PetDogMapper.INSTANCE.breedToBreedDTO(breed)).collect(Collectors.toList());
    }

    public Optional<Breed> getBreed(Long id) {
        return breedRepository.findById(id);
    }

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;
    private final FileService fileService;
    private final BreedImageRepository imageRepository;

    @Transactional
    public void insertBreed(BreedInput input) {
        breedRepository.save(Breed.builder()
                .breedName(input.getName())
                .image(saveBreedImage(input.getImage()))
                .build());
    }

    @Transactional
    private BreedImage saveBreedImage(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        Path root = Paths.get(uploadPath, "breed");

        try {
            ImageDTO imageDTO =  fileService.createImageDTO(originalName, root);
            BreedImage breedImage = BreedImage.builder()
                    .uuid(imageDTO.getUuid())
                    .fileName(imageDTO.getFileName())
                    .fileUrl(imageDTO.getFileUrl())
                    .build();

            file.transferTo(Paths.get(imageDTO.getFileUrl()));

            return imageRepository.save(breedImage);
        } catch (IOException e) {
            log.warn("업로드 폴더 생성 실패: " + e.getMessage());
        }

        return null;
    }
}
