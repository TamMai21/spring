package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleGetSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        return skill.isPresent() ? skill.get() : null;
    }

    public ResultPaginationDTO handleGetAllSkillWithPaginate(Specification<Skill> spec, Pageable pageable) {
        // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Page<Skill> pageCompany = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta metaData = new ResultPaginationDTO.Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageCompany.getTotalPages());
        metaData.setTotal(pageCompany.getTotalElements());

        rs.setMeta(metaData);
        rs.setResult(pageCompany.getContent());

        return rs;
    }

    public Skill handleUpdateSkill(Skill skill) {
        Skill skillToUpdate = this.handleGetSkillById(skill.getId());
        if (skillToUpdate != null) {
            skillToUpdate.setName(skill.getName());
            return this.skillRepository.save(skillToUpdate);
        }
        return null;
    }

    public void handleDeleteSkill(long id) {
        // xoa het data trong job_skill (vi trong entity Skill dc dinh nghia mappedBy
        // nen ko tu dong xoa data bang join dc)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));// xoa di skill tuong ung trong
                                                                                    // job(job chua list gom nhieu
                                                                                    // skill khac nhau)

        // xoa entity skill trong db
        this.skillRepository.delete(currentSkill);
    }

}
