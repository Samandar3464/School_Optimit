package com.example.service;

import com.example.entity.OverallReport;
import com.example.entity.StudentClass;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.enums.Months;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.OverallReportRequest;
import com.example.model.response.OverallReportResponse;
import com.example.repository.OverallReportRepository;
import com.example.repository.SalaryRepository;
import com.example.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OverallReportService implements BaseService<OverallReportRequest, Integer> {

    private final OverallReportRepository overallReportRepository;
    private final UserService userService;
    private final StudentClassRepository studentClassRepository;
    private final SalaryRepository salaryRepository;

    @Override
    public ApiResponse create(OverallReportRequest overallReportRequest) {
        User user = userService.getUserById(overallReportRequest.getUserId());
        OverallReport overallReport = getOverallReport(user);
        Optional<StudentClass> classLeader = studentClassRepository.findByClassLeaderIdAndActiveTrue(user.getId());
        classLeader.ifPresent(studentClass -> overallReport.setClassLeadership(studentClass.getClassName()));
        overallReport.setSalary(salaryRepository.findByUserPhoneNumberAndActiveTrue(user.getPhoneNumber()).orElseThrow(()->new RecordNotFoundException(Constants.SALARY_NOT_FOUND)));
        overallReportRepository.save(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, OverallReportResponse.toOverallResponse(overallReport));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        OverallReport overallReport = checkById(integer);
        OverallReportResponse overallReportResponse = OverallReportResponse.toOverallResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, overallReportResponse);
    }

    public ApiResponse getAll() {
        List<OverallReportResponse> allOverallResponse = OverallReportResponse.toAllOverallResponse(overallReportRepository.findAll());
        return new ApiResponse(Constants.SUCCESSFULLY, true, allOverallResponse);
    }

    public ApiResponse getByIdAndMonth(Integer integer, Months months) {
        OverallReport overallReport = overallReportRepository.findById(integer).orElseThrow(()->new RecordNotFoundException(Constants.OVERALL_REPORT_NOT_FOUND));
        OverallReportResponse overallReportResponse = OverallReportResponse.toOverallResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, overallReportResponse);
    }

    @Override
    public ApiResponse update(OverallReportRequest overallReportRequest) {
        checkById(overallReportRequest.getId());
        User user = userService.getUserById(overallReportRequest.getUserId());
        OverallReport overallReport = getOverallReport(user);
        overallReportRepository.save(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY, true, OverallReportResponse.toOverallResponse(overallReport));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        OverallReport overallReport = checkById(integer);
        overallReportRepository.deleteById(integer);
        return new ApiResponse(Constants.DELETED, true, OverallReportResponse.toOverallResponse(overallReport));
    }

    private OverallReport checkById(Integer integer) {
        return overallReportRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.OVERALL_REPORT_NOT_FOUND));
    }

    private OverallReport getOverallReport(User user) {
        try {
            return OverallReport
                    .builder()
                    .branch(user.getBranch())
                    .user(user)
                    .build();
        } catch (Exception e) {
            throw new RecordNotFoundException(Constants.SOMETHING_IS_WRONG);
        }
    }
}
