package com.example.rail.service;

import com.example.rail.dto.CertificateDto;
import com.example.rail.dto.DelayInfoDto;

public interface DelayCertificateService {
    CertificateDto generateCertificate(DelayInfoDto delayInfo, String userId);
}