package com.sailotech.testautomation.commonutilities;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sailotech.testautomation.accelarators.TestBase;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReadExcel extends TestBase {

	public static Map<String, String> readExcel() {
		Map<String, String> data = new HashMap<String, String>();
		Workbook workbook = null;
		try {
			String userDir = System.getProperty("user.dir");
			Path testResources = Path.of(userDir).resolve("resources");
//            Path runners = testResources.resolve("runners");

			String[] jsonFiles = System.getProperty("jsonFiles").split("/");

			String excelPath = testResources.resolve(jsonFiles[0]).resolve("testdata files")
					.resolve(jsonFiles[1] + ".xlsx").toString();
			log.info("excelPath : " + excelPath);
			File excelFile = new File(excelPath);

			workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				Cell keyCell = row.getCell(0);
				Cell dataCell = row.getCell(1);
				switch (dataCell.getCellType()) {
				case STRING:
					data.put(keyCell.getStringCellValue(), dataCell.getStringCellValue());
					break;
				case NUMERIC:
					data.put(keyCell.getStringCellValue(), String.valueOf(dataCell.getNumericCellValue()));
					break;
				case BLANK:
					break;
				case BOOLEAN:
					break;
				case ERROR:
					break;
				case FORMULA:
					break;
				case _NONE:
					break;
				default:
					break;
				}
			}
			log.info("data: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
		}
		return data;
	}

	public static Map<String, String> readMDMData(String excelPath, String[] keys, int[] cells) {
		Map<String, String> data = new HashMap<String, String>();
		Workbook workbook = null;
		try {
			log.info("excelPath : " + excelPath);
			File excelFile = new File(excelPath);

			workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				for (int i = 0; i < cells.length; i++) {
					Cell dataCell = row.getCell(cells[i]);
					switch (dataCell.getCellType()) {
					case STRING:
						data.put(keys[i], dataCell.getStringCellValue());
						break;
					case NUMERIC:
						data.put(keys[i], String.valueOf(dataCell.getNumericCellValue()).replace(".0", ""));
						break;
					case BLANK:
						break;
					case BOOLEAN:
						break;
					case ERROR:
						break;
					case FORMULA:
						break;
					case _NONE:
						break;
					default:
						break;
					}
				}
			}
			log.info("data: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
			}
		}
		return data;
	}

	public static void main(String args[]) {
		String file = "C:\\Users\\srilakshmi.supraja\\Downloads\\Global Template review\\01 - Items - tcibd0501m000.xlsx";
		ReadExcel.readMDMData(file, new String[] { "company", "item" }, new int[] { 3, 4 });
	}

}
