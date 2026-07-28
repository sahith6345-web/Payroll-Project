package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.dto.PayrollDto;
import com.payroll.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PayrollDto.PayrollSummaryResponse>>> getPayrollHistory() {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getAllPayrolls()));
    }

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'PAYROLL_MANAGER')")
    public ResponseEntity<ApiResponse<PayrollDto.PayrollSummaryResponse>> processPayroll(@Valid @RequestBody PayrollDto.ProcessRequest request) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.processMonthlyPayroll(request), "Payroll calculated and processed successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'PAYROLL_MANAGER')")
    public ResponseEntity<ApiResponse<PayrollDto.PayrollSummaryResponse>> generatePayroll(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestBody(required = false) PayrollDto.ProcessRequest requestBody
    ) {
        int m = (month != null) ? month : (requestBody != null ? requestBody.getMonth() : 7);
        int y = (year != null) ? year : (requestBody != null ? requestBody.getYear() : 2026);

        PayrollDto.ProcessRequest request = PayrollDto.ProcessRequest.builder()
                .month(m)
                .year(y)
                .build();

        return ResponseEntity.ok(ApiResponse.success(payrollService.processMonthlyPayroll(request), "Payroll calculated and processed successfully"));
    }

    @GetMapping("/{payrollId}/payslips")
    public ResponseEntity<ApiResponse<List<PayrollDto.PayslipResponse>>> getPayslips(@PathVariable String payrollId) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getPayslipsForPayroll(payrollId)));
    }

    @GetMapping("/{payrollId}/items")
    public ResponseEntity<ApiResponse<List<PayrollDto.PayslipResponse>>> getItems(@PathVariable String payrollId) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getPayslipsForPayroll(payrollId)));
    }

    @GetMapping("/employee/{employeeId}/payslips")
    public ResponseEntity<ApiResponse<List<PayrollDto.PayslipResponse>>> getEmployeePayslips(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getPayslipsForEmployee(employeeId)));
    }

    @GetMapping("/payslips/{payslipId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String payslipId) throws IOException {
        byte[] pdfBytes = payrollService.downloadPayslipPdf(payslipId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payslip-" + payslipId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
