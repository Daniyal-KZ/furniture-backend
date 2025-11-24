package com.daniyal.furniturebackend.service;

import com.daniyal.furniturebackend.model.Material;
import com.daniyal.furniturebackend.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final MaterialRepository materialRepository;

    public void importMaterials(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new Exception("Файл пуст");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // начинаем с 1, потому что 0 = заголовок
                Row row = sheet.getRow(i);

                if (row == null) continue;

                Material material = new Material();

                material.setName(getCellString(row, 0));
                material.setType(getCellString(row, 1));
                material.setUnit(getCellString(row, 2));
                material.setPrice(getCellDouble(row, 3));
                material.setImageUrl(getCellString(row, 4));

                materialRepository.save(material);
            }
        }
    }

    private String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ?
                cell.getStringCellValue() :
                String.valueOf(cell.getNumericCellValue());
    }

    private Double getCellDouble(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return 0.0;

        return switch (cell.getCellType()) {
            case STRING -> {
                try { yield Double.parseDouble(cell.getStringCellValue()); }
                catch (Exception e) { yield 0.0; }
            }
            case NUMERIC -> cell.getNumericCellValue();
            default -> 0.0;
        };
    }
}
