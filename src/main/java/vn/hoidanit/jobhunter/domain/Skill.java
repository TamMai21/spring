package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.utils.SecurityUtils;

@Entity
@Table(name = "skills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
    @JsonIgnore
    private List<Job> jobs;

    @PrePersist
    public void handleCreatedAt() {
        this.createdBy = SecurityUtils.getCurrentUserLogin().isPresent() == true
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleUpdatedAt() {
        this.updatedBy = SecurityUtils.getCurrentUserLogin().isPresent() == true
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
