package uos.capstone.dms.domain.pet;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@ToString(exclude = {"profileImage"})
public class PetDog {

    //애완견 등록번호로
    @Id
    private Long petId;

    private String name;
    private LocalDate birth;
    private int gender;

    @OneToOne
    private Breed breed;

    private double weight;
    private int obesity;
    private int calorieGoal;

    @OneToOne
    private PetImage profileImage;

    @Builder
    public PetDog(Long petId, String name, LocalDate birth, int gender, Breed breed, double weight, int calorieGoal, PetImage profileImage) {
        this.petId = petId;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.breed = breed;
        this.weight = weight;
        this.calorieGoal = calorieGoal;
        this.profileImage = profileImage;
    }

    public void setObesity(int rate) {
        this.obesity = obesity;
    }

    public void setBreed(Breed breed){
        this.breed = breed;
    }

    public void setProfileImage(PetImage image) {
        this.profileImage = image;
    }
}
