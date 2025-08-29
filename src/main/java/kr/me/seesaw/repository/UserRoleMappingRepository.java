package kr.me.seesaw.repository;

import kr.me.seesaw.entity.UserRoleMapping;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UserRoleMappingRepository extends Repository<UserRoleMapping, String> {

    void save(UserRoleMapping userRoleMapping);

    List<UserRoleMapping> findAllByUserId(String userId);

}
