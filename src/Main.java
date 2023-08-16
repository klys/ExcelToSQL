import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class Main {

    private static final String FILE_NAME = "LPN.xlsx";

    //private static String keys[] = new String[]{"DISPATCH_ID", "ENG_PROJ_CODE", "DOA_AREA", "DOA_DT", "COMMODITY_CD", "DISPOSITION", "MODULE_SERIAL_NR", "UPPER_LEVEL_PART_NR", "LOWER_LEVEL_PART_NR", "DOA_COMPTIA_CD", "TECH_ID", "TECH_COMMENT_TXT", "VENDOR_NAME", "CATEGORY_FA", "FA_ANALYSIS", "WEEK_", "MONTH_", "EXCLUDED"};
    private static String keys[] = new String[]{"PPN","UL_PN", "DESCRIPTION", "PART_TYPE", "LL_PN", "QTY_PER", "KP", "CONSIGNATION"};

    private static final String TABLE_NAME = "LPN";

    //private static DataModel data = new DataModel();;

    private static final String OUTPUT_FILE = "LPN.sql";

    private static boolean POSSIBLE_BLANK = false;
    public static String getRowValue(Cell currentCell) throws Exception {
        switch(currentCell.getCellTypeEnum()) {
            case _NONE:
            case NUMERIC:
                return Double.toString(currentCell.getNumericCellValue() ).replace(".0","");

            case STRING:
                String curatedString = currentCell.getStringCellValue();
                if (curatedString.contains("\"") == true) {
                    curatedString = curatedString.replaceAll("\"", "inch");
                }
                if (curatedString.contains("'") == true) {
                    curatedString = curatedString.replaceAll("'", "inch");
                }
                return curatedString;
            case FORMULA:

            case BLANK:
                System.out.println("WARNING: BLANK CELL DETECTED!");
                if (POSSIBLE_BLANK == false) {
                    throw new Exception("BLANK CELL NOT ALLOWED EXCEPTION");
                }
                return "";
            case BOOLEAN:

            case ERROR:
                return "";

        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println("keys.length: "+ keys.length);
        try {
            boolean firsTime = false;
            int fileCount = 0;
            String text = "BEGIN TRANSACTION;"+System.lineSeparator();
            Files.write(Paths.get("./"+OUTPUT_FILE), text.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();



            while (iterator.hasNext()) {
                DataModel data = new DataModel();
                data.setTableName(TABLE_NAME);


                Row currentRow = iterator.next();

                if (POSSIBLE_BLANK) {
                    for (int r = 0; r < keys.length; r++) {
                        currentRow.getCell(r, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    }
                }

                Iterator<Cell> cellIterator = currentRow.iterator();
                int countColumn = 0;
                countColumn = 0;

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    if (countColumn < keys.length) data.model.put(keys[countColumn], getRowValue(currentCell));
                    countColumn++;

                } // loop end

                if (firsTime == false) {
                    firsTime = true;
                    String sqlCreate = data.getStrSqlTableCreation();
                    Files.write(Paths.get("./"+OUTPUT_FILE), sqlCreate.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }

                String sql = "INSERT INTO "+TABLE_NAME+" "+data.getStrSqlValues()+";"+System.lineSeparator();


                Files.write(Paths.get("./"+OUTPUT_FILE), sql.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            String text3 = "COMMIT;";
            Files.write(Paths.get("./"+OUTPUT_FILE), text3.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }


    }


}