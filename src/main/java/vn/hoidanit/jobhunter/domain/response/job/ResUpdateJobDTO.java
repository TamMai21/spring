package vn.hoidanit.jobhunter.domain.response.job;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.utils.constant.LevelEnum;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;

    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum levelEnum;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private List<String> skills;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
