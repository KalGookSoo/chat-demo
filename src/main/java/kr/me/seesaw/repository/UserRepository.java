package kr.me.seesaw.repository;

import kr.me.seesaw.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);
}
