package uos.capstone.dms.domain.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PetListResponse {

    private List<PetDogDTO> petList;
}
