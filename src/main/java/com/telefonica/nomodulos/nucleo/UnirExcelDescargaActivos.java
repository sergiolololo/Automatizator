package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UnirExcelDescargaActivos {
	
	public static void main(String[] args) throws Exception {
		
		XSSFWorkbook workbook1 = new XSSFWorkbook();
		XSSFSheet sheet1 = workbook1.createSheet();
		
		int j=0;
		File file = new File("C:\\Users\\sergy\\Documents\\ARIS 10\\prueba");
		for(File file1: file.listFiles()){
			FileInputStream fis = new FileInputStream(file1);
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			for(int i=0; i<sheet.getPhysicalNumberOfRows();i++) {
				Row row = sheet.getRow(i);
				Row row1 = sheet1.createRow(j);
				for(int z=0; z<row.getPhysicalNumberOfCells();z++) {
					System.out.println(z);
					row1.createCell(z);
					row1.getCell(z).setCellValue(row.getCell(z).getStringCellValue());
				}
				j++;
			}
			wb.close();
		}
		
		
		File dirDestino = new File("C:\\Users\\sergy\\Documents\\ARIS 10\\prueba" + "aaaaaa.xlsx");
        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
        workbook1.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook1.close();
	}
}