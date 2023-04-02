package uos.capstone.dms.domain.pet;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDogDTO {

    private Long petId;
    private String name;
    private LocalDate birth;
    private int gender;
    private Breed breed;
    private double weight;
    private int obesity;
    private int calorieGoal;
    private PetImageDTO profileImage;
}
