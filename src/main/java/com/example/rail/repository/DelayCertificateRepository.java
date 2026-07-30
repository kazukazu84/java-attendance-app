package com.example.rail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rail.entity.DelayCertificateEntity;

@Repository
public interface DelayCertificateRepository extends JpaRepository<DelayCertificateEntity, String> {
}