package kr.me.seesaw.repository;

import kr.me.seesaw.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, String> {

    void save(User user);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);

    List<User> findAllByIdIn(Collection<String> userIds);

    Optional<User> findById(String id);

    User getReferenceById(String id);

}
