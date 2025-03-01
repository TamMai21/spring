package vn.hoidanit.jobhunter.repository;

import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<vn.hoidanit.jobhunter.domain.Job, Long>,
        JpaSpecificationExecutor<vn.hoidanit.jobhunter.domain.Job> {

}
