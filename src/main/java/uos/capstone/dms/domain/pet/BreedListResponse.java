package uos.capstone.dms.domain.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class BreedListResponse {

    private int currentPage;
    private List<BreedDTO> breedList;

}
