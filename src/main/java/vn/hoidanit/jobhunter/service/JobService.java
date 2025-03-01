package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        if (job.getSkills() != null) {
            // lay danh sach Id tu Job
            List<Long> reqSKill = job.getSkills().stream().map(j -> j.getId()).collect(Collectors.toList());

            // loc nhung Id co ton tai trong db xong set cho job
            List<Skill> listSkills = this.skillRepository.findByIdIn(reqSKill);
            job.setSkills(listSkills);
        }

        Job jobCreated = this.jobRepository.save(job);

        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(jobCreated.getId());
        resCreateJobDTO.setName(jobCreated.getName());
        resCreateJobDTO.setSalary(jobCreated.getSalary());
        resCreateJobDTO.setQuantity(jobCreated.getQuantity());
        resCreateJobDTO.setLocation(jobCreated.getLocation());
        resCreateJobDTO.setLevelEnum(jobCreated.getLevelEnum());
        resCreateJobDTO.setStartDate(jobCreated.getStartDate());
        resCreateJobDTO.setEndDate(jobCreated.getEndDate());
        resCreateJobDTO.setActive(jobCreated.isActive());
        resCreateJobDTO.setCreatedAt(jobCreated.getCreatedAt());
        resCreateJobDTO.setUpdatedAt(jobCreated.getUpdatedAt());

        if (jobCreated.getSkills() != null) {
            List<String> skill = jobCreated.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skill);
        }

        return resCreateJobDTO;

    }

    public Job handleGetJobById(long id) {
        Optional<Job> jobFound = this.jobRepository.findById(id);
        return jobFound.isPresent() ? jobFound.get() : null;
    }

    public ResultPaginationDTO handleGetAllJobWithPaginate(Specification<Job> spec, Pageable pageable) {
        // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Page<Job> pageCompany = this.jobRepository.findAll(spec, pageable);
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

    public ResUpdateJobDTO handleUpdateJob(Job job) {
        if (job.getSkills() != null) {
            // lay danh sach Id tu Job
            List<Long> reqSKill = job.getSkills().stream().map(j -> j.getId()).collect(Collectors.toList());

            // loc nhung Id co ton tai trong db xong set cho job
            List<Skill> listSkills = this.skillRepository.findByIdIn(reqSKill);
            job.setSkills(listSkills);
        }

        // TODO: process PUT request
        Job jobToUpdate = this.handleGetJobById(job.getId());
        if (jobToUpdate != null) {
            Job jobCreated = this.jobRepository.save(jobToUpdate);
            ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
            resUpdateJobDTO.setId(jobCreated.getId());
            resUpdateJobDTO.setName(jobCreated.getName());
            resUpdateJobDTO.setSalary(jobCreated.getSalary());
            resUpdateJobDTO.setQuantity(jobCreated.getQuantity());
            resUpdateJobDTO.setLocation(jobCreated.getLocation());
            resUpdateJobDTO.setLevelEnum(jobCreated.getLevelEnum());
            resUpdateJobDTO.setStartDate(jobCreated.getStartDate());
            resUpdateJobDTO.setEndDate(jobCreated.getEndDate());
            resUpdateJobDTO.setActive(jobCreated.isActive());
            resUpdateJobDTO.setCreatedAt(jobCreated.getCreatedAt());
            resUpdateJobDTO.setUpdatedAt(jobCreated.getUpdatedAt());
        }
        return null;
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

}
