package uos.capstone.dms.domain.obesity;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ObesityGraphDotDTO {

    private Long obesityId;
    private double weight;
    private LocalDate date;
    private int obesity;
}
