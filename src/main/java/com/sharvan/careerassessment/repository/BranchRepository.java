package com.sharvan.careerassessment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharvan.careerassessment.entity.BranchEntity;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {

    Optional<BranchEntity> findByName(String name);
}
