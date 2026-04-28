package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.PushDeviceRegisterRequest;
import kr.me.seesaw.domain.dto.PushDeviceResponse;
import kr.me.seesaw.domain.entity.PushDevice;
import kr.me.seesaw.domain.entity.PushProvider;
import kr.me.seesaw.domain.entity.User;
import kr.me.seesaw.repository.PushDeviceRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DefaultPushDeviceService implements PushDeviceService {

    private final PushDeviceRepository pushDeviceRepository;

    private final UserRepository userRepository;

    @Override
    public PushDeviceResponse registerDevice(String userId, PushDeviceRegisterRequest request) {
        log.info("푸시 기기 등록 요청: userId={}, provider={}", userId, request.provider());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. id: " + userId));

        PushDevice device = findExistingDevice(request)
                .map(existing -> {
                    updateExistingDevice(existing, user, request);
                    return existing;
                })
                .orElseGet(() -> createNewDevice(user, request));

        PushDevice saved = pushDeviceRepository.save(device);
        log.info("푸시 기기 등록 완료: deviceId={}", saved.getId());
        return PushDeviceResponse.from(saved);
    }

    private Optional<PushDevice> findExistingDevice(PushDeviceRegisterRequest request) {
        if (request.provider() == PushProvider.WEB_PUSH) {
            return pushDeviceRepository.findByEndpoint(request.endpoint());
        } else {
            return pushDeviceRepository.findByPushToken(request.token());
        }
    }

    private void updateExistingDevice(PushDevice existing, User user, PushDeviceRegisterRequest request) {
        existing.updateUser(user);
        if (request.provider() == PushProvider.WEB_PUSH) {
            existing.updateWebPush(
                    request.keys().get("p256dh"),
                    request.keys().get("auth"),
                    request.platform(),
                    request.deviceId()
            );
        } else {
            existing.updateExpoPush(request.token(), request.platform(), request.deviceId());
        }
    }

    private PushDevice createNewDevice(User user, PushDeviceRegisterRequest request) {
        PushDevice.PushDeviceBuilder builder = PushDevice.builder()
                .user(user)
                .provider(request.provider())
                .userAgent(request.platform())
                .deviceName(request.deviceId());

        if (request.provider() == PushProvider.WEB_PUSH) {
            builder.endpoint(request.endpoint())
                    .p256dh(request.keys().get("p256dh"))
                    .auth(request.keys().get("auth"));
        } else {
            builder.pushToken(request.token());
        }

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PushDeviceResponse> getMyDevices(String userId) {
        log.debug("내 푸시 기기 목록 조회: userId={}", userId);
        return pushDeviceRepository.findAllByUserId(userId).stream()
                .map(PushDeviceResponse::from)
                .toList();
    }

    @Override
    public void unregisterDevice(String userId, String deviceId) {
        log.info("푸시 기기 등록 해제 요청: userId={}, deviceId={}", userId, deviceId);
        PushDevice device = pushDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new NoSuchElementException("기기가 존재하지 않습니다. id: " + deviceId));

        if (!device.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 기기만 삭제할 수 있습니다.");
        }

        pushDeviceRepository.deleteById(deviceId);
        log.info("푸시 기기 등록 해제 완료: deviceId={}", deviceId);
    }

    @Override
    public void deactivateDevice(String deviceId) {
        log.info("푸시 기기 비활성화 요청: deviceId={}", deviceId);
        pushDeviceRepository.findById(deviceId).ifPresent(device -> {
            device.deactivate();
            log.info("푸시 기기 비활성화 완료: deviceId={}", deviceId);
        });
    }

}
