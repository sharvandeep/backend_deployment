package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.entity.BranchEntity;
import com.sharvan.careerassessment.service.BranchService;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public List<BranchEntity> getAllBranches() {
        return branchService.getAllBranches();
    }
}
