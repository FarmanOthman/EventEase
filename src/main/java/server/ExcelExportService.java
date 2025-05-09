package server;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Server-side service to export data to Excel format with analysis capability
 */
public class ExcelExportService {

  /**
   * Exports data to an Excel file with the provided filename
   * 
   * @param data        The data to export
   * @param filePath    The full path where to save the Excel file
   * @param sheetName   The name of the Excel sheet
   * @param columnNames The names of the columns
   * @return True if export was successful, false otherwise
   */
  public boolean exportToExcel(List<Map<String, Object>> data, String filePath,
      String sheetName, String[] columnNames) {
    if (data == null || data.isEmpty() || filePath == null || filePath.isEmpty()) {
      return false;
    }

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet(sheetName);

      // Create header styles
      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      Font headerFont = workbook.createFont();
      headerFont.setColor(IndexedColors.WHITE.getIndex());
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);

      // Create cell styles for data
      CellStyle dataCellStyle = workbook.createCellStyle();
      dataCellStyle.setBorderBottom(BorderStyle.THIN);
      dataCellStyle.setBorderTop(BorderStyle.THIN);
      dataCellStyle.setBorderLeft(BorderStyle.THIN);
      dataCellStyle.setBorderRight(BorderStyle.THIN);

      // Create header row
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < columnNames.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columnNames[i]);
        cell.setCellStyle(headerStyle);
      }

      // Create data rows
      int rowNum = 1;
      for (Map<String, Object> rowData : data) {
        Row row = sheet.createRow(rowNum++);

        for (int i = 0; i < columnNames.length; i++) {
          Cell cell = row.createCell(i);

          // Get the appropriate key for this column
          String key = getKeyForColumn(columnNames[i]);

          // Get and format the cell value
          Object value = rowData.get(key);
          if (value != null) {
            if (value instanceof Number) {
              cell.setCellValue(((Number) value).doubleValue());
            } else {
              String strValue = value.toString();
              // Remove currency symbol if present
              if (strValue.startsWith("$")) {
                strValue = strValue.substring(1);
              }
              cell.setCellValue(strValue);
            }
          }

          cell.setCellStyle(dataCellStyle);
        }
      }

      // Add analysis section
      addAnalysisSection(workbook, sheet, data, columnNames, rowNum);

      // Auto-size columns
      for (int i = 0; i < columnNames.length; i++) {
        sheet.autoSizeColumn(i);
      }

      // Write to file
      try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
        workbook.write(outputStream);
        return true;
      }

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Maps display column names to database column names
   */
  private String getKeyForColumn(String displayColumnName) {
    switch (displayColumnName) {
      case "Date":
        return "sale_date";
      case "Category":
        return "category";
      case "Tickets Sold":
        return "tickets_sold";
      case "Revenue ($)":
      case "Revenue":
        return "revenue";
      case "Team A":
        return "team_a";
      case "Team B":
        return "team_b";
      case "VIP Tickets":
        return "vip_tickets";
      case "Standard Tickets":
        return "standard_tickets";
      case "Premium Tickets":
        return "premium_tickets";
      default:
        return displayColumnName.toLowerCase().replace(" ", "_");
    }
  }

  /**
   * Adds an analysis section to the Excel sheet with summary data
   */
  private void addAnalysisSection(Workbook workbook, Sheet sheet,
      List<Map<String, Object>> data,
      String[] columnNames, int startRow) {
    // Skip a row
    startRow += 2;

    // Create analysis header
    CellStyle analysisHeaderStyle = workbook.createCellStyle();
    analysisHeaderStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
    analysisHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font analysisHeaderFont = workbook.createFont();
    analysisHeaderFont.setColor(IndexedColors.WHITE.getIndex());
    analysisHeaderFont.setBold(true);
    analysisHeaderStyle.setFont(analysisHeaderFont);

    Row analysisHeaderRow = sheet.createRow(startRow++);
    Cell analysisHeaderCell = analysisHeaderRow.createCell(0);
    analysisHeaderCell.setCellValue("Sales Analysis Summary");
    analysisHeaderCell.setCellStyle(analysisHeaderStyle);

    // Add total revenue calculation
    double totalRevenue = 0;
    int totalTickets = 0;

    for (Map<String, Object> rowData : data) {
      // Get revenue value (handling both strings with $ and numeric values)
      Object revenueObj = rowData.get("revenue");
      if (revenueObj != null) {
        if (revenueObj instanceof Number) {
          totalRevenue += ((Number) revenueObj).doubleValue();
        } else {
          String revenueStr = revenueObj.toString().replace("$", "");
          try {
            totalRevenue += Double.parseDouble(revenueStr);
          } catch (NumberFormatException e) {
            // Skip invalid values
          }
        }
      }

      // Get tickets sold
      Object ticketsObj = rowData.get("tickets_sold");
      if (ticketsObj != null) {
        if (ticketsObj instanceof Number) {
          totalTickets += ((Number) ticketsObj).intValue();
        } else {
          try {
            totalTickets += Integer.parseInt(ticketsObj.toString());
          } catch (NumberFormatException e) {
            // Skip invalid values
          }
        }
      }
    }

    // Add total revenue row
    Row totalRevenueRow = sheet.createRow(startRow++);
    totalRevenueRow.createCell(0).setCellValue("Total Revenue:");
    Cell totalRevenueCell = totalRevenueRow.createCell(1);
    totalRevenueCell.setCellValue(totalRevenue);

    // Add total tickets row
    Row totalTicketsRow = sheet.createRow(startRow++);
    totalTicketsRow.createCell(0).setCellValue("Total Tickets Sold:");
    Cell totalTicketsCell = totalTicketsRow.createCell(1);
    totalTicketsCell.setCellValue(totalTickets);

    // Add average revenue per ticket
    Row avgRevenueRow = sheet.createRow(startRow++);
    avgRevenueRow.createCell(0).setCellValue("Average Revenue per Ticket:");
    Cell avgRevenueCell = avgRevenueRow.createCell(1);
    double avgRevenue = totalTickets > 0 ? totalRevenue / totalTickets : 0;
    avgRevenueCell.setCellValue(avgRevenue);

    // Add a timestamp
    Row timestampRow = sheet.createRow(startRow + 1);
    timestampRow.createCell(0).setCellValue("Report Generated:");
    Cell timestampCell = timestampRow.createCell(1);
    timestampCell.setCellValue(new java.util.Date().toString());
  }
}