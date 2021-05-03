package library.repository;

import library.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    List<UserEntity> findAllByUsername(String username);
    Optional<UserEntity> findAllByUsernameAndEnabledIsTrue(String username);
}
