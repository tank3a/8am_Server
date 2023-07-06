package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.obesity.*;
import uos.capstone.dms.domain.pet.PetDog;

@Mapper(componentModel = "spring")
public interface ObesityMapper {

    ObesityMapper INSTANCE = Mappers.getMapper(ObesityMapper.class);

    @Mapping(target = "result", ignore = true)
    ObesitySurvey registerDTOToObesityDTO(ObesityRegisterDTO registerDTO);

    @Mapping(target = "createdTime", source = "obesity.createdDate")
    @Mapping(target = "obesityId", source = "obesity.obesityId")
    @Mapping(target = "obesity", source = "obesity.obesity")
    @Mapping(target = "weight", source = "obesity.weight")
    ObesityCurrentDTO petDogToObesityCurrentDTO(PetDog petDog, Obesity obesity);

    @Mapping(target = "date", source = "createdDate")
    ObesityGraphDotDTO obesityToObesityGraphDotDTO(Obesity obesity);
}
