package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.utils.constant.Gender;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUpdatedUserDTO {
    private long id;
    private String name;
    private String email;
    private Gender gender;

    private String address;
    private int age;
    private Instant updatedAt;

}
