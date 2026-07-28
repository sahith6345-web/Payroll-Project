package com.payroll.pdf;

import com.payroll.entity.PayrollItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PayslipPdfGenerator {

    public static byte[] generatePayslipPdf(PayrollItem item, String companyName, String companyAddress) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream stream = new PDPageContentStream(document, page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // Title Header
            stream.beginText();
            stream.setFont(fontBold, 20);
            stream.newLineAtOffset(50, 750);
            stream.showText(companyName != null ? companyName : "ENTERPRISE HRMS & PAYROLL");
            stream.endText();

            stream.beginText();
            stream.setFont(fontRegular, 10);
            stream.newLineAtOffset(50, 735);
            stream.showText(companyAddress != null ? companyAddress : "100 Innovation Way, Tech Park, Suite 400");
            stream.endText();

            // Divider Line
            stream.setLineWidth(1.5f);
            stream.moveTo(50, 720);
            stream.lineTo(550, 720);
            stream.stroke();

            // Payslip Subheader
            stream.beginText();
            stream.setFont(fontBold, 14);
            stream.newLineAtOffset(50, 695);
            stream.showText("SALARY SLIP FOR " + item.getMonth() + "/" + item.getYear());
            stream.endText();

            // Employee Details Table Box
            stream.beginText();
            stream.setFont(fontBold, 10);
            stream.newLineAtOffset(50, 665);
            stream.showText("Employee Code: ");
            stream.setFont(fontRegular, 10);
            stream.showText(item.getEmployeeCode() != null ? item.getEmployeeCode() : "N/A");

            stream.newLineAtOffset(250, 0);
            stream.setFont(fontBold, 10);
            stream.showText("Employee Name: ");
            stream.setFont(fontRegular, 10);
            stream.showText(item.getEmployeeName() != null ? item.getEmployeeName() : "N/A");
            stream.endText();

            stream.beginText();
            stream.setFont(fontBold, 10);
            stream.newLineAtOffset(50, 645);
            stream.showText("Department: ");
            stream.setFont(fontRegular, 10);
            stream.showText(item.getDepartmentName() != null ? item.getDepartmentName() : "General");

            stream.newLineAtOffset(250, 0);
            stream.setFont(fontBold, 10);
            stream.showText("Designation: ");
            stream.setFont(fontRegular, 10);
            stream.showText(item.getDesignationTitle() != null ? item.getDesignationTitle() : "Employee");
            stream.endText();

            // Earnings vs Deductions Headers
            float y = 600;
            stream.setLineWidth(1f);
            stream.moveTo(50, y);
            stream.lineTo(550, y);
            stream.stroke();

            stream.beginText();
            stream.setFont(fontBold, 11);
            stream.newLineAtOffset(55, y - 15);
            stream.showText("EARNINGS");
            stream.newLineAtOffset(180, 0);
            stream.showText("AMOUNT ($)");
            stream.newLineAtOffset(100, 0);
            stream.showText("DEDUCTIONS");
            stream.newLineAtOffset(140, 0);
            stream.showText("AMOUNT ($)");
            stream.endText();

            y -= 25;
            stream.moveTo(50, y);
            stream.lineTo(550, y);
            stream.stroke();

            // Content Rows
            y -= 20;
            writeRow(stream, fontRegular, "Basic Salary", String.valueOf(item.getBasicSalary()), "Provident Fund (PF)", String.valueOf(item.getPfDeduction()), y);
            y -= 20;
            writeRow(stream, fontRegular, "House Rent Allowance", String.valueOf(item.getHra()), "ESI Contribution", String.valueOf(item.getEsiDeduction()), y);
            y -= 20;
            writeRow(stream, fontRegular, "Dearness Allowance", String.valueOf(item.getDa()), "Professional Tax", String.valueOf(item.getProfessionalTax()), y);
            y -= 20;
            writeRow(stream, fontRegular, "Special Allowance", String.valueOf(item.getSpecialAllowance()), "Income Tax (TDS)", String.valueOf(item.getIncomeTaxTds()), y);
            y -= 20;
            writeRow(stream, fontRegular, "Overtime Pay", String.valueOf(item.getOvertimePay()), "Loan EMI", String.valueOf(item.getLoanEmiDeduction()), y);
            y -= 20;
            writeRow(stream, fontRegular, "Bonus / Incentives", String.valueOf(item.getBonus()), "Unpaid Leave Penalty", String.valueOf(item.getUnpaidLeaveDeduction()), y);

            y -= 25;
            stream.moveTo(50, y);
            stream.lineTo(550, y);
            stream.stroke();

            // Totals
            y -= 20;
            writeRow(stream, fontBold, "TOTAL GROSS", String.valueOf(item.getGrossSalary()), "TOTAL DEDUCTIONS", String.valueOf(item.getTotalDeductions()), y);

            y -= 30;
            // Net Salary Highlight Box
            stream.beginText();
            stream.setFont(fontBold, 14);
            stream.newLineAtOffset(50, y);
            stream.showText("NET SALARY PAYABLE: $" + item.getNetSalary());
            stream.endText();

            // Footer
            stream.beginText();
            stream.setFont(fontRegular, 8);
            stream.newLineAtOffset(50, 50);
            stream.showText("This is a computer-generated document and does not require a physical signature.");
            stream.endText();

            stream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private static void writeRow(PDPageContentStream stream, PDType1Font font, String eLabel, String eVal, String dLabel, String dVal, float y) throws IOException {
        stream.beginText();
        stream.setFont(font, 9);
        stream.newLineAtOffset(55, y);
        stream.showText(eLabel);
        stream.newLineAtOffset(180, 0);
        stream.showText(eVal != null ? eVal : "0.00");
        stream.newLineAtOffset(100, 0);
        stream.showText(dLabel);
        stream.newLineAtOffset(140, 0);
        stream.showText(dVal != null ? dVal : "0.00");
        stream.endText();
    }
}
