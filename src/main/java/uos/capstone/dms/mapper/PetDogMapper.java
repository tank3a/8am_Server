package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.pet.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PetDogMapper {

    PetDogMapper INSTANCE = Mappers.getMapper(PetDogMapper.class);

    BreedDTO breedToBreedDTO(Breed breed);

    PetDogDTO petDogToPetDogDTO(PetDog petDog);

    @Mapping(target = "breed", ignore = true)
    PetDog registerDTOToPetDog(PetDogRegisterDTO petDogRegisterDTO);

    @Mapping(target = "petId", source = "petImage.petDog.petId")
    PetImageDTO petImageToPetImageDTO(PetImage petImage);

}
