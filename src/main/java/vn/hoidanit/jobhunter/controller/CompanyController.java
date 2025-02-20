package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.utils.annotation.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.EmptyFieldException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // @GetMapping("/companies/page")
    // public ResponseEntity<ResultPaginationDTO> getAllCompany(
    // // @Filter Specification<Company> spec
    // @RequestParam("current") Optional<String> currentOptional,
    // @RequestParam("pageSize") Optional<String> pageSizeOptional) {
    // String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
    // String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() :
    // "";
    // Pageable pageable = PageRequest.of(Integer.parseInt(sCurrent) - 1,
    // Integer.parseInt(sPageSize));

    // return ResponseEntity.status(HttpStatus.CREATED)
    // .body(this.companyService.handleGetAllCompaniesWithPaginate(pageable));
    // // return ResponseEntity.status(HttpStatus.CREATED)
    // // .body(this.companyService.handleGetAllCompaniesWithPaginate(spec));
    // }

    @GetMapping("/companies/pages")
    @ApiMessage("fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.companyService.handleGetAllCompaniesWithPaginate(spec, pageable));
    }

    @GetMapping("/companies/all")
    public ResponseEntity<List<Company>> getAllCompanyWithPaginate() {
        // TODO: process POST request
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleGetAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyByID(@PathVariable long id) {
        // TODO: process POST request
        Company companyFound = this.companyService.handleGetCompanyByID(id);
        if (companyFound == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(companyFound);
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> postCreateCompany(@Valid @RequestBody Company company) {
        // TODO: process POST request
        Company companyCreated = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyCreated);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> putUpdateCompany(@Valid @RequestBody Company company) {
        // TODO: process POST request
        Company companyUpdated = this.companyService.handleUpdateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(companyUpdated);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompanyByID(@PathVariable long id) {
        // TODO: process POST request
        return ResponseEntity.status(HttpStatus.CREATED).body(id + " deleted");
    }

}
