package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.pet.Breed;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long>, PagingAndSortingRepository<Breed, Long> {

    List<Breed> findAll();

    Optional<Breed> findById(Long breedId);
}
