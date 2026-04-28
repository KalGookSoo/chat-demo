package kr.me.seesaw.service;

import kr.me.seesaw.domain.dto.PushDeviceRegisterRequest;
import kr.me.seesaw.domain.dto.PushDeviceResponse;

import java.util.List;

public interface PushDeviceService {

    /**
     * 푸시 기기 등록 또는 갱신
     */
    PushDeviceResponse registerDevice(String userId, PushDeviceRegisterRequest request);

    /**
     * 내 푸시 기기 목록 조회
     */
    List<PushDeviceResponse> getMyDevices(String userId);

    /**
     * 푸시 기기 삭제 (등록 해제)
     */
    void unregisterDevice(String userId, String deviceId);

    /**
     * 푸시 기기 비활성화 (발송 실패 시 등)
     */
    void deactivateDevice(String deviceId);

}
