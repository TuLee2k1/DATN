package poly.com.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class ExcelUtils {

    // Phương thức mới với tham số đơn giản hơn
    public <T> byte[] exportToExcel(
     List<T> data,
     String[] headers,
     Function<T, Object[]> dataMapper,
     String sheetName
    ) throws IOException {
        // Tạo workbook mới
        try (Workbook workbook = new XSSFWorkbook()) {
            // Tạo sheet
            Sheet sheet = workbook.createSheet(sheetName);

            // Kiểm tra nếu danh sách rỗng
            if (data == null || data.isEmpty()) {
                return new byte[0];
            }

            // Tạo style cho header
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Tạo header row với custom headers
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu
            for (int rowNum = 1; rowNum <= data.size(); rowNum++) {
                Row row = sheet.createRow(rowNum);
                T item = data.get(rowNum - 1);

                // Sử dụng dataMapper để lấy dữ liệu
                Object[] rowData = dataMapper.apply(item);

                for (int colNum = 0; colNum < rowData.length; colNum++) {
                    Cell cell = row.createCell(colNum);
                    Object value = rowData[colNum];

                    // Xử lý các kiểu dữ liệu
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            // Định dạng ngày
                            CellStyle dateStyle = workbook.createCellStyle();
                            CreationHelper createHelper = workbook.getCreationHelper();
                            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
                            cell.setCellStyle(dateStyle);
                            cell.setCellValue((Date) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Thêm thông tin tổng số bản ghi
            addPaginationInfo(sheet, data.size(), headers.length);

            // Chuyển workbook sang byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Các phương thức hỗ trợ khác như createHeaderStyle, addPaginationInfo vẫn giữ nguyên
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    // Phương thức thêm thông tin tổng số bản ghi cho List
    private void addPaginationInfo(Sheet sheet, int totalRecords, int headerLength) {
        // Thêm dòng trống
        Row emptyRow = sheet.createRow(sheet.getLastRowNum() + 2);

        // Thêm thông tin tổng số bản ghi
        Row totalRow = sheet.createRow(sheet.getLastRowNum() + 1);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("Tổng số bản ghi:");
        Cell totalValueCell = totalRow.createCell(1);
        totalValueCell.setCellValue(totalRecords);
    }

    // Phương thức thêm thông tin phân trang cho Page
    private void addPaginationInfo(Sheet sheet, Page<?> page, int headerLength) {
        // Thêm dòng trống
        Row emptyRow = sheet.createRow(sheet.getLastRowNum() + 2);

        // Thêm thông tin phân trang
        Row paginationRow = sheet.createRow(sheet.getLastRowNum() + 1);

        // Tổng số bản ghi
        Cell totalLabelCell = paginationRow.createCell(0);
        totalLabelCell.setCellValue("Tổng số bản ghi:");
        Cell totalValueCell = paginationRow.createCell(1);
        totalValueCell.setCellValue(page.getTotalElements());

        // Trang hiện tại
        Cell currentPageLabelCell = paginationRow.createCell(2);
        currentPageLabelCell.setCellValue("Trang hiện tại:");
        Cell currentPageValueCell = paginationRow.createCell(3);
        currentPageValueCell.setCellValue(page.getNumber() + 1);

        // Tổng số trang
        Cell totalPagesLabelCell = paginationRow.createCell(4);
        totalPagesLabelCell.setCellValue("Tổng số trang:");
        Cell totalPagesValueCell = paginationRow.createCell(5);
        totalPagesValueCell.setCellValue(page.getTotalPages());
    }
}


