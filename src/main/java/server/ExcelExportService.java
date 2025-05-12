package server;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

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

      // Create styles
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataCellStyle = createDataCellStyle(workbook);
      CellStyle dateStyle = createDateStyle(workbook);
      CellStyle currencyStyle = createCurrencyStyle(workbook);

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
          String columnName = columnNames[i].toLowerCase().replace(" ", "_");
          Object value = rowData.get(columnName);

          // Format cell based on value type and column name
          formatCell(cell, value, columnName, dateStyle, currencyStyle, dataCellStyle);
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

  private CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font headerFont = workbook.createFont();
    headerFont.setColor(IndexedColors.WHITE.getIndex());
    headerFont.setBold(true);
    style.setFont(headerFont);
    return style;
  }

  private CellStyle createDataCellStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    return style;
  }

  private CellStyle createDateStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.cloneStyleFrom(createDataCellStyle(workbook));
    style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
    return style;
  }

  private CellStyle createCurrencyStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.cloneStyleFrom(createDataCellStyle(workbook));
    style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
    return style;
  }

  private void formatCell(Cell cell, Object value, String columnName, CellStyle dateStyle,
      CellStyle currencyStyle, CellStyle defaultStyle) {
    if (value == null) {
      cell.setCellValue("");
      cell.setCellStyle(defaultStyle);
      return;
    }

    // Handle date fields
    if (columnName.contains("date") || columnName.endsWith("_at")) {
      if (value instanceof Date) {
        cell.setCellValue((Date) value);
      } else if (value instanceof String) {
        try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          Date date = sdf.parse((String) value);
          cell.setCellValue(date);
        } catch (Exception e) {
          cell.setCellValue(value.toString());
        }
      }
      cell.setCellStyle(dateStyle);
      return;
    }

    // Handle numeric fields
    if (value instanceof Number) {
      double numValue = ((Number) value).doubleValue();
      // Use currency format for price, revenue, or amount fields
      if (columnName.contains("price") || columnName.contains("revenue") ||
          columnName.contains("amount")) {
        cell.setCellValue(numValue);
        cell.setCellStyle(currencyStyle);
      } else {
        cell.setCellValue(numValue);
        cell.setCellStyle(defaultStyle);
      }
      return;
    }

    // Handle description fields - ensure they're not truncated
    if (columnName.contains("description")) {
      String strValue = value.toString();
      cell.setCellValue(strValue);
      // If description is long, adjust row height
      if (strValue.length() > 50) {
        int numberOfLines = strValue.length() / 50 + 1;
        cell.getRow().setHeight((short) (numberOfLines * 255));
      }
      CellStyle wrapStyle = cell.getSheet().getWorkbook().createCellStyle();
      wrapStyle.cloneStyleFrom(defaultStyle);
      wrapStyle.setWrapText(true);
      cell.setCellStyle(wrapStyle);
      return;
    }

    // Default handling for other types
    cell.setCellValue(value.toString());
    cell.setCellStyle(defaultStyle);
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