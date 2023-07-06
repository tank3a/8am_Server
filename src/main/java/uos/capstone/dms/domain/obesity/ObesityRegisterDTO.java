package uos.capstone.dms.domain.obesity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ObesityRegisterDTO {

    private Long petId;
    private double weight;
    private List<MultipartFile> images;
    private int touchRip;
    private int seeRip;
    private int touchBelly;
    private int seeWeistUp;
    private int seeWeistSide;
}
