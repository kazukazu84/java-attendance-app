package com.example.rail.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rail.dto.CertificateDto;
import com.example.rail.dto.DelayInfoDto;
import com.example.rail.entity.DelayCertificateEntity;
import com.example.rail.entity.DelayInfoEntity;
import com.example.rail.entity.RailLineEntity;
import com.example.rail.repository.DelayCertificateRepository;
import com.example.rail.repository.DelayInfoRepository;
import com.example.rail.repository.RailLineRepository;
import com.example.rail.service.DelayCertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DelayCertificateServiceImpl implements DelayCertificateService {

    private final RailLineRepository railLineRepository;
    private final DelayInfoRepository delayInfoRepository;
    private final DelayCertificateRepository delayCertificateRepository;

    @Override
    @Transactional
    public CertificateDto generateCertificate(DelayInfoDto delayInfo, String userId) {

        String certificateId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        // ★ 路線データの取得または保存（最新仕様：statusText をそのまま保存）
        RailLineEntity railLine = railLineRepository.findByLineName(delayInfo.getLineName())
                .orElseGet(() -> railLineRepository.save(
                        RailLineEntity.builder()
                                .company("Yahoo!路線情報")
                                .lineName(delayInfo.getLineName())
                                .statusText(delayInfo.getStatusText())   // ← そのまま保存
                                .detailText(delayInfo.getDetailText())   // ← そのまま保存
                                .updatedText(delayInfo.getUpdatedText()) // ← そのまま保存
                                .lastUpdated(now)
                                .build()
                ));

        // ★ 遅延情報の保存（最新仕様：そのまま保存）
        DelayInfoEntity delayInfoEntity = DelayInfoEntity.builder()
                .railLine(railLine)
                .delayMinutes(delayInfo.getDelayMinutes())
                .reason(delayInfo.getReason())          // ← そのまま保存
                .occurredAt(delayInfo.getOccurredAt())
                .build();
        delayInfoRepository.save(delayInfoEntity);

        // ★ 証明書エンティティの生成・保存
        DelayCertificateEntity certificateEntity = DelayCertificateEntity.builder()
                .certificateId(certificateId)
                .railLine(railLine)
                .delayInfo(delayInfoEntity)
                .issuedAt(now)
                .issuedToUserId(userId)
                .build();
        delayCertificateRepository.save(certificateEntity);

        // ★ CertificateDto にマッピングして返却（そのまま返す）
        return CertificateDto.builder()
                .certificateId(certificateId)
                .lineName(delayInfo.getLineName())
                .delayMinutes(delayInfo.getDelayMinutes())
                .detailText(delayInfo.getDetailText())          // ← そのまま返す
                .statusText(delayInfo.getStatusText())  // ← そのまま返す
                .updatedText(delayInfo.getUpdatedText())// ← そのまま返す
                .issuedToUserId(userId)
                .issuedAt(now)
                .build();
    }
}
