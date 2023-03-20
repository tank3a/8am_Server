package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.PetImageDTO;

import java.util.List;

@Repository
public interface PetImageRepository extends ImageRepository {

    @Query("select new uos.capstone.dms.domain.pet.PetImageDTO(pi.id, pi.uuid, pi.fileName, pi.fileUrl, pi.petDog.member.userId, pi.petDog.petId) from PetImage pi where pi.petDog.member.userId = :userId and pi.petDog.petId = :petId")
    List<PetImageDTO> findAllPetImages(@Param("userId") String userId, @Param("petId") String petId);

    @Query("select new uos.capstone.dms.domain.pet.PetImageDTO(pi.id, pi.uuid, pi.fileName, pi.fileUrl, pi.petDog.member.userId, pi.petDog.petId) from PetImage pi where pi.id = :imageId")
    PetImageDTO findByImageId(@Param("imageId") Long imageId);
}