package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.utils.annotation.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResCreateJobDTO> postCreateSkill(@Valid @RequestBody Job job) {
        // TODO: process POST request
        ResCreateJobDTO jobCreated = this.jobService.handleCreateJob(job);
        return ResponseEntity.ok().body(jobCreated);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get job by id")
    public ResponseEntity<Job> getJobById(@PathVariable long id) {
        Job job = this.jobService.handleGetJobById(id);
        if (job != null) {
            return ResponseEntity.ok().body(job);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.handleGetAllJobWithPaginate(spec, pageable));
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResUpdateJobDTO> putUpdateJobs(@Valid @RequestBody Job job) throws IdInvalidException {
        // TODO: process PUT request
        if (this.jobService.handleGetJobById(job.getId()) == null) {
            throw new IdInvalidException("Cannot find this job");
        }
        ResUpdateJobDTO jobUpdated = this.jobService.handleUpdateJob(job);
        return ResponseEntity.ok().body(jobUpdated);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        if (this.jobService.handleGetJobById(id) == null) {
            throw new IdInvalidException("Cannot find this job");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

}
