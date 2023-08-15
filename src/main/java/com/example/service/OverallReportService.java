package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.OverallReportRequest;
import com.example.model.response.OverallReportResponse;
import com.example.model.response.OverallReportResponsePage;
import com.example.model.response.SalaryResponse;
import com.example.model.response.UserResponse;
import com.example.repository.BranchRepository;
import com.example.repository.OverallReportRepository;
import com.example.repository.SalaryRepository;
import com.example.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OverallReportService implements BaseService<OverallReportRequest, Integer> {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final OverallReportRepository overallReportRepository;
    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryService salaryService;

    @Override
    public ApiResponse create(OverallReportRequest overallReportRequest) {
        OverallReport overallReport = modelMapper.map(overallReportRequest, OverallReport.class);
        setOverallReport(overallReportRequest, overallReport);
        overallReportRepository.save(overallReport);
        OverallReportResponse response = getOverallReportResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        OverallReport overallReport = checkById(integer);
        OverallReportResponse overallReportResponse = getOverallReportResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, overallReportResponse);
    }

    public ApiResponse getAllByDate(LocalDate date, int page, int size) {
        Page<OverallReport> all = overallReportRepository.findAllByDate(date, PageRequest.of(page, size));
        OverallReportResponsePage responses = getOverallReportResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        Page<OverallReport> all = overallReportRepository.findAllByBranch_Id(branchId, PageRequest.of(page, size));
        OverallReportResponsePage responses = getOverallReportResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }


    @Override
    public ApiResponse update(OverallReportRequest overallReportRequest) {
        OverallReport overallReport = checkById(overallReportRequest.getId());
        setOverallReport(overallReportRequest, overallReport);
        overallReportRepository.save(overallReport);
        OverallReportResponse response = getOverallReportResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        OverallReport overallReport = checkById(integer);
        overallReportRepository.deleteById(integer);
        OverallReportResponse response = getOverallReportResponse(overallReport);
        return new ApiResponse(Constants.DELETED, true, response);
    }

    private OverallReport checkById(Integer integer) {
        return overallReportRepository.findById(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.OVERALL_REPORT_NOT_FOUND));
    }

    private void setOverallReport(OverallReportRequest overallReportRequest, OverallReport overallReport) {
        User user = userService.getUserById(overallReportRequest.getUserId());
        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(user.getPhoneNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        Branch branch = branchRepository.findByIdAndDeleteFalse(overallReportRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        Optional<StudentClass> studentClassOptional =
                studentClassRepository.findByClassLeaderIdAndActiveTrue(user.getId());

        studentClassOptional.ifPresent(studentClass -> overallReport.setClassLeadership(studentClass.getClassName()));
        overallReport.setBranch(branch);
        overallReport.setSalary(salary);
        overallReport.setUser(user);
    }


    private OverallReportResponse getOverallReportResponse(OverallReport overallReport) {
        OverallReportResponse response = modelMapper.map(overallReport, OverallReportResponse.class);
        UserResponse userResponse = userService.getResponse(overallReport.getUser());
        SalaryResponse salaryResponse = salaryService.getResponse(overallReport.getSalary());
        response.setSalary(salaryResponse);
        response.setUserResponse(userResponse);
        response.setDate(overallReport.getDate().toString());
        return response;
    }


    private OverallReportResponsePage getOverallReportResponses(Page<OverallReport> all) {
        OverallReportResponsePage overallReportResponsePage = new OverallReportResponsePage();
        List<OverallReportResponse> allOverallResponse = new ArrayList<>();
        all.forEach(overallReport -> {
            OverallReportResponse overallReportResponse = getOverallReportResponse(overallReport);
            allOverallResponse.add(overallReportResponse);
        });
        overallReportResponsePage.setOverallReportResponses(allOverallResponse);
        overallReportResponsePage.setTotalPage(all.getTotalPages());
        overallReportResponsePage.setTotalElement(all.getTotalElements());
        return overallReportResponsePage;
    }
}
