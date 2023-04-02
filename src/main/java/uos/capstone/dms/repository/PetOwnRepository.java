package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uos.capstone.dms.domain.pet.PetDog;
import uos.capstone.dms.domain.pet.PetOwner;

import java.util.List;

public interface PetOwnRepository extends JpaRepository<PetOwner, Long> {

    //join fetch 이용하여 petDog fetch
    @Query("select distinct p"
            + " from PetOwner po"
            + " inner join PetDog p"
            + " inner join Member m"
            + " on m.userId = :userId")
    List<PetDog> findAllByMember(@Param("userId") String userId);

}
