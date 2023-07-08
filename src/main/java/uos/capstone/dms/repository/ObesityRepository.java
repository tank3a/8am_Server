package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uos.capstone.dms.domain.obesity.Obesity;
import uos.capstone.dms.domain.pet.PetDog;

import java.util.List;
import java.util.Optional;

public interface ObesityRepository extends JpaRepository<Obesity, Long> {

    List<Obesity> findAllByPetDog(PetDog petDog);

    @Query("select o from Obesity o where o.petDog.petId = :petId")
    List<Obesity> findAllByPetId(@Param("petId") Long petId);

    @Query("select o.petDog from Obesity o where o.obesityId = :obesityId")
    Optional<PetDog> findPetDogByObesityId(@Param("obesityId") Long obesityId);
}
