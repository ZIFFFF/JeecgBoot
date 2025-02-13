package org.jeecg.modules.live.service.impl;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.modules.live.entity.ZsEncodeTask;
import org.jeecg.modules.live.mapper.ZsEncodeTaskMapper;
import org.jeecg.modules.live.service.IZsEncodeTaskService;
import org.jeecg.modules.server.entity.ZsServersManage;
import org.jeecg.modules.server.service.IZsServersManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * @Description: 编码任务表
 * @Author: jeecg-boot
 * @Date: 2025-01-21
 * @Version: V1.0
 */
@Service
@Slf4j
public class ZsEncodeTaskServiceImpl extends ServiceImpl<ZsEncodeTaskMapper, ZsEncodeTask> implements IZsEncodeTaskService {

    @Autowired
    private IZsServersManageService zsServersManageService;

    @Override
    public Result<T> startSwitch(ZsEncodeTask zsEncodeTask) {
        ZsServersManage zsServersManage = zsServersManageService.getById(zsEncodeTask.getServer());
        String command = zsEncodeTask.getCommand();

        Session session = null;
        ChannelExec channel = null;
        try {
            session = connectToServer(zsEncodeTask);

            if (!checkFfmpegExists(session)) {
                return Result.error("服务器未安装FFmpeg");
            }

            String logFilePath = "/home/" + zsServersManage.getUsername() + "/logs/ffmpeg";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateDir = LocalDate.now().format(formatter);
            String baseLogPath = logFilePath + "/" + zsEncodeTask.getId();

            if (!mkdirLogPath(baseLogPath, session)) {
                return Result.error("创建日志目录失败");
            }

            String fileName = zsEncodeTask.getId() + "_" + dateDir + ".log ";
            String logLevel = " -loglevel verbose -y -stats > ";

            // 执行命令并获取PID
            String wrappedCommand =  command + logLevel + baseLogPath + "/" + fileName + " 2>&1 & echo $!";
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(wrappedCommand);

            log.info("Executing command: " + wrappedCommand);

            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();

            channel.connect();

            // 读取PID
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String pidLine = reader.readLine();
            String error = readStream(err);

            // 等待命令执行完成
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            int exitStatus = channel.getExitStatus();
            if (exitStatus != 0) {
                log.error("Exit status: " + exitStatus + ", Error: " + error);
                return Result.error("Exit status: " + exitStatus + ", Error: " + error);
            }

            if (pidLine == null || pidLine.isEmpty()) {
                log.error("No PID received. Error: " + error);
                return Result.error("No PID received. Error: " + error);
            }

            int pid = parsePid(pidLine);
            if (pid == -1) {
                log.error("Invalid PID: " + pidLine);
                return Result.error("Invalid PID: " + pidLine);
            }

            // 检查进程是否存在
            if (!checkProcessExists(session, pid)) {
                log.error("Process " + pid + " does not exist.");
                return Result.error("进程启动失败");
            }
            // 保存PID到ZsEncodeTask
            zsEncodeTask.setProcessId(pid);
            zsEncodeTask.setStatus(1);
            zsEncodeTask.setStartTime(new Date());
            baseMapper.updateById(zsEncodeTask);
            log.info("Process started with PID: " + pid);
            return Result.ok();

        } catch (Exception e) {
            log.error("ERROR：" + e.getMessage());
            return Result.error("连接失败");
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }

    @Override
    public Result<T> stopSwitch(ZsEncodeTask zsEncodeTask) {
        Integer pidLine = zsEncodeTask.getProcessId();
        Session session = null;
        ChannelExec channel = null;
        try {
            session = connectToServer(zsEncodeTask);

            // 执行命令
            String wrappedCommand = "kill -9 " + pidLine;
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(wrappedCommand);

            log.info("Executing command: " + wrappedCommand);

            channel.connect();
            // 保存PID到ZsEncodeTask
            zsEncodeTask.setProcessId(0);
            zsEncodeTask.setStatus(0);
            zsEncodeTask.setStartTime(null);
            baseMapper.updateById(zsEncodeTask);

            return Result.ok();
        } catch (JSchException e) {
            log.error("ERROR：" + e.getMessage());
            return Result.error("连接失败");
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }

    @Override
    public Result<List<String>> getTaskLogList(ZsEncodeTask zsEncodeTask) {
        ZsServersManage zsServersManage = zsServersManageService.getById(zsEncodeTask.getServer());
        Session session = null;
        try {
            session = connectToServer(zsEncodeTask);
            String logFilePath = "/home/" + zsServersManage.getUsername() + "/logs/ffmpeg/" + zsEncodeTask.getId();
            // 打开SFTP通道
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            // 获取远程目录中的文件列表
            Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(logFilePath);

            List<String> fileObj = new ArrayList<>();
            // 遍历文件并提取日期
            for (ChannelSftp.LsEntry file : files) {
                String fileName = file.getFilename();
                if (!fileName.startsWith(".") && !file.getAttrs().isDir()) { // 跳过隐藏文件和目录
                    String date = extractAndFormatDateFromFileName(fileName);
                    if (date != null) {
                        fileObj.add(date);
                    } else {
                        Result.error("文件名: " + fileName + "，未找到日期");
                    }
                }
            }
            // 关闭SFTP通道
            sftpChannel.exit();
            return Result.ok(fileObj);
        } catch (JSchException | SftpException e) {
            log.error("ERROR：" + e.getMessage());
            return Result.error("获取失败");
        } finally {
            if (session != null) session.disconnect();
        }
    }

    @Override
    public String getTaskLog(ZsEncodeTask zsEncodeTask, String taskDate) {
        ZsServersManage zsServersManage = zsServersManageService.getById(zsEncodeTask.getServer());
        Session session = null;
        try {
            session = connectToServer(zsEncodeTask);
            String logFilePath = "/home/" + zsServersManage.getUsername() + "/logs/ffmpeg/" + zsEncodeTask.getId();
            String fileName = zsEncodeTask.getId() + "_" + taskDate + ".log";
            String remoteFilePath = logFilePath + "/" + fileName;
            // 打开SFTP通道
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            // 读取文件内容
            InputStream inputStream = sftpChannel.get(remoteFilePath);
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            // 读取并打印文件内容
            byte[] buffer = new byte[4096];
            StringBuilder logContent = new StringBuilder();
            int read;
            while ((read = bis.read(buffer)) != -1) {
               logContent.append(new String(buffer, 0, read));
            }
            bis.close();
            inputStream.close();
            // 关闭SFTP通道
            sftpChannel.exit();
            return logContent.toString();
        } catch (JSchException | SftpException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) session.disconnect();
        }
    }

    @Override
    public void checkTaskStatus(ZsEncodeTask zsEncodeTask) {
        Session session = null;
        try {
            session = connectToServer(zsEncodeTask);
            // 检查进程是否存在
            if (!checkProcessExists(session, zsEncodeTask.getProcessId())) {
                log.error("推流任务已停止：{}", zsEncodeTask.getId());
                zsEncodeTask.setStatus(0);
                zsEncodeTask.setStartTime(null);
                zsEncodeTask.setProcessId(0);
                baseMapper.updateById(zsEncodeTask);
                if (zsEncodeTask.getEnableRecovery().equals("Y")) {
                    try {
                        log.info("尝试重启推流任务：{}", zsEncodeTask.getId());
                        startSwitch(zsEncodeTask);
                        return;
                    } catch (Exception e) {
                        log.error("推流任务重启失败：{}", zsEncodeTask.getId());
                    }
                }
                return;
            }
            log.info("推流任务运行正常：{}", zsEncodeTask.getId());

        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }


    private Session connectToServer(ZsEncodeTask zsEncodeTask) throws JSchException {
        ZsServersManage zsServersManage = zsServersManageService.getById(zsEncodeTask.getServer());
        String host = zsServersManage.getHost();
        String username = zsServersManage.getUsername();
        String password = PasswordUtil.decrypt(zsServersManage.getPassword(),zsServersManage.getHost(),zsServersManage.getPrivateKey());
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            // 清除密码引用，防止内存泄漏
            password = null;
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);
            log.info("SSH连接成功{}", host);
            return session;
        } catch (JSchException e) {
            log.error("SSH连接失败" + host + e);
            throw e;
        }
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    private int parsePid(String pidLine) {
        try {
            return Integer.parseInt(pidLine.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean checkProcessExists(Session session, int pid) {
        ChannelExec checkChannel = null;
        try {
            checkChannel = (ChannelExec) session.openChannel("exec");
            checkChannel.setCommand("ps -p " + pid);
            checkChannel.connect();

            while (!checkChannel.isClosed()) {
                Thread.sleep(100);
            }

            return checkChannel.getExitStatus() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (checkChannel != null) checkChannel.disconnect();
        }
    }

    private boolean checkFfmpegExists(Session session) throws JSchException, IOException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("command -v ffmpeg || which ffmpeg"); // 兼容性检查

        try (InputStream in = channel.getInputStream();
             InputStream err = channel.getErrStream()) {
            channel.connect(3000); // 3秒超时
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            // 检查退出状态和输出
            if (channel.getExitStatus() != 0) {
                String error = readStream(err);
                log.warn("FFmpeg检查失败: {}", error);
                return false;
            }

            String path = readStream(in).trim();
            log.info("检测到FFmpeg路径: {}", path);
            return !path.isEmpty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            channel.disconnect();
        }
    }

    private boolean mkdirLogPath(String logFilePath, Session session) {
        ChannelExec checkChannel = null;
        try {
            checkChannel = (ChannelExec) session.openChannel("exec");
            checkChannel.setCommand("mkdir -p " + logFilePath);
            checkChannel.connect();

            while (!checkChannel.isClosed()) {
                Thread.sleep(100);
            }
            log.info("创建日志目录成功: {}", logFilePath);
            return checkChannel.getExitStatus() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (checkChannel != null) checkChannel.disconnect();
        }
    }

    // 方法：从文件名中提取日期并转换为 yyyy-MM-dd 格式
    private static String extractAndFormatDateFromFileName(String fileName) {
        // 使用下划线分割文件名
        String[] parts = fileName.split("_");
        if (parts.length >= 2) {
            // 提取日期部分（假设日期是第二部分，并且在.log之前）
            return parts[1].split("\\.")[0];
        }
        return null;
    }
}
