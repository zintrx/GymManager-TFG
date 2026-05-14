package com.gymmanager.backend.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public byte[] exportReport(List<?> data, Map<String, Object> parameters, String reportPath) throws JRException {
        // Load the JRXML template
        InputStream reportStream = getClass().getResourceAsStream(reportPath);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        
        // Fill the report with data
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
