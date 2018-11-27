/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakalarka;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Anry
 */
public class FileProcessor {

    private File incomFile;
    private File planFile;
    private Map<String, List<Row>> incomMap;
    private Map<String, List<Row>> planeYMap;
    private Map<String, List<Row>> planlYMap;
    private Map<String, Double> profitMap;
    private Map<Date, Double> cashflow;
    //private Map<String, Pair<Date, Double>> salesMapG;
    //private Map<String, List<Pair<Date, Double>>> profitMapG;
    private GraphFrame frame;
    private Row headOfIncomMap;
    private Row headOfPlanMap;
    private int numOfRows;
    private int rowsOfPotentMiss;
    private Map<Integer, Row> potMissRows = new TreeMap<>();
    private int operationNum;
    private String extF;
    private ErrorFrame eFrame;

    public FileProcessor(File f, int i) throws IOException {
        String extension = "";
        if (i == 1) {
            this.incomFile = f;
            extension = incomFile.getName().substring(incomFile.getName().lastIndexOf(".") + 1);
            this.incomMap = readExcelFile(f, extension);
            headOfIncomMap = extractHead(incomMap);
        } else {
            this.planFile = f;
            extension = planFile.getName().substring(planFile.getName().lastIndexOf(".") + 1);
            this.planeYMap = readExcelFileP(f, extension, 4);
            //headOfPlanMap = extractHead(planeYMap);
            extF = extension;
        }
    }

    public FileProcessor(File incomF, File planF) throws IOException {
        this.incomFile = incomF;
        this.planFile = planF;
        String extensionIncom = "";
        String extensionPlan = "";
        extensionIncom = incomFile.getName().substring(incomFile.getName().lastIndexOf(".") + 1);
        extensionPlan = planFile.getName().substring(planFile.getName().lastIndexOf(".") + 1);
        this.incomMap = readExcelFile(incomF, extensionIncom);
        headOfIncomMap = extractHead(incomMap);
        this.planlYMap = readExcelFileP(planF, extensionPlan, 1);
        this.planeYMap = readExcelFileP(planF, extensionPlan, 4);
        //headOfPlanMap = extractHead(planMap);
    }

    public int getYear() {
        int size = incomMap.size();
        String s = incomMap.keySet().toArray()[size / 2].toString();
        Row r = incomMap.get(s).get(0);
        Date d = r.getCell(6).getDateCellValue();
        return d.getYear() - 100 + 2000;
    }

    public int getOperationNum() {
        return operationNum;
    }

    public boolean CheckFile(int num) throws IOException {
        boolean res = true;
        if (num == 1) {
            String[] origHead = {"Projekt", "Profit centrum", "Účet", "Transakce",
                "Typ", "Doklad", "Datum", "MD", "DAL", "Obrat", "Č.materiálu",
                "Text", "Mnozstvi", "MJ", "Nazev_uctu", "Kód OP", "Název OP",
                "Číslo a název projektu", "Sklad"};
            int i = 0;
            for (Cell cell : headOfIncomMap) {
                if (origHead[i].equals(cell.getStringCellValue()) == false) {
                    return false;
                }
                i++;
                operationNum++;
            }
            if (i != origHead.length) {
                return false;
            }
        } else {
            res = CheckFile(planFile, extF);
        }
        return res;
    }

    public boolean CheckFile(File planF, String extension) throws IOException {
        boolean res = true;
        try (FileInputStream fis = new FileInputStream(planF)) {
            Workbook workbook;
            switch (extension) {
                case "xlsx":
                    workbook = new XSSFWorkbook(fis);
                    break;
                case "xls":
                    workbook = new HSSFWorkbook(fis);
                    break;
                default:
                    eFrame = new ErrorFrame("<html><p>Bol zadaný dokument, ktorý nezodpovedajúci Excelovskému!<br><br>" + extension + "</p></html>");
                    throw new IllegalArgumentException("The specified file is not Excel file");
            }
            if (workbook.getNumberOfSheets() < 5) {
                return false;
            }
            Sheet firstSheet = workbook.getSheetAt(1);
            Sheet forthSheet = workbook.getSheetAt(4);

            String[] origHead = {"Zodp.os.", "č.zak.", "Akcia", "Objednávateľ",
                "Predmet", "Objednané v Kovocité", "Termín odberateľa", "Výroba",
                "Obrat ", "Náklady", "Výnos", "v %",
                "Obrat ", "Náklady", "Výnos", "ON/OFF",
                "Obrat", "Náklady", "Výnos", "v %",
                "Faktúra č.", "suma z. fa", "suma úhrady", "Poistenie"};
            int i = 0;
            for (Cell cell : firstSheet.getRow(2)) {
                if (origHead[i].equals(cell.getStringCellValue()) == false
                        || origHead[i].equals(forthSheet.getRow(2).getCell(i).getStringCellValue()) == false) {
                    //System.out.println(cell.getStringCellValue() + " "+ forthSheet.getRow(2).getCell(i).getStringCellValue() + " " + origHead[i]);
                    eFrame = new ErrorFrame("<html><p>Zmenila sa hlavička vstupného dokumentu.<br>Kontaktujte autora programu.</p></html>");
                    return false;
                }
                i++;
                operationNum++;
            }
            if (i != origHead.length) {
                return false;
            }
            workbook.close();

        }

        return res;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public Integer getRowsOfPotentMiss() {
        return rowsOfPotentMiss;
    }

    public List<String> getPotMissRows() {
        List<String> resStrs = new LinkedList<>();
        String str;
        for (Map.Entry<Integer, Row> entry : potMissRows.entrySet()) {
            Integer key = entry.getKey();
            Row value = entry.getValue();
            str = "#" + String.valueOf(key);
            for (Cell cell : value) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        str += " " + cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        str += " " + String.valueOf(cell.getNumericCellValue());
                        break;
                    default:
                        break;
                }
                operationNum++;
            }
            resStrs.add(str);
            str = "";
        }
        return resStrs;
    }

    private Map<String, List<Row>> readExcelFile(File f, String extension) throws IOException {
        Map<String, List<Row>> projectMap = new TreeMap<>();
        try (FileInputStream fis = new FileInputStream(f)) {
            Workbook workbook;
            switch (extension) {
                case "xlsx":
                    workbook = new XSSFWorkbook(fis);
                    break;
                case "xls":
                    workbook = new HSSFWorkbook(fis);
                    break;
                default:
                    eFrame = new ErrorFrame("<html><p>Bol zadaný dokument, ktorý nezodpovedajúci Excelovskému!<br><br>" + extension + "</p></html>");
                    throw new IllegalArgumentException("The specified file is not Excel file");
            }
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iter = firstSheet.iterator();
            while (iter.hasNext()) {
                Row nextRow = iter.next();
                Cell projectID = nextRow.getCell(0);
                if (projectMap.containsKey(projectID.getStringCellValue())) {
                    projectMap.get(projectID.getStringCellValue()).add(nextRow);
                } else {
                    List<Row> rows = new LinkedList<>();
                    rows.add(nextRow);
                    projectMap.put(projectID.getStringCellValue(), rows);
                }
                operationNum++;
            }
            workbook.close();
        }
        return projectMap;
    }

    private Map<String, List<Row>> readExcelFileP(File f, String extension, int sheetNum) throws IOException {
        Map<String, List<Row>> projectMap = new TreeMap<>();
        try (FileInputStream fis = new FileInputStream(f)) {
            Workbook workbook;
            switch (extension) {
                case "xlsx":
                    workbook = new XSSFWorkbook(fis);
                    break;
                case "xls":
                    workbook = new HSSFWorkbook(fis);
                    break;
                default:
                    eFrame = new ErrorFrame("<html><p>Bol zadaný dokument, ktorý nezodpovedajúci Excelovskému!<br><br>" + extension + "</p></html>");
                    throw new IllegalArgumentException("The specified file is not Excel file");
            }
            //System.out.println(sheetNum + extension);
            if (workbook.getNumberOfSheets() - 1 < sheetNum) {
                return projectMap;
            }
            Sheet firstSheet = workbook.getSheetAt(sheetNum);
            Iterator<Row> iter = firstSheet.iterator();
            int i = 0;
            while (iter.hasNext()) {
                Row nextRow = iter.next();
                if (i > 2) {
                    Cell projectID = nextRow.getCell(1);
                    //System.out.println(firstSheet.getSheetName()+projectID + nextRow.getFirstCellNum());
                    if (projectID != null && projectID.getCellType() == Cell.CELL_TYPE_STRING) {
                        if (projectMap.containsKey(projectID.getStringCellValue())) {
                            projectMap.get(projectID.getStringCellValue()).add(nextRow);
                        } else {
                            List<Row> rows = new LinkedList<>();
                            rows.add(nextRow);
                            projectMap.put(projectID.getStringCellValue(), rows);
                        }
                    }
                }
                i++;
                operationNum++;
            }
            workbook.close();
        }
        return projectMap;
    }

    /**
     * Method write XLS table with sorted projects
     *
     * @param fileOut output file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void WriteSortedProject(File fileOut) throws FileNotFoundException, IOException {
        /* ************************************************************** *
         * Sekcia prvá atribúty excelovský dokument, jeho hárky, atribút  *
         * obsahujúci štýl pre zvýrazňovania riadku s chybným údajom,     *
         * pomocné atribúty pre prechody cyklami, pomocné metódy          *
         * ************************************************************** */
        Workbook book = new HSSFWorkbook();
        Sheet newSheet = book.createSheet();
        Font red = book.createFont();
        red.setColor(HSSFColor.RED.index);
        CellStyle style = book.createCellStyle();
        style.setFont(red);
        WriteHeadofXsl(newSheet, headOfIncomMap);
        CellStyle cellStyle = book.createCellStyle();
        CreationHelper createHelper = book.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("d.m.yyyy"));
        int i = 1;
        // koniec prvej sekcia
        /* ***************************************************************** *
         * Sekcia druhá cyklus prechádzajúci mapu načítaných dát z dokumentu,*
         * kde následne prechádza všetky riadky s rovnakým kľúčom, u ktorých *
         * porovnáva vzájomne riadky medzi sebou a kontroluje zhodu v hodnote*
         * riadku, číslo účtu a dátum operácie. Ak takú zhodu nájde opravý   *
         * hodnotu a riadok označý tak, že mu pridá červený štýl             *
         * ***************************************************************** */
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            int ii = 0;
            List<Integer> redRow = new LinkedList<>();
            for (Row row : value) {
                numOfRows++;
                Row newRow = newSheet.createRow(i++);
                for (int k = ii + 1; k < value.size(); k++) {
                    double val1 = 0, val2 = 0;
                    if (value.get(ii).getCell(2).getNumericCellValue() < 600000
                            && value.get(k).getCell(2).getNumericCellValue() < 600000) {
                        if (value.get(ii).getCell(7).getCellType() == Cell.CELL_TYPE_STRING) {
                            val1 = CellStrToNum(value.get(ii).getCell(7));
                        } else {
                            val1 = value.get(ii).getCell(7).getNumericCellValue();
                        }
                        if (value.get(k).getCell(7).getCellType() == Cell.CELL_TYPE_STRING) {
                            val2 = CellStrToNum(value.get(k).getCell(7));
                        } else {
                            val2 = value.get(k).getCell(7).getNumericCellValue();
                        }
                        if (val1 == val2 && value.get(k).getCell(6).getDateCellValue().
                                equals(value.get(ii).getCell(6).getDateCellValue())
                                && value.get(k).getCell(2).getNumericCellValue()
                                == value.get(ii).getCell(2).getNumericCellValue()) {
                            redRow.add(k);
                            /* If you want to repair misstakes in excel file  deactivate comment */
                            //value.get(k).getCell(7).setCellValue(val1 * -1);
                            //value.get(k).getCell(9).setCellValue(val1 * -1);
                            rowsOfPotentMiss++;
                            potMissRows.put(newRow.getRowNum() + 1 + k - ii, value.get(k));
                        }
                    }
                    for (Integer intg : redRow) {
                        if (intg == ii) {
                            newRow.setRowStyle(style);
                        }
                    }
                    operationNum++;
                }
                // koniec druhej sekcie
                /* ********************************************************** *
                 * Sekcia tretia je výpis zotriedených opravených údajov do   *
                 * nového excelovského dokumentu. Tento zápis prebieha vý-    *
                 * pisom jednotlivých okienok dokumentu. Pokiaľ všetko pre-   *
                 * behne bez chyby vypíše sa konzolová správa "Sort Finished" *
                 * ********************************************************** */
                for (int j = 0; j < 19; j++) {
                    Cell cell = row.getCell(j);
                    Cell newCell = newRow.createCell(j);
                    newCell.setCellStyle(newRow.getRowStyle());
                    if (cell == null) {
                        newCell.setCellValue("");
                    } else {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                newCell.setCellValue(cell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    newCell.setCellValue(cell.getDateCellValue());
                                    newCell.setCellStyle(cellStyle);
                                } else {
                                    newCell.setCellValue(cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                newCell.setCellValue(cell.getBooleanCellValue());
                                break;
                            default:
                                newCell.setCellValue("Blank");
                                System.out.println("Blank cell");
                                break;
                        }
                    }
                    operationNum++;
                }
                ii++;
            }
            Row blankR = newSheet.createRow(i++);
        }
        try (FileOutputStream fos = new FileOutputStream(fileOut)) {
            book.write(fos);
            fos.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        System.out.println("Sort Finished!");
    }

    private Row extractHead(Map<String, List<Row>> map) {
        List<Row> rows = map.get("Projekt");
        Row row = rows.get(0);
        map.remove("Projekt");
        return row;
    }

    private void WriteHeadofXsl(Sheet sheet, Row row) {
        Row newRow = sheet.createRow(0);
        int i = 0;
        for (Cell cell : row) {
            Cell newCell = newRow.createCell(i++);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(cell.getBooleanCellValue());
                    break;
                default:
                    newCell.setCellValue("Blank");
                    System.out.println("Blank cell");
                    break;
            }
            operationNum++;
        }
    }

    public void WriteProfitByProject(File fileProfitOut) throws IOException {
        Workbook profitBook = new HSSFWorkbook();
        CellStyle style;
        Sheet profitSheet = profitBook.createSheet();
        profitMap = new TreeMap<>();
        int i = 0;
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            double profit = 0;
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            for (Row row : value) {
                if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    profit -= row.getCell(7).getNumericCellValue();
                } else {
                    profit -= CellStrToNum(row.getCell(7));
                }
                if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    profit += row.getCell(8).getNumericCellValue();
                } else {
                    profit += CellStrToNum(row.getCell(8));
                }
                operationNum++;
            }
            profitMap.put(key, profit);
            Row newRow = profitSheet.createRow(i++);
            Cell projectCell = newRow.createCell(0);
            projectCell.setCellValue(key);
            Cell profitCell = newRow.createCell(1);
            profitCell.setCellValue(profit);
            if (profit < 0) {
                Font redFont = profitBook.createFont();
                redFont.setColor(HSSFColor.RED.index);
                style = profitBook.createCellStyle();
                style.setFont(redFont);
                profitCell.setCellStyle(style);
            }
            if (profit > 0) {
                Font greenFont = profitBook.createFont();
                greenFont.setColor(HSSFColor.GREEN.index);
                style = profitBook.createCellStyle();
                style.setFont(greenFont);
                profitCell.setCellStyle(style);
            }
        }
        if (profitMap.containsKey("Projekt")) {
            profitMap.remove("Projekt");
            System.out.println("Removing finished");
        }
        try (FileOutputStream fos = new FileOutputStream(fileProfitOut)) {
            profitBook.write(fos);
            fos.close();
        } catch (Exception e) {
            //throw new IOException(e.getMessage());
            eFrame = new ErrorFrame("<html><p>Vstupne - výstupná chyba systému!<br>" + e.getMessage() + "</p></html>");
        }
        System.out.println("Profit Finished!");
    }

    /**
     *
     * @param fileProfitOut
     * @return
     * @throws IOException
     */
    public Pair<Double, Double> WriteSalesByProject(File fileProfitOut) throws IOException {
        Workbook workbook;
        try (FileInputStream fis = new FileInputStream(fileProfitOut)) {
            switch (fileProfitOut.getName().substring(fileProfitOut.getName().lastIndexOf(".") + 1)) {
                case "xlsx":
                    workbook = new XSSFWorkbook(fis);
                    break;
                case "xls":
                    workbook = new HSSFWorkbook(fis);
                    break;
                default:
                    throw new IllegalArgumentException("The specified file is not Excel file");
            }
        }
        Sheet lYSheet = workbook.getSheetAt(1);
        Sheet eYSheet = workbook.getSheetAt(4);
        Map<String, Double> salesMap = new TreeMap<>();
        double totalSalesAll = 0;
        double totalSalesPlayCité = 0;
        int i = 0;
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            double sales = 0;
            double costs = 0;
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            for (Row row : value) {
                if (row.getCell(2).getNumericCellValue() >= 601000 && row.getCell(2).getNumericCellValue() < 605000) {
                    if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        sales += row.getCell(8).getNumericCellValue();//Math.abs(row.getCell(8).getNumericCellValue());
                    } else {
                        sales += CellStrToNum(row.getCell(8));//Math.abs(CellStrToNum(row.getCell(8)));
                    }
                }
                if (row.getCell(2).getNumericCellValue() >= 501000 && row.getCell(2).getNumericCellValue() < 599000) {
                    if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        costs += row.getCell(7).getNumericCellValue();//Math.abs(row.getCell(7).getNumericCellValue());
                    } else {
                        costs += CellStrToNum(row.getCell(7));//Math.abs(CellStrToNum(row.getCell(7)));
                    }
                }
                operationNum++;
            }
            for (Map.Entry<String, List<Row>> entry1 : planeYMap.entrySet()) {
                String planKey = entry1.getKey();
                if (key.equals(planKey)) {
                    Cell cell = eYSheet.getRow(entry1.getValue().get(0).getRowNum()).getCell(16);
                    cell.setCellValue(sales);
                    Cell ccell = eYSheet.getRow(entry1.getValue().get(0).getRowNum()).getCell(17);
                    ccell.setCellValue(costs);
                    Cell icell = eYSheet.getRow(entry1.getValue().get(0).getRowNum()).getCell(18);
                    icell.setCellValue(sales - costs);
                    Cell picell = eYSheet.getRow(entry1.getValue().get(0).getRowNum()).getCell(19);
                    if (sales == 0) {
                        picell.setCellType(CellType.BLANK);
                    } else {
                        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
                        formatter.applyPattern("#,###,##0.00");
                        picell.setCellValue(formatter.format((sales - costs) / sales * 100));
                    }
                }
                operationNum++;
            }
            for (Map.Entry<String, List<Row>> entry2 : planlYMap.entrySet()) {
                String planKey = entry2.getKey();
                if (key.equals(planKey)) {
                    Cell cell = lYSheet.getRow(entry2.getValue().get(0).getRowNum()).getCell(16);
                    cell.setCellValue(sales);
                    Cell ccell = lYSheet.getRow(entry2.getValue().get(0).getRowNum()).getCell(17);
                    ccell.setCellValue(costs);
                    Cell icell = lYSheet.getRow(entry2.getValue().get(0).getRowNum()).getCell(18);
                    icell.setCellValue(sales - costs);
                    Cell picell = lYSheet.getRow(entry2.getValue().get(0).getRowNum()).getCell(19);
                    if (sales == 0) {
                        picell.setCellType(CellType.BLANK);
                    } else {
                        System.out.println(sales);
                        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
                        formatter.applyPattern("#,###,##0.00");
                        picell.setCellValue(formatter.format((sales - costs) / sales * 100));
                    }
                }
                operationNum++;
            }
            salesMap.put(key, sales);
            totalSalesAll += sales;
            if (key.contains("C")) {
                totalSalesPlayCité += sales;
            }
        }
        workbook.getCreationHelper().createDataFormat().getFormat("#.##");
        try (FileOutputStream fos = new FileOutputStream(fileProfitOut)) {
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            //throw new IOException(e.getMessage());
            eFrame = new ErrorFrame("<html><p>Vstupne - výstupná chyba systému!<br>" + e.getMessage() + "</p></html>");
        }
        System.out.println("Sales Finished!");
        Pair p = new Pair(totalSalesAll, totalSalesPlayCité);
        return p;
    }

    /*
    public void CheckIncomInPlan(File file) throws IOException {
        Workbook checkBook = new HSSFWorkbook();
        Sheet checkSheet = checkBook.createSheet();
        WriteHeadofXsl(checkSheet, headOfPlanMap);
        Font red = checkBook.createFont();
        red.setColor(HSSFColor.RED.index);
        Font green = checkBook.createFont();
        green.setColor(HSSFColor.GREEN.index);
        CellStyle gStyle = checkBook.createCellStyle();
        gStyle.setFont(green);
        CellStyle rStyle = checkBook.createCellStyle();
        rStyle.setFont(red);

        if (profitMap == null) {
            profitMap = new TreeMap<>();
            int i = 0;
            for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
                double profit = 0;
                String key = entry.getKey();
                List<Row> value = entry.getValue();
                for (Row row : value) {
                    if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        profit -= row.getCell(7).getNumericCellValue();
                    } else {
                        profit -= CellStrToNum(row.getCell(7));
                    }
                    if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        profit += row.getCell(8).getNumericCellValue();
                    } else {
                        profit += CellStrToNum(row.getCell(8));
                    }
                }
                profitMap.put(key, profit);
            }
        }

        int i = 1;
        for (Map.Entry<String, List<Row>> entry : planMap.entrySet()) {
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            for (Row row : value) {
                Row newRow = checkSheet.createRow(i++);
                for (int j = 0; j < 24; j++) {
                    Cell cell = row.getCell(j);
                    Cell newCell = newRow.createCell(j);
                    if (cell == null) {
                        newCell.setCellValue("");
                    } else {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                if (j >= 6 && j <= 12) {
                                    newCell.setCellValue(CellStrToNum(cell));
                                } else {
                                    newCell.setCellValue(cell.getStringCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                newCell.setCellValue(cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                newCell.setCellValue(cell.getBooleanCellValue());
                                break;
                            default:
                                newCell.setCellValue("Blank");
                                System.out.println("Blank cell");
                                break;
                        }
                    }
                }
                if (row.getCell(21) != null && row.getCell(21).getStringCellValue().equals("Uzavřeno")) {
                    if (!profitMap.containsKey(key)) {
                        Cell cell = newRow.createCell(24);
                        cell.setCellStyle(rStyle);
                        cell.setCellValue("Neznáme");
                    }
                    if (row.getCell(12).getCellType() == Cell.CELL_TYPE_STRING) {
                        row.getCell(12).setCellValue(CellStrToNum(row.getCell(12)));
                    }
                    if (Math.abs(row.getCell(12).getNumericCellValue() - profitMap.get(key)) > 0.01) {
                        Cell cell = newRow.createCell(24);
                        cell.setCellStyle(rStyle);
                        cell.setCellValue(profitMap.get(key));
                        double sales = 0;
                        for (Row row1 : incomMap.get(key)) {
                            if (row1.getCell(8).getCellType() == Cell.CELL_TYPE_STRING) {
                                sales += CellStrToNum(row1.getCell(8));
                            } else {
                                sales += row1.getCell(8).getNumericCellValue();
                            }
                        }
                        rStyle.setDataFormat(
                                checkBook.getCreationHelper().createDataFormat().getFormat("#.##"));
                        Cell cell2 = newRow.createCell(25);
                        cell2.setCellStyle(rStyle);
                        cell2.setCellValue(profitMap.get(key) / sales * 100);
                    } else {
                        Cell cell = newRow.createCell(24);
                        cell.setCellStyle(gStyle);
                        cell.setCellValue(profitMap.get(key));
                    }
                }
            }

        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            checkBook.write(fos);
            fos.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        System.out.println("Check Finished!");
    }*/
    /**
     * Convert enlish notation of numbers writen by string val to Double value
     *
     * @param cell cell which contains value for converting
     * @return convertet value in Double type
     */
    public double CellStrToNum(Cell cell) {
        double number = 0;
        String num = cell.getStringCellValue();
        number += Double.parseDouble(num.substring(0, num.indexOf("."))) * 1000;
        String substr = num.substring(num.indexOf(".") + 1);
        substr = substr.replace(',', '.');
        if (number < 0) {
            number -= Double.parseDouble(substr);
        } else {
            number += Double.parseDouble(substr);
        }
        return number;
    }

    private void FillCashFlowData() {
        cashflow = new TreeMap<>();
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            for (Row row : value) {
                Date date = row.getCell(6).getDateCellValue();
                if (row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING) {
                    if (cashflow.containsKey(date)) {
                        cashflow.put(date, cashflow.get(date) + (-1) * CellStrToNum(row.getCell(9)));
                    } else {
                        cashflow.put(date, (-1) * CellStrToNum(row.getCell(9)));
                    }
                } else {
                    if (cashflow.containsKey(date)) {
                        cashflow.put(date, cashflow.get(date) + (-1) * row.getCell(9).getNumericCellValue());
                    } else {
                        cashflow.put(date, (-1) * row.getCell(9).getNumericCellValue());
                    }
                }

            }
        }

    }

    public Map<Date, Double> getCashflow() {
        return cashflow;
    }

    /*public Map<String, Pair<Date, Double>> getSalesMapG() {
        salesMapG = new TreeMap<>();
        Map<String, Double> salesMap = new TreeMap<>();
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            double sales = 0;
            String key = entry.getKey();
            List<Row> value = entry.getValue();
            for (Row row : value) {
                if (row.getCell(2).getNumericCellValue() >= 601000 && row.getCell(2).getNumericCellValue() < 605000) {
                    if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        sales += row.getCell(8).getNumericCellValue();//Math.abs(row.getCell(8).getNumericCellValue());
                    } else {
                        sales += CellStrToNum(row.getCell(8));//Math.abs(CellStrToNum(row.getCell(8)));
                    }
                }

            }
            salesMap.put(key, sales);
        }
        for (Map.Entry<String, List<Row>> entry : planMap.entrySet()) {
            String key = entry.getKey();
            Row row = entry.getValue().get(0);
            if (row.getCell(21) != null && row.getCell(21).getStringCellValue().equals("Uzavřeno")) {
                salesMapG.put(key, new Pair(row.getCell(4).getDateCellValue(), salesMap.get(key)));
            } else {
                if (row.getCell(3) == null) {
                    continue;
                }
                if (row.getCell(6).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    salesMapG.put(key, new Pair(row.getCell(3).getDateCellValue(), row.getCell(6).getNumericCellValue()));
                } else {
                    salesMapG.put(key, new Pair(row.getCell(3).getDateCellValue(), CellStrToNum(row.getCell(6))));
                }
            }
        }
        return salesMapG;
    }
     
    public Map<String, List<Pair<Date, Double>>> getProfitMapG() {
        profitMapG = new TreeMap<>();
        for (Map.Entry<String, List<Row>> entry : incomMap.entrySet()) {
            String key = entry.getKey();
            if (!key.contains("SK") || key.contains("Y")) {
                continue;
            }
            List<Row> value = entry.getValue();
            List<Pair<Date, Double>> l = new LinkedList<>();
            for (Row row : value) {
                double profit = 0;
                Pair p;
                if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    profit -= row.getCell(7).getNumericCellValue();
                } else {
                    profit -= CellStrToNum(row.getCell(7));
                }
                if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    profit += row.getCell(8).getNumericCellValue();
                } else {
                    profit += CellStrToNum(row.getCell(8));
                }
                p = new Pair(row.getCell(6).getDateCellValue(), profit);
                l.add(p);
            }
            profitMapG.put(key, l);
        }
        return profitMapG;
    }
     */
}
