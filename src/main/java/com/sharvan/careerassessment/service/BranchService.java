package com.sharvan.careerassessment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sharvan.careerassessment.entity.BranchEntity;
import com.sharvan.careerassessment.repository.BranchRepository;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<BranchEntity> getAllBranches() {
        return branchRepository.findAll();
    }
}
