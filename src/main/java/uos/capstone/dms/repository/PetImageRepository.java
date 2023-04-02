package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.PetImage;
import uos.capstone.dms.domain.pet.PetImageDTO;

import java.util.List;

@Repository
public interface PetImageRepository extends ImageRepository {

    @Query("select pi from PetImage pi where pi.petDog.id = :petId")
    List<PetImage> findAllPetImages(@Param("petId") String petId);

    @Query("select pi from PetImage pi where id = :imageId")
    PetImage findByImageId(Long imageId);
}