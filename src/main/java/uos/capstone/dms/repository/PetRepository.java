package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.PetDog;

@Repository
public interface PetRepository extends JpaRepository<PetDog, Long> { }
