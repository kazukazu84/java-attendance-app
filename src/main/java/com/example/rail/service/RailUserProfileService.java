package com.example.rail.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rail.entity.RailUserProfile;
import com.example.rail.repository.RailUserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RailUserProfileService {

    private final RailUserProfileRepository repository;

    public Optional<RailUserProfile> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public RailUserProfile save(RailUserProfile profile) {
        return repository.save(profile);
    }
}
