package com.example.rail.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rail.entity.RailLineMaster;
import com.example.rail.repository.RailLineMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RailLineMasterService {

    private final RailLineMasterRepository repository;

    public long count() {
        return repository.count();
    }

    public void saveAll(List<RailLineMaster> lines) {
        repository.saveAll(lines);
    }

    public Optional<RailLineMaster> findByRailCode(String railCode) {
        return repository.findByRailCode(railCode);
    }

    public List<RailLineMaster> findAll() {
        return repository.findAll();
    }
}
