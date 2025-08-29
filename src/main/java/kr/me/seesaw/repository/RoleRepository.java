package kr.me.seesaw.repository;

import kr.me.seesaw.entity.Role;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends Repository<Role, String> {

    void save(Role role);

    List<Role> findAllByIdIn(Collection<String> ids);

}
