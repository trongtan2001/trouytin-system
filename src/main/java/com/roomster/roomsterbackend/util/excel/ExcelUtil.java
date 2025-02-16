package com.roomster.roomsterbackend.util.excel;

import com.roomster.roomsterbackend.entity.OrderEntity;
import com.roomster.roomsterbackend.entity.TenantEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<OrderEntity> listOrders;

    public ExcelUtil(List<OrderEntity> orderEntityList) {
        this.listOrders = orderEntityList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        font.setFontHeight(18);
        // title1 : Thống kê tháng 12
        Row row1 = sheet.createRow(0);
        createCell(row1, 0, "Thống kê tháng 12", style);
        // title2 : Nhà: Tất cả, Phòng: Tất cả
        Row row2 = sheet.createRow(1);
        createCell(row2, 0, "Nhà: Tất cả, Phòng: Tất cả", style);
        font.setFontHeight(12);
        Row row3 = sheet.createRow(2);
        createCell(row3, 0, "STT", style);
        createCell(row3, 1, "Nhà", style);
        createCell(row3, 2, "Phòng", style);
        createCell(row3, 3, "Khách thuê", style);
        createCell(row3, 4, "CS điện", style);
        createCell(row3, 5, "CS nước", style);
        createCell(row3, 6, "Dịch vụ", style);
        createCell(row3, 7, "Tiền phòng", style);
        createCell(row3, 8, "Tổng tiền", style);
        createCell(row3, 9, "Trạng thái", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 3;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        for (OrderEntity order : listOrders) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, order.getOrderId().intValue(), style);
            createCell(row, columnCount++, order.getRoom().getHouse().getHouseName(), style);
            createCell(row, columnCount++, "Phòng " + order.getRoom().getNumberRoom(), style);
            String name = order.getRoom().getTenantList().stream().map(TenantEntity::getName).reduce("", (s1, s2) -> s1 + "\n" + s2);
            if (name.length() > 0) {
                name = name.substring(1);
            } else {
                name = "Không có  khách";
            }
            createCell(row, columnCount++, name, style);
            createCell(row, columnCount++, order.getElectricity().intValue(), style);
            createCell(row, columnCount++, order.getWater().intValue(), style);
            String service = order.getRoom().getServices().stream().map(s -> s.getServiceHouse().getServiceName()).reduce("", (s1, s2) -> s1 + ", " + s2);
            if (service.length() > 0) {
                service = service.substring(1);
            } else {
                service = "Không có dịch vụ";
            }
            createCell(row, columnCount++, service, style);
            createCell(row, columnCount++, order.getRoom().getPrice().intValue(), style);
            createCell(row, columnCount++, order.getTotalPayment().intValue(), style);
            String statusPayment = "";
            switch (order.getStatusPayment()) {
                case "Y":
                    statusPayment = "Đã thanh toán";
                    break;
                case "N":
                    statusPayment = "Chưa thanh toán";
                    break;
                case "P":
                    statusPayment = "Còn nợ";
                    break;
                default:
                    statusPayment = "";
            }
            createCell(row, columnCount++, statusPayment, style);
        }
    }

    public ByteArrayOutputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream;
    }
}
