package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uos.capstone.dms.domain.pet.PetDog;
import uos.capstone.dms.domain.pet.PetOwner;
import uos.capstone.dms.domain.pet.PetOwnerId;
import uos.capstone.dms.domain.user.Member;

import java.util.List;

public interface PetOwnRepository extends JpaRepository<PetOwner, PetOwnerId> {

    //join fetch 이용하여 petDog fetch
    @Query("select po.petDog"
            + " from PetOwner po"
            + " where po.member.userId = :userId")
    List<PetDog> findAllByMember(@Param("userId") String userId);

    @Query("select distinct m"
            + " from PetOwner po"
            + " inner join PetDog p"
            + " inner join Member m"
            + " on p.petId = :petId")
    List<Member> findAllByPet(@Param("petId") Long petId);

    @Modifying
    @Transactional
    void deleteAllByMember(Member member);
}
