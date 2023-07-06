package uos.capstone.dms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uos.capstone.dms.domain.obesity.ObesityDTO;
import uos.capstone.dms.domain.obesity.ObesityGraphDotDTO;
import uos.capstone.dms.domain.obesity.ObesityRegisterDTO;
import uos.capstone.dms.domain.obesity.ObesityCurrentDTO;
import uos.capstone.dms.domain.pet.PetDogDTO;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.ObesityService;
import uos.capstone.dms.service.PetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/obesity")
@RequiredArgsConstructor
public class ObesityController {

    private final ObesityService obesityService;
    private final PetService petService;

    //비만도 추가
    @PostMapping("/register")
    public ResponseEntity<?> registerObesity(@ModelAttribute ObesityRegisterDTO obesityRegisterDTO) {
        if(!petService.isOwner(SecurityUtil.getCurrentUsername(), obesityRegisterDTO.getPetId())) {
            throw new RuntimeException("애완견 소유주가 아닙니다.");
        }
        HttpHeaders headers = new HttpHeaders();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                        .scheme("http")
                                .host("localhost:8080")
                                        .path("/obesity")
                                                .queryParam("petId", obesityRegisterDTO.getPetId())
                                                        .build();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }

    @DeleteMapping("/delete")
    public void deleteObesity(@RequestParam("obesityId") Long obesityId) {
        obesityService.deleteObesity(obesityId, SecurityUtil.getCurrentUsername());
    }

    @GetMapping("/list")
    public List<ObesityGraphDotDTO> getGraphData(@RequestParam("petId") Long petId) {
        if(!petService.isOwner(SecurityUtil.getCurrentUsername(), petId)) {
            throw new RuntimeException("애완견 소유주가 아닙니다.");
        }
        return obesityService.getGraphData(petId);
    }

    @GetMapping("/detail")
    public ObesityDTO getObesityDetail(@RequestParam("obesityId") Long obesityId) {
        String userId = SecurityUtil.getCurrentUsername();

        PetDogDTO petDogDTO = obesityService.getPetfromObesity(obesityId);
        if(!petService.isOwner(userId, petDogDTO.getPetId())) {
            throw new RuntimeException("애완견의 주인이 아닙니다.");
        }

        return obesityService.getPetObesityByObesityId(obesityId);
    }

    //가장 최신 데이터 불러오기
    public ObesityDTO getObesityDetailFirst(@RequestParam("petId") Long petId) {
        String userId = SecurityUtil.getCurrentUsername();

        if(!petService.isOwner(userId, petId)) {
            throw new RuntimeException("애완견의 주인이 아닙니다.");
        }

        return obesityService.getPetObesityByPetId(petId);
    }


    @GetMapping()
    public Optional<ObesityCurrentDTO> getCurrentObesityData(@RequestParam("petId") Long petId) {
        if(!petService.isOwner(SecurityUtil.getCurrentUsername(), petId)) {
            throw new RuntimeException("애완견 소유주가 아닙니다.");
        }
        return obesityService.getPetObesityDataFromPetId(petId);
    }
}