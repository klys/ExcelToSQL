import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DataModel {

    public HashMap<String, String> model = new HashMap<String, String>();
    private final String SQL_STR_SEPARATOR = "\"";
    private String table_name = "";
    DataModel(String keys[], String values[]) throws Exception {
        if (keys.length != values.length) {
            throw new Exception("keys and values Must be same size");
        }
        for (int pos = 0; pos < keys.length; pos++) {
            model.put(keys[pos], values[pos]);
        }
    }

    DataModel() {

    }

    public void setTableName(String tablename) {
        table_name = tablename;
    }
    public Object[] getKeys() {
        return model.keySet().toArray();
    }

    public Object[] getValues() {
        return model.values().toArray();
    }

    public String getStrSqlValues() {
        // return the values on the INSERT SQL part
        AtomicReference<String> keys = new AtomicReference<>("(");
        AtomicReference<String> values = new AtomicReference<>("(");

        model.forEach((key, value) -> {
                if (keys.toString() !="(") {
                    keys.set(keys+",");
                    values.set(values+",");
                }
                keys.set(keys + "'" +key +"'");
                values.set(values +SQL_STR_SEPARATOR+ value + SQL_STR_SEPARATOR);
            });
           //System.out.println(keys+") VALUES "+values+")");
            return keys+") VALUES "+values+")";
    }

    public String getStrSqlTableCreation() {
        AtomicReference<String> keys = new AtomicReference<>("CREATE TABLE "+table_name+" ("+System.lineSeparator()+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+System.lineSeparator());

        AtomicInteger count = new AtomicInteger();
        model.forEach((key, value) -> {
            count.getAndIncrement();
            /*if (keys.toString() !="(") {
                keys.set(keys+",");
            }*/

            keys.set(keys + " " +key +" TEXT");

            if (count.get() < model.values().size()) {
                keys.set(keys +","+System.lineSeparator());
            } else {
                keys.set(keys +""+System.lineSeparator());
            }

        });
        //System.out.println(keys+") VALUES "+values+")");
        return keys+");"+System.lineSeparator();
    }

}
