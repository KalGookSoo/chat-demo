package kr.me.seesaw.repository;

import kr.me.seesaw.domain.entity.PushDevice;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface PushDeviceRepository extends Repository<PushDevice, String> {

    PushDevice save(PushDevice pushDevice);

    Optional<PushDevice> findById(String id);

    Optional<PushDevice> findByPushToken(String pushToken);

    Optional<PushDevice> findByEndpoint(String endpoint);

    List<PushDevice> findAllByUserId(String userId);

    List<PushDevice> findAllByUserIdAndActiveTrue(String userId);

    void deleteById(String id);

}
