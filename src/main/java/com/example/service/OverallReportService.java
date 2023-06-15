package com.example.service;

import com.example.entity.OverallReport;
import com.example.entity.Salary;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.enums.Months;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.OverallReportRequest;
import com.example.model.response.OverallReportResponse;
import com.example.repository.OverallReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OverallReportService implements BaseService<OverallReportRequest,Integer>{

    private final OverallReportRepository overallReportRepository;
    private final UserService userService;

    @Override
    public ApiResponse create(OverallReportRequest overallReportRequest) {
        User user = userService.checkUserExistById(overallReportRequest.getUserId());
        OverallReport overallReport = getOverallReport(user);
        overallReportRepository.save(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY,true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        OverallReport overallReport = checkById(integer);
        OverallReportResponse overallReportResponse = toOverallResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY,true,overallReportResponse);
    }

    public ApiResponse getByIdAndMonth(Integer integer, Months months) {
        OverallReport overallReport = checkById(integer);
        OverallReportResponse overallReportResponse = toOverallResponse(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY,true,overallReportResponse);
    }

    @Override
    public ApiResponse update(OverallReportRequest overallReportRequest) {
        checkById(overallReportRequest.getId());
        User user = userService.checkUserExistById(overallReportRequest.getUserId());
        OverallReport overallReport = getOverallReport(user);
        overallReportRepository.save(overallReport);
        return new ApiResponse(Constants.SUCCESSFULLY,true,toOverallResponse(overallReport));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        OverallReport overallReport = checkById(integer);
        overallReportRepository.deleteById(integer);
        return new ApiResponse(Constants.DELETED,true,toOverallResponse(overallReport));
    }

    private OverallReport checkById(Integer integer) {
        return  overallReportRepository.findById(integer).orElseThrow(()->new RecordNotFoundException(Constants.OVERALL_REPORT_NOT_FOUND));
    }

    private  OverallReport getOverallReport(User user) {
        OverallReport overallReport = new OverallReport();
        overallReport.setBranch(user.getBranch());
        List<Salary> salaries = user.getSalaries();
        for (Salary salary : salaries) {
            if (salary.getMonth().equals(overallReport.getMonth())) {
                overallReport.setSalary(salary);
            }
        }
        overallReport.setUser(user);
        overallReport.setPosition(user.getPosition());
        overallReport.setClassLeadership(user.getStudentClass().getClassName());
        overallReport.setTeachingHours(user.getTeachingHours());
        return overallReport;
    }

    private OverallReportResponse toOverallResponse(OverallReport overallReport) {
        return OverallReportResponse
                .builder()
                .id(overallReport.getId())
                .classLeadership(overallReport.getClassLeadership())
                .branch(overallReport.getBranch())
                .teachingHours(overallReport.getTeachingHours())
                .position(overallReport.getPosition())
                .salary(overallReport.getSalary())
                .fullName(overallReport.getUser().getFullName())
                .build();
    }
}
