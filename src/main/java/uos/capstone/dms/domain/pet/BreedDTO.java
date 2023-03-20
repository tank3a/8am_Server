package uos.capstone.dms.domain.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BreedDTO {

    private long id;
    private String breedName;
    private BreedImage image;

}
