package com.sharvan.careerassessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharvan.careerassessment.entity.*;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByRoleAndBranch_Id(String role, Long branchId);

    long countByRole(Role role);

    List<UserEntity> findByRoleAndBranch(Role role, BranchEntity branch);

    long countByAssignedFaculty(UserEntity faculty);
}
