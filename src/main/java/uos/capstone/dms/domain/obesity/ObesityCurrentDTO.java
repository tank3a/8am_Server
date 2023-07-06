package uos.capstone.dms.domain.obesity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ObesityCurrentDTO {

    private Long obesityId;
    private String name;
    private double weight;
    private int obesity;
    private int calorieGoal;
    private LocalDateTime createdTime;
}
