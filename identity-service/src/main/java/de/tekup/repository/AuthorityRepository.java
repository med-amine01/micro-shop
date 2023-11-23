package de.tekup.repository;

import de.tekup.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
     Authority findByName(String name);
     
     @Query("SELECT a.name FROM Authority a WHERE a.name IN :authorityNames")
     List<String> findNonExistentAuthorities(@Param("authorityNames") Collection<String> authorityNames);
}
