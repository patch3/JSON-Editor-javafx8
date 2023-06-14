package com.editor.util;

import com.jcraft.jsch.*;
import com.editor.util.directory.Directory;
import com.editor.util.directory.DirectoryElement;
import com.editor.util.directory.IDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SFTPClient {
    public static List<SFTPClient> connections = new ArrayList<>();

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
        session.connect(5000);

        Channel channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
        connections.add(this);
        connected = true;
    }

    public void disconnect() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        connections.remove(this);
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

    public Directory getDirectory(String remoteFolderPath, IDirectory parent) throws SftpException {
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
            inputStream = Files.newInputStream(localFile.toPath());

            channelSftp.put(inputStream, remoteFilePath, ChannelSftp.OVERWRITE);
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

    public void createFile(String remoteFilePath) throws SftpException {

        remoteFilePath = remoteFilePath.replace("\\", "/");
        System.err.println(remoteFilePath);

        channelSftp.put(new ByteArrayInputStream(new byte[0]), remoteFilePath);
    }

    public void createDirectory(String remoteDirectoryPath) throws SftpException {
        remoteDirectoryPath = remoteDirectoryPath.replace("\\", "/");
        System.err.println(remoteDirectoryPath);
        channelSftp.mkdir(remoteDirectoryPath);
    }

    public void deleteFile(String remoteFilePath) throws SftpException {
        remoteFilePath = remoteFilePath.replace("\\", "/");
        channelSftp.rm(remoteFilePath);

    }

    public void deleteDirectory(String remoteDirectoryPath) throws SftpException {
        remoteDirectoryPath = remoteDirectoryPath.replace("\\", "/");
        channelSftp.rmdir(remoteDirectoryPath);

    }
}
