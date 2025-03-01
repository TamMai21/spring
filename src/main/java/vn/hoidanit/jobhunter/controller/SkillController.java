package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.utils.annotation.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Get skill by id")
    public ResponseEntity<Skill> geSkillById(@PathVariable long id) {
        Skill skillFound = this.skillService.handleGetSkillById(id);
        if (skillFound == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(skillFound);
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok(this.skillService.handleGetAllSkillWithPaginate(spec, pageable));
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> postCreateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // TODO: process POST request
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("SKill " + skill.getName() + " is already exists");
        }
        Skill skillCreated = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.ok().body(skillCreated);
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> putUpdateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // TODO: process PUT request
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("SKill " + skill.getName() + " is already exists");
        }
        Skill skillUpdated = this.skillService.handleUpdateSkill(skill);
        return ResponseEntity.ok().body(skillUpdated);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable long id) throws IdInvalidException {
        if (this.skillService.handleGetSkillById(id) == null) {
            throw new IdInvalidException("Cannot delete skill with id = " + id + " because it's not exists.");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
