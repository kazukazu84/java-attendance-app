package com.example.rail.service;

import com.example.rail.dto.DelayInfoDto;
import com.example.rail.dto.RailStatusDto;

public interface RailFetcher {
    RailStatusDto fetchStatus(String lineName);
    DelayInfoDto fetchDelayInfo(String lineName);
}