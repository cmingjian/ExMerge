package exMerge;

import com.fasterxml.jackson.databind.ObjectMapper;
import exMerge.bean.CellBean;
import exMerge.bean.RowBean;
import exMerge.bean.SheetMetaBean;
import org.apache.poi.ss.usermodel.*;

abstract class Excel2JsonParser {
    private static String calcSheetText(Sheet sheet) throws Exception {
        StringBuilder sb = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        int rowNum = sheet.getLastRowNum();
        boolean isFirstRow = true;
        for (int r = 0; r < rowNum; r++) {
            Row row = sheet.getRow(r);
            if (!isFirstRow) {
                sb.append(",");
            }
            isFirstRow = false;
            sb.append("{\n");
            RowBean rowBean = new RowBean(row);
            sb.append(mapper.writeValueAsString(rowBean));


            boolean isFirstCell = true;
            int colNum = row.getLastCellNum();
            for (int c = 0; c < colNum; c++) {
                Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (!isFirstCell) {
                    sb.append(",\n");
                }
                isFirstCell = false;
                CellBean cellBean = new CellBean(cell);
                sb.append(mapper.writeValueAsString(cellBean));
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
    String fileName;

    public abstract String toJsonString() throws Exception;

    String calcJsonString(Workbook wb) throws Exception {
        StringBuilder sb = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        sb.append("[\n");
        boolean isFirstSheet = true;
        for (Sheet sheet : wb) {
            if (!isFirstSheet) {
                sb.append(",\n");
            }
            isFirstSheet = false;
            sb.append("{ \"sheetName\":");
            sb.append("\"");
            sb.append(sheet.getSheetName().replace("\"", "\\\""));
            sb.append("\"");
            SheetMetaBean sheetMetaBean = new SheetMetaBean(sheet);
            sb.append(",\n\"meta\":");
            sb.append(mapper.writeValueAsString(sheetMetaBean));
            sb.append(",\n\"content\":[");
            sb.append(calcSheetText(sheet));
            sb.append("]}");
        }
        sb.append("]\n");
        return sb.toString();
    }
}