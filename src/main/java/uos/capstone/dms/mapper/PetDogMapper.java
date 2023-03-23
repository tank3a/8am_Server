package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.pet.*;
import uos.capstone.dms.domain.user.Member;

@Mapper(componentModel = "spring")
public interface PetDogMapper {

    PetDogMapper INSTANCE = Mappers.getMapper(PetDogMapper.class);

    BreedDTO breedToBreedDTO(Breed breed);

    @Mapping(target = "profileImage", source = "petImageDTO")
    @Mapping(target = "breedId", expression = "java(petDog.getBreed().getId())")
    @Mapping(target = "petId", source = "petDog.petId")
    PetDogDTO petDogToPetDogDTO(PetDog petDog, PetImageDTO petImageDTO);

    @Mapping(target = "breed", ignore = true)
    @Mapping(target = "member", source = "member")
    @Mapping(target = "birth", source = "petDogRegisterDTO.birth")
    @Mapping(target = "gender", source = "petDogRegisterDTO.gender")
    @Mapping(target = "petId", source = "petDogRegisterDTO.petId")
    PetDog registerDTOToPetDog(PetDogRegisterDTO petDogRegisterDTO, Member member);

}
