package uos.capstone.dms.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uos.capstone.dms.domain.pet.*;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.BreedService;
import uos.capstone.dms.service.PetService;

import java.util.List;

@Tag(name = "애완견 관련", description = "애완견 관련 API")
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final BreedService breedService;
    private final PetService petService;


    @GetMapping("/getPetList")
    public PetListResponse getPetList() {
        String userId = SecurityUtil.getCurrentUsername();

        return PetListResponse.builder().petList(petService.loadMemberPets(userId)).build();
    }

    //breedResponse값을 총 페이지수로 변경해야함
    @GetMapping("/getBreedPage")
    public BreedListResponse getBreedList(Pageable pageable) {
        List<BreedDTO> breedList = breedService.getBreedList(pageable);


        return BreedListResponse.builder()
                .currentPage(pageable.getPageNumber())
                .breedList(breedList)
                .build();
    }

    @PostMapping("/dog/register")
    public PetDogDTO registerPetDog(@ModelAttribute PetDogRegisterDTO petDogRegisterDTO) {

        return petService.registerPet(petDogRegisterDTO);
    }

}
