package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.ImageDTO;
import uos.capstone.dms.domain.obesity.ObesityImage;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    ObesityImage ImageDTOToObesityImage(ImageDTO imageDTO);

}
