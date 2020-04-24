package cn.liangcp.log;

import cn.liangcp.ssh.SshUtils;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : liangcp6
 * @description: 批量执行linux命令
 * @date : 2019/12/17 14:17
 */
public class LogView {

    //默认账号密码
    private static final String USER = "root";
    private static final String PWD = "root";

    /**
     * main参数列表说明：1-linux命令；2 需要执行的主机ip
     * 输出结果：ip + 回车 + 结果
     * @param args
     */
    public static void main(String [] args){
        System.out.println("输入参数为：" + JSON.toJSONString(args));
        if(args.length < 2){
            throw new RuntimeException("错误，参数长度不对，执行命令应该如下：java -jar 执行的linux命令 执行的ip(多个ip以英文,隔开)" +
                    " 验证信息（账号,密码,端口，验证信息三者以逗号隔开，默认是root账号，root密码，22端口，三者以逗号隔开）");
        }
        String linuxShell = args[0];
        String [] ips = args[1].split(",");
        //检查是否包含验证信息
        String [] auths = null;
        if(args.length == 3){
            auths = args[2].split(",");
            if(auths.length < 3){
                throw new RuntimeException("错误，如果输入验证信息，必须包含账号、密码和端口三个信息，ssh默认端口可指定为22");
            }
        }
        List<Map<String,String>> results = new ArrayList<>();
        System.out.println("开始批量连接远程主机-----------");
        for(String ip : ips){
            Map<String,String> result = null;
            if(auths != null){
                result = getResultFromHost(ip,linuxShell,auths[0],auths[1],Integer.parseInt(auths[2]));
            }else {
                result = getResultFromHost(ip,linuxShell);
            }
            results.add(result);
        }
        System.out.println("远程批量主机命令执行完毕，一下是执行结果-----------");
        for(Map<String,String> result : results){
            System.out.println(String.format("主机：%s 执行结果：---------------------",result.get("ip")));
            System.out.println(result.get("msg"));
        }

    }

    private static Map<String,String> getResultFromHost(String host,String shell){
        return getResultFromHost(host,shell,USER,PWD,22);
    }

    private static Map<String,String> getResultFromHost(String host,String shell,String user,String pwd,int port){
        System.out.println("主机：" + host + " 连接开始-----");
        SshUtils sshUtils = new SshUtils(host,user,pwd,port);
        Map<String,String> result =  sshUtils.execute(shell);
        result.put("ip",host);
        return result;
    }
}
