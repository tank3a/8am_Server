package uos.capstone.dms.domain.pet;

import jakarta.persistence.*;
import lombok.*;
import uos.capstone.dms.domain.user.Member;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@IdClass(PetId.class)
@AllArgsConstructor
public class PetDog {

    @Id
    private String petId;

    @Id
    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    private String name;
    private LocalDate birth;
    private int gender;

    @OneToOne
    private Breed breed;

    private double weight;
    private int obesity;
    private int calorieGoal;

    private Long profileImageId;

    @Builder
    public PetDog(String petId, Member member, String name, LocalDate birth, int gender, Breed breed, double weight, int calorieGoal, Long profileImageId) {
        this.petId = petId;
        this.member = member;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.breed = breed;
        this.weight = weight;
        this.calorieGoal = calorieGoal;
        this.profileImageId = profileImageId;
    }

    public void setObesity(int rate) {
        this.obesity = obesity;
    }

    public void setBreed(Breed breed){
        this.breed = breed;
    }

    public void setProfileImageId(Long id) {
        this.profileImageId = id;
    }
}
