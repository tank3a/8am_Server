package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uos.capstone.dms.domain.pet.*;
import uos.capstone.dms.mapper.PetDogMapper;
import uos.capstone.dms.repository.BreedRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BreedService {

    private final BreedRepository breedRepository;

    public List<BreedDTO> getBreedList() {
        List<Breed> breedList = breedRepository.findAll();

        return breedList.stream().map(breed -> PetDogMapper.INSTANCE.breedToBreedDTO(breed)).collect(Collectors.toList());
    }

    public Optional<Breed> getBreed(Long id) {
        return breedRepository.findById(id);
    }
}
