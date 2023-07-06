package uos.capstone.dms.domain.obesity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ObesityDTO {

    private Long obesityId;
    private LocalDate createdDate;
    private double weight;
    private int obesity;
    private List<String> imageUUid;
    private ObesitySurvey survey;

}
