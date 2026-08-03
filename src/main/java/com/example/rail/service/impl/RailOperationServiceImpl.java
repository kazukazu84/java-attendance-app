package com.example.rail.service.impl;

import org.springframework.stereotype.Service;

import com.example.rail.dto.RailStatusDto;
import com.example.rail.service.RailFetcher;
import com.example.rail.service.RailOperationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RailOperationServiceImpl implements RailOperationService {

    private final RailFetcher railFetcher;

    @Override
    public RailStatusDto getStatus(String lineName) {
        return railFetcher.fetchStatus(lineName);
    }

}