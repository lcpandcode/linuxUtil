package cn.liangcp.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:
 * @Despriction:
 * @Author: liangcp6@chinaunicom.cn
 * @Date: 2019/5/16 9:13
 */
public class SshUtils {

    private String host; // ip
    private String username;
    private String password;
    private int port = 22;

    /**
     * @param host(ip)
     * @param username(用户名)
     * @param password(密码)
     */
    public SshUtils(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public SshUtils(String host, String username, String password,int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    private Connection conn = null;

    /**
     * 连接
     */
    private boolean connect() {
        conn = new Connection(host,port);
        try {
            conn.connect();
            if (conn.authenticateWithPassword(username, password)) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("ssh远程登录服务器失败，失败详情：");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行命令
     */
    public Map<String,String> execute(String command) {
        if (command == null || "null".equals(command)) {
            throw new RuntimeException("传入命令为空！");
        }
        if (!connect()) {
            throw new RuntimeException("连接服务器失败!!!") ;
        }
        Session session = null;
        InputStream stdout = null;
        BufferedReader br = null;
        Map<String,String> result = new HashMap<>(2);
        StringBuffer sb = new StringBuffer();
        try {
            session = conn.openSession();
            session.execCommand(command);
            stdout = new StreamGobbler(session.getStdout());
            br = new BufferedReader(new InputStreamReader(stdout));

            // ExitCode正常为0
            // 1:报错、2:误用命令、126:命令不得执行、127:没找到命令、130:ctrl+c结束、255:返回码超出范围
            result.put("exitCode",session.getExitStatus() + "");
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line + "\n");
            }
            result.put("msg",sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("错误，无法远程执行linux！",e);
        } finally {
            try {
                if(br != null){
                    br.close();
                }
                if(stdout != null){
                    stdout.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (conn != null) {
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
        return result;
    }
}
