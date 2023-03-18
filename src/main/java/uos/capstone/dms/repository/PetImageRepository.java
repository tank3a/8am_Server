package uos.capstone.dms.repository;

import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.PetDog;
import uos.capstone.dms.domain.pet.PetImage;

import java.util.List;

@Repository
public interface PetImageRepository extends ImageRepository {

    List<PetImage> findAllByPetDog(PetDog petDog);
}
