package tech.getarrays.employeemanager.service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.getarrays.employeemanager.model.UserE;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserE, Long> {

    Optional<UserE> findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByPassword(String password);

    Boolean existsByEmail(String email);
}
