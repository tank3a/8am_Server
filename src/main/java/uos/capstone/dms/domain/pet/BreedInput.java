package uos.capstone.dms.domain.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class BreedInput {

    private String name;
    private MultipartFile image;
}
