package com.example.rail.service;

import com.example.rail.dto.DelayInfoDto;
import com.example.rail.dto.RailStatusDto;

public interface RailOperationService {
    RailStatusDto getStatus(String lineName);
    DelayInfoDto getDelayInfo(String lineName);
}