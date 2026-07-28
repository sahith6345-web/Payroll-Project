package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.entity.PayrollItem;
import com.payroll.pdf.PayslipPdfGenerator;
import com.payroll.repository.PayrollItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PayrollItemRepository payrollItemRepository;

    @GetMapping("/payslip/{payrollItemId}/pdf")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable String payrollItemId) throws IOException {
        PayrollItem item = payrollItemRepository.findById(payrollItemId)
                .orElseThrow(() -> new RuntimeException("Payroll record not found for id: " + payrollItemId));

        byte[] pdfBytes = PayslipPdfGenerator.generatePayslipPdf(
                item,
                "ENTERPRISE HRMS & PAYROLL",
                "100 Innovation Way, Tech Park, Suite 400"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Payslip_" + item.getEmployeeCode() + "_" + item.getMonth() + "_" + item.getYear() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
