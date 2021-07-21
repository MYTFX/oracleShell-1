
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableUtils {
    public TableUtils() {
    }

    public static void addListRows(JTable table, List<Map<String, String>> mapList) {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
        Iterator var4 = mapList.iterator();

        while(var4.hasNext()) {
            Map<String, String> map = (Map)var4.next();
            addListRow(table, map);
        }

    }

    public static void addListRow(JTable table, Map<String, String> map) {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        Vector<String> row = new Vector();
        row.add((String)map.get("name"));
        row.add(((String)map.get("type")).equals("D") ? "目录" : "文件");
        row.add((String)map.get("size"));
        row.add((String)map.get("lastModifyTime"));
        model.addRow(row);
    }
}
