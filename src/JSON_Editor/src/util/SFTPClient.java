package src.util;

import com.jcraft.jsch.*;
import src.util.directory.Directory;
import src.util.directory.DirectoryElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SFTPClient {
    private String hostname;
    private int port;
    private String username;
    private String password;
    private Session session;
    private ChannelSftp channelSftp;
    private boolean connected;

    public SFTPClient(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connected = false;
    }

    public void connect() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, hostname, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(2000);

        Channel channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
        connected = true;
    }

    public void disconnect() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public File downloadFile(String remoteFilePath, String localFilePath) throws SftpException {
        OutputStream outputStream = null;
        File downloadedFile = null;
        remoteFilePath = remoteFilePath.replace("\\", "/");
        try {
            // Проверяем и создаем директорию, если она не существует
            Path directoryPath = Paths.get(localFilePath).getParent();
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            outputStream = Files.newOutputStream(Paths.get(localFilePath));
            channelSftp.get(remoteFilePath, outputStream);

            // Создаем объект File для скачанного файла
            downloadedFile = new File(localFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignore) {
                }
            }
        }

        return downloadedFile;
    }

    public boolean fileExists(String remoteFilePath) {
        try {
            channelSftp.stat(remoteFilePath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    public List<String> checkFilesInFolder(String remoteFolderPath) throws SftpException {
        List<String> path = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteFolderPath);
        for (ChannelSftp.LsEntry entry : files) {
            path.add(remoteFolderPath + File.separator + entry.getFilename());
        }
        return path;
    }

    public Directory getDirectory(String remoteFolderPath, Directory parent) throws SftpException {
        List<DirectoryElement> jsons = new ArrayList<>();
        List<DirectoryElement> folders = new ArrayList<>();
        remoteFolderPath = remoteFolderPath.replace("\\", "/");

        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteFolderPath);
        for (ChannelSftp.LsEntry entry : files) {
            if (entry.getFilename().equals("..") || entry.getFilename().equals(".")) {
                continue;
            }
            if (entry.getAttrs().isDir()) {
                folders.add(new DirectoryElement(remoteFolderPath + File.separator + entry.getFilename(), parent, false));
                continue;
            }
            int dotIndex = entry.getFilename().lastIndexOf(".");
            if (dotIndex != -1 && dotIndex < entry.getFilename().length() - 1) {
                String fileExtension = entry.getFilename().substring(dotIndex + 1);
                if (fileExtension.equals("json")) {
                    jsons.add(new DirectoryElement(remoteFolderPath + File.separator + entry.getFilename(), parent, true));
                }
            }
        }
        return new Directory(folders, jsons, remoteFolderPath, true);
    }

    public void uploadFile(String remoteFilePath, String localFilePath) throws SftpException {
        remoteFilePath = remoteFilePath.replace("\\", "/");
        InputStream inputStream = null;

        try {
            File localFile = new File(localFilePath);
            inputStream = new FileInputStream(localFile);

            channelSftp.put(inputStream, remoteFilePath, ChannelSftp.OVERWRITE);

            System.out.println("Файл успешно загружен на сервер и заменил старый файл.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
