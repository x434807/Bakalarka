/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakalarka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Anry
 */
public class AnalysisProcessor {

    private File balanceFile;
    private Map<Double, Row> assets;
    private Map<Double, Row> liabilities;
    private Map<Double, Row> incNlos;
    private int[] years = new int[2];

    public AnalysisProcessor(File balanceFile) throws IOException {
        this.balanceFile = balanceFile;
        read();
    }

    public void read() throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream(balanceFile)) {
            Workbook workbook = new HSSFWorkbook(fis);
            Sheet assetsSheet = workbook.getSheetAt(1);
            Iterator<Row> iter = assetsSheet.iterator();
            assets = new TreeMap<>();
            int counter = 0;
            while (iter.hasNext()) {
                Row nextRow = iter.next();
                if (counter > 6) {
                    if (nextRow.getCell(5).getCellType() == Cell.CELL_TYPE_STRING) {
                        assets.put(Double.parseDouble(nextRow.getCell(5).getStringCellValue()), nextRow);
                    } else {
                        assets.put(nextRow.getCell(5).getNumericCellValue(), nextRow);
                    }
                }
                counter++;
            }
            counter = 0;
            liabilities = new TreeMap<>();
            Sheet liabSheet = workbook.getSheetAt(2);
            iter = liabSheet.iterator();
            while (iter.hasNext()) {
                Row nextRow = iter.next();
                if (counter > 5) {
                    if (nextRow.getCell(5).getCellType() == Cell.CELL_TYPE_STRING) {
                        liabilities.put(Double.parseDouble(nextRow.getCell(5).getStringCellValue()), nextRow);
                    } else {
                        liabilities.put(nextRow.getCell(5).getNumericCellValue(), nextRow);
                    }
                }
                counter++;
            }
            counter = 0;
            incNlos = new TreeMap<>();
            Sheet incSheet = workbook.getSheetAt(3);
            iter = incSheet.iterator();
            while (iter.hasNext()) {
                Row nextRow = iter.next();
                if (counter > 5) {
                    if (nextRow.getCell(5).getCellType() == Cell.CELL_TYPE_STRING) {
                        incNlos.put(Double.parseDouble(nextRow.getCell(5).getStringCellValue()), nextRow);
                    } else {
                        incNlos.put(nextRow.getCell(5).getNumericCellValue(), nextRow);
                    }
                }
                counter++;
            }
            Sheet fSheet = workbook.getSheetAt(0);
            Row year1 = fSheet.getRow(16);
            String y1 = year1.getCell(31).getStringCellValue()
                    + year1.getCell(32).getStringCellValue()
                    + year1.getCell(33).getStringCellValue()
                    + year1.getCell(34).getStringCellValue();
            Row year2 = fSheet.getRow(23);
            String y2 = year2.getCell(31).getStringCellValue()
                    + year2.getCell(32).getStringCellValue()
                    + year2.getCell(33).getStringCellValue()
                    + year2.getCell(34).getStringCellValue();
            years[0] = Integer.parseInt(y1);
            years[1] = Integer.parseInt(y2);
            workbook.close();
        }
    }

    public List<Triplet<String, Double, Double>> cashFlow() {
        List<Triplet<String, Double, Double>> cf = new LinkedList<>();
        cf.add(new Triplet(liabilities.get(100.0).getCell(1).getStringCellValue(), liabilities.get(100.0).getCell(6).getNumericCellValue(), 0.0));
        cf.add(new Triplet(incNlos.get(22.0).getCell(1).getStringCellValue(), incNlos.get(22.0).getCell(6).getNumericCellValue(), 0.0));
        cf.add(new Triplet(liabilities.get(118.0).getCell(1).getStringCellValue(), liabilities.get(118.0).getCell(6).getNumericCellValue(), liabilities.get(118.0).getCell(7).getNumericCellValue()));
        cf.add(new Triplet(liabilities.get(122.0).getCell(1).getStringCellValue(), liabilities.get(122.0).getCell(6).getNumericCellValue(), liabilities.get(122.0).getCell(7).getNumericCellValue()));
        cf.add(new Triplet(liabilities.get(139.0).getCell(1).getStringCellValue(), liabilities.get(139.0).getCell(6).getNumericCellValue(), liabilities.get(139.0).getCell(7).getNumericCellValue()));
        cf.add(new Triplet(liabilities.get(141.0).getCell(1).getStringCellValue(), liabilities.get(141.0).getCell(6).getNumericCellValue(), liabilities.get(141.0).getCell(7).getNumericCellValue()));
        cf.add(new Triplet(assets.get(53.0).getCell(1).getStringCellValue(), assets.get(53.0).getCell(9).getNumericCellValue(), assets.get(53.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(assets.get(74.0).getCell(1).getStringCellValue(), assets.get(74.0).getCell(9).getNumericCellValue(), assets.get(74.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(assets.get(34.0).getCell(1).getStringCellValue(), assets.get(34.0).getCell(9).getNumericCellValue(), assets.get(34.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(assets.get(11.0).getCell(1).getStringCellValue(), assets.get(11.0).getCell(9).getNumericCellValue(), assets.get(11.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(assets.get(21.0).getCell(1).getStringCellValue(), assets.get(21.0).getCell(9).getNumericCellValue(), assets.get(21.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(assets.get(41.0).getCell(1).getStringCellValue(), assets.get(41.0).getCell(9).getNumericCellValue(), assets.get(41.0).getCell(8).getNumericCellValue()));
        cf.add(new Triplet(liabilities.get(102.0).getCell(1).getStringCellValue(), liabilities.get(102.0).getCell(6).getNumericCellValue(), liabilities.get(102.0).getCell(7).getNumericCellValue()));

        return cf;
    }

    public Map<Integer, Triplet<Double, Double, Double>> solidity() {
        Map<Integer, Triplet<Double, Double, Double>> res = new TreeMap<>();
        for (int i = 8;  i < 10; i++) {
            double val1 = (assets.get(71.0).getCell(i).getNumericCellValue()) / (liabilities.get(122.0).getCell(i-2).getNumericCellValue() + liabilities.get(139.0).getCell(i-2).getNumericCellValue());
            double val2 = (assets.get(53.0).getCell(i).getNumericCellValue() + assets.get(71.0).getCell(i).getNumericCellValue()) / (liabilities.get(122.0).getCell(i-2).getNumericCellValue() + liabilities.get(139.0).getCell(i-2).getNumericCellValue());
            double val3 = (assets.get(34.0).getCell(i).getNumericCellValue() + assets.get(53.0).getCell(i).getNumericCellValue() + assets.get(71.0).getCell(i).getNumericCellValue()) / (liabilities.get(122.0).getCell(i-2).getNumericCellValue() + liabilities.get(139.0).getCell(i-2).getNumericCellValue());
            res.put(years[i-8], new Triplet(val1,val2,val3));
        }
        return res;
    }
    
    
    public Map<Integer, List<Double>> indebtedness() {
        Map<Integer, List<Double>> res = new TreeMap<>();
        List<Double> list;
        int j = 0;
        for (int i = 8;  i < 10; i++) {
            list = new ArrayList<>();
            list.add((liabilities.get(80.0).getCell(i-2).getNumericCellValue()) / (liabilities.get(79.0).getCell(i-2).getNumericCellValue())*100);
            list.add((liabilities.get(121.0).getCell(i-2).getNumericCellValue() + liabilities.get(139.0).getCell(i-2).getNumericCellValue())/ (assets.get(1.0).getCell(i).getNumericCellValue())*100);
            list.add((liabilities.get(122.0).getCell(i-2).getNumericCellValue()) / (assets.get(53.0).getCell(i).getNumericCellValue()));
            list.add((incNlos.get(56.0).getCell(i-j).getNumericCellValue() + incNlos.get(49.0).getCell(i-j).getNumericCellValue()) / (incNlos.get(49.0).getCell(i-j).getNumericCellValue()));
            list.add((assets.get(1.0).getCell(i).getNumericCellValue()) / (liabilities.get(80.0).getCell(i-2).getNumericCellValue()));
            res.put(years[i-8], list);
            j++;
        }
        return res;
    }
    
    
    public Map<Integer, List<Double>> rentability() {
        Map<Integer, List<Double>> res = new TreeMap<>();
        List<Double> list;
        int j = 0;
        for (int i = 8;  i < 10; i++) {
            list = new ArrayList<>();
            list.add((liabilities.get(100.0).getCell(i-2).getNumericCellValue()) / (liabilities.get(80.0).getCell(i-2).getNumericCellValue())*100);
            list.add((incNlos.get(56.0).getCell(i-j).getNumericCellValue())/ (assets.get(1.0).getCell(i).getNumericCellValue())*100);
            list.add((liabilities.get(100.0).getCell(i-2).getNumericCellValue()) / (incNlos.get(3.0).getCell(i-j).getNumericCellValue() + incNlos.get(4.0).getCell(i-j).getNumericCellValue() + incNlos.get(5.0).getCell(i-j).getNumericCellValue())*100);
            list.add((liabilities.get(100.0).getCell(i-2).getNumericCellValue()) / ((liabilities.get(80.0).getCell(i-2).getNumericCellValue()) + (liabilities.get(102.0).getCell(i-2).getNumericCellValue()))*100);
            list.add((liabilities.get(100.0).getCell(i-2).getNumericCellValue() + incNlos.get(57.0).getCell(i-j).getNumericCellValue() + incNlos.get(49.0).getCell(i-j).getNumericCellValue()) / (incNlos.get(3.0).getCell(i-j).getNumericCellValue() + incNlos.get(4.0).getCell(i-j).getNumericCellValue() + incNlos.get(5.0).getCell(i-j).getNumericCellValue())*100);
            res.put(years[i-8], list);
            j++;
        }
        return res;
    }
}
