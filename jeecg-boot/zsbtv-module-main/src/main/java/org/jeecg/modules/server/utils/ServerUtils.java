package org.jeecg.modules.server.utils;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.server.entity.ZsServersManage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class ServerUtils {



    /**
     * 测试 SSH 连通性
     *
     * @param server   服务器地址
     * @param username 用户名
     * @param password 密码
     * @return 连通性测试结果
     */
    public static boolean checkSSHConnectivity(String server, String username, String password) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, server, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000); // 设置连接超时时间为10秒

            if (session.isConnected()) {
                log.info("SSH 连接成功: " + server);
                session.disconnect();
                return true;
            } else {
                log.error("无法连接到服务器: " + server);
                return false;
            }
        } catch (Exception e) {
            log.error("SSH 连接失败: " + server, e);
            return false;
        }
    }

    /**
     * 获取服务器信息
     */
    public static ZsServersManage getServerInfo(String host, String username, String password) {
        Session session = null;
        ChannelExec channel = null;
        ZsServersManage serverInfo = new ZsServersManage();

        try {
            // 创建 SSH 会话
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 获取操作系统信息
            serverInfo.setOs(executeCommand(session, "cat /etc/os-release | grep PRETTY_NAME"));
            // 获取 CPU 信息
            serverInfo.setCpuModel(executeCommand(session, "lscpu | grep 'Model name'"));
            // 获取 CPU 数量
            serverInfo.setCpuCores(Integer.parseInt(executeCommand(session, "nproc")));
            // 获取内存信息
            serverInfo.setMemoryGb(executeCommand(session, "free -h | awk '/Mem:/ {print $2}'"));
            // 获取磁盘信息
            serverInfo.setDiskGb(executeCommand(session, "df -h / | awk 'NR==2 {print $2}'"));
            // 获取运行时间
            // serverInfo.uptime = executeCommand(session, "uptime -p");

            return serverInfo;
        } catch (JSchException e) {
            log.error("获取服务器信息时出错: " + host, e);
            return null;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * 执行远程命令
     */
    private static String executeCommand(Session session, String command) {
        ChannelExec channel = null;
        StringBuilder output = new StringBuilder();

        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            log.error("获取服务器信息时出错: " + e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return output.toString().trim();
    }
}