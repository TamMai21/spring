package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResCreatedUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResGetUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdatedUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> handleFetchAllUsers() {
        return userRepository.findAll();
    }

    public ResultPaginationDTO handleGetAllUsersWithPaginate(Specification<User> spec, Pageable pageable) {
        // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta metaData = new Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageUsers.getTotalPages());
        metaData.setTotal(pageUsers.getTotalElements());

        rs.setMeta(metaData);

        List<ResGetUserDTO> listUserDTOs = pageUsers.getContent().stream()
                .map(user -> new ResGetUserDTO(user.getId(), user.getName(), user.getEmail(), user.getGender(),
                        user.getAddress(),
                        user.getAge(), user.getCreatedAt(), user.getUpdatedAt()))
                .collect(Collectors.toList());
        rs.setResult(listUserDTOs);
        return rs;
    }

    public User handleFetchUserByID(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.isPresent() ? userOptional.get() : null;
    }

    public User handleGetUserByUsername(String email) {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        return userOptional.isPresent() ? userOptional.get() : null;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleCreateUser(User user) {
        boolean isExist = this.userRepository.existsByEmail(user.getEmail());
        return isExist != true ? this.userRepository.save(user) : null;
    }

    public User handleUpdateUser(User user) {
        User userToUpdate = handleFetchUserByID(user.getId());
        if (userToUpdate != null) {
            userToUpdate.setName(user.getName());
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setAge(user.getAge());
            userToUpdate.setGender(user.getGender());

            return this.userRepository.save(userToUpdate);
        }

        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public ResCreatedUserDTO convertToResCreatedUserDTO(User user) {
        ResCreatedUserDTO resUserDTO = new ResCreatedUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmai(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());

        return resUserDTO;
    }

    public ResUpdatedUserDTO convertResUpdatedUserDTO(User user) {
        ResUpdatedUserDTO resUserDTO = new ResUpdatedUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        return resUserDTO;
    }

    public ResGetUserDTO convertResGetUserDTO(User user) {
        ResGetUserDTO resUserDTO = new ResGetUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUserDTO.setCreatedAt(user.getCreatedAt());

        return resUserDTO;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

}
