package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.utils.SecurityUtils;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> handleGetAllCompanies() {
        return this.companyRepository.findAll();
    }

    public ResultPaginationDTO handleGetAllCompaniesWithPaginate(Specification<Company> spec, Pageable pageable) {
        // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta metaData = new Meta();

        metaData.setPage(pageable.getPageNumber() + 1);
        metaData.setPageSize(pageable.getPageSize());

        metaData.setPages(pageCompany.getTotalPages());
        metaData.setTotal(pageCompany.getTotalElements());

        rs.setMeta(metaData);
        rs.setResult(pageCompany.getContent());

        return rs;
    }

    // public ResultPaginationDTO handleGetAllCompaniesWithPaginate(Pageable
    // pageable) {
    // // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
    // Page<Company> pageCompany = this.companyRepository.findAll(pageable);
    // ResultPaginationDTO rs = new ResultPaginationDTO();
    // Meta metaData = new Meta();

    // metaData.setPage(pageCompany.getNumber() + 1);
    // metaData.setPageSize(pageCompany.getSize());

    // metaData.setPages(pageCompany.getTotalPages());
    // metaData.setTotal(pageCompany.getTotalElements());

    // rs.setMeta(metaData);
    // rs.setResult(pageCompany.getContent());

    // return rs;
    // }

    public Company handleGetCompanyByID(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        return companyOptional.isPresent() ? companyOptional.get() : null;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Company handleUpdateCompany(Company company) {
        Company companyToUpdate = this.handleGetCompanyByID(company.getId());
        if (companyToUpdate != null) {
            companyToUpdate.setAddress(company.getAddress());
            companyToUpdate.setLogo(company.getLogo());
            companyToUpdate.setName(company.getName());
            companyToUpdate.setDescription(company.getDescription());

            return this.companyRepository.save(companyToUpdate);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

}
