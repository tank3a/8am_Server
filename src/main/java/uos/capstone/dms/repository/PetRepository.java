package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.PetDog;
import uos.capstone.dms.domain.pet.PetId;
import uos.capstone.dms.domain.user.Member;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetDog, PetId> {

    List<PetDog> findAllByMember(Member member);

    boolean existsByNameAndMember(@Param("name") String name, @Param("member") Member member);
}
