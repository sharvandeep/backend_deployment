package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.BranchSkillDTO;
import com.sharvan.careerassessment.entity.BranchSkillEntity;
import com.sharvan.careerassessment.repository.BranchSkillRepository;

@RestController
@RequestMapping("/skills")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BranchSkillController {

    private final BranchSkillRepository repository;

    public BranchSkillController(BranchSkillRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{branchId}")
    public List<BranchSkillDTO> getSkillsByBranch(
            @PathVariable Long branchId
    ) {

        List<BranchSkillEntity> skills =
                repository.findByBranch_Id(branchId);

        return skills.stream().map(skill -> {

            BranchSkillDTO dto = new BranchSkillDTO();
            dto.setId(skill.getId());
            dto.setSkillName(skill.getSkillName());

            return dto;

        }).toList();
    }
}