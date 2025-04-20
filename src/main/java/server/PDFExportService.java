package server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Service for exporting data to PDF format
 */
public class PDFExportService {
  private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
  private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
  private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
  private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);

  /**
   * Exports data to a PDF file
   * 
   * @param data        Data to export as a list of maps
   * @param filePath    Path where to save the PDF
   * @param title       Title of the document
   * @param columnNames Column headers for the table
   * @return True if export was successful, false otherwise
   */
  public boolean exportToPDF(List<Map<String, Object>> data, String filePath,
      String title, String[] columnNames) {
    if (data == null || filePath == null || title == null || columnNames == null) {
      return false;
    }

    Document document = new Document();
    try {
      PdfWriter.getInstance(document, new FileOutputStream(filePath));
      document.open();

      // Add title
      Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
      titleParagraph.setAlignment(Element.ALIGN_CENTER);
      titleParagraph.setSpacingAfter(20);
      document.add(titleParagraph);

      // Add date
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
      Paragraph dateParagraph = new Paragraph("Generated on: " + dateFormat.format(new Date()), NORMAL_FONT);
      dateParagraph.setAlignment(Element.ALIGN_RIGHT);
      dateParagraph.setSpacingAfter(20);
      document.add(dateParagraph);

      // Add table with data
      PdfPTable table = createDataTable(data, columnNames);
      document.add(table);

      // Add summary section if data exists
      if (!data.isEmpty()) {
        document.add(new Paragraph("\n"));
        document.add(createSummarySection(data));
      }

      // Add footer
      Paragraph footer = new Paragraph("This is an automated export from the Event Management System", FOOTER_FONT);
      footer.setAlignment(Element.ALIGN_CENTER);
      document.add(footer);

      return true;
    } catch (DocumentException | IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      document.close();
    }
  }

  /**
   * Creates a table with the data
   */
  private PdfPTable createDataTable(List<Map<String, Object>> data, String[] columnNames) throws DocumentException {
    PdfPTable table = new PdfPTable(columnNames.length);
    table.setWidthPercentage(100);

    // Add header row
    for (String columnName : columnNames) {
      PdfPCell headerCell = new PdfPCell(new Phrase(columnName, HEADER_FONT));
      headerCell.setBackgroundColor(new BaseColor(64, 133, 219));
      headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      headerCell.setPadding(5);
      table.addCell(headerCell);
    }

    // Add data rows
    for (Map<String, Object> rowData : data) {
      for (String columnName : columnNames) {
        // Map display column name to database column name
        String dbColumnName = mapColumnName(columnName);
        Object value = rowData.get(dbColumnName);

        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", NORMAL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        table.addCell(cell);
      }
    }

    return table;
  }

  /**
   * Maps display column names to database column names
   */
  private String mapColumnName(String displayColumnName) {
    switch (displayColumnName) {
      case "Date":
        return "event_date";
      case "Team A":
        return "team_a";
      case "Team B":
        return "team_b";
      case "Tickets Sold":
        return "total_ticket_sold";
      case "Revenue":
        return "total_revenue";
      case "VIP Tickets":
        return "vip_tickets";
      case "Standard Tickets":
        return "standard_tickets";
      default:
        return displayColumnName.toLowerCase().replace(" ", "_");
    }
  }

  /**
   * Creates a summary section with totals and averages
   */
  private PdfPTable createSummarySection(List<Map<String, Object>> data) throws DocumentException {
    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidthPercentage(50);
    summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

    // Add header
    PdfPCell headerCell = new PdfPCell(new Phrase("Summary", HEADER_FONT));
    headerCell.setBackgroundColor(new BaseColor(64, 133, 219));
    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    headerCell.setColspan(2);
    headerCell.setPadding(5);
    summaryTable.addCell(headerCell);

    // Calculate totals
    double totalRevenue = 0;
    int totalTickets = 0;

    for (Map<String, Object> rowData : data) {
      // Get revenue
      Object revenueObj = rowData.get("total_revenue");
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
      Object ticketsObj = rowData.get("total_ticket_sold");
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

    // Add total records
    addSummaryRow(summaryTable, "Total Records", Integer.toString(data.size()));

    // Add total revenue
    addSummaryRow(summaryTable, "Total Revenue", String.format("$%.2f", totalRevenue));

    // Add total tickets
    addSummaryRow(summaryTable, "Total Tickets Sold", Integer.toString(totalTickets));

    // Add average revenue per ticket
    if (totalTickets > 0) {
      double avgRevenue = totalRevenue / totalTickets;
      addSummaryRow(summaryTable, "Avg. Revenue Per Ticket", String.format("$%.2f", avgRevenue));
    }

    return summaryTable;
  }

  /**
   * Adds a row to the summary table
   */
  private void addSummaryRow(PdfPTable table, String label, String value) {
    PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
    labelCell.setPadding(5);
    labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
    table.addCell(labelCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
    valueCell.setPadding(5);
    valueCell.setBackgroundColor(new BaseColor(240, 240, 240));
    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(valueCell);
  }
}