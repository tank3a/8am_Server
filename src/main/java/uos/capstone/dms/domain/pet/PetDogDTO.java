package uos.capstone.dms.domain.pet;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDogDTO {

    private String petId;
    private String name;
    private LocalDate birth;
    private int gender;
    private Long breedId;
    private double weight;
    private int obesity;
    private int calorieGoal;
    private PetImageDTO profileImage;

}
