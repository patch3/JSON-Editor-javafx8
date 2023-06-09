package util;

import com.jcraft.jsch.*;

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

    public boolean fileExists(String remoteFilePath) {
        try {
            channelSftp.stat(remoteFilePath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    public void checkFilesInFolder(String remoteFolderPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteFolderPath);
        for (ChannelSftp.LsEntry entry : files) {
            if (!entry.getAttrs().isDir()) {
                System.out.println(entry.getFilename());
            }
        }
    }
}
