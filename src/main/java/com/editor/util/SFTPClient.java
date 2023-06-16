package com.editor.util;

import com.jcraft.jsch.*;
import com.editor.util.directory.Directory;
import com.editor.util.directory.DirectoryElement;
import com.editor.util.directory.IDirectory;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Класс, который осуществляет всю работу с SFTP соединением
 */
public class SFTPClient {
    //Текущие открытые соединения с сервером
    public static List<SFTPClient> connections = new ArrayList<>();

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private Session session;
    private ChannelSftp channelSftp;
    private boolean connected; // Флаг, определяющий открыто соединение или нет

    /**
     * Создает обертку для открытия соединения.
     * @param hostname Имя хоста, к которому подключаемся
     * @param port Порт, по которому подключаемся
     * @param username Имя пользователя
     * @param password Пароль
     */
    public SFTPClient(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connected = false;
    }

    /**
     * Открывает соединение
     * @throws  JSchException Исключение, которое появляется при нудачном соединении
     */
    public void connect() throws JSchException {
        JSch jsch = new JSch(); // Создаем экземпляр класса JSch для работы с SSH-соединениями
        session = jsch.getSession(username, hostname, port); // Создаем сессию SSH, передавая ей имя пользователя, имя хоста и порт
        session.setPassword(password); // Устанавливаем пароль для аутентификации в сессии
        session.setConfig("StrictHostKeyChecking", "no"); // Устанавливаем конфигурацию сессии для отключения проверки строгого соответствия хост-ключей
        session.connect(5000); // Устанавливаем таймаут для подключения в 5000 миллисекунд (5 секунд)

        Channel channel = session.openChannel("sftp"); // Открываем канал передачи данных типы SFTP
        channel.connect(); // Подключаемся по этому каналу
        channelSftp = (ChannelSftp) channel; // Преобразовываем текущий канал в канал Sftp
        connections.add(this); // Добавляем это подключение к списку всех подключений
        connected = true; // Если всё прошло без ошибок, то выставляем, что подключение установлено
    }

    /**
     * Отклчаемся от сервера
     */
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
    /**
     * Вернет true, если сейчас подключен к серверу, иначе false
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Скачивает файл с сервера на локальный компьютер
     * @param remoteFilePath Путь к файлу на сервере
     * @param localFilePath Путь к файлу на локальном компьютере
     * @return - Вернет скачанный файл
     * @throws SftpException Вызывается при ошибки скачивания файла
     */
    public File downloadFile(String remoteFilePath, String localFilePath) throws SftpException {
        // Создаем локальные переменные
        OutputStream outputStream = null;
        File downloadedFile = null;
        remoteFilePath = remoteFilePath.replace("\\", "/"); // Преобразовываем пути к корректному варианту
        try {
            // Проверяем и создаем директорию, если она не существует
            Path directoryPath = Paths.get(localFilePath).getParent();
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            outputStream = Files.newOutputStream(Paths.get(localFilePath)); // Открываем стрим к файлу на локальном компьютере
            channelSftp.get(remoteFilePath, outputStream); // Скачиваем файл

            // Создаем объект File для скачанного файла
            downloadedFile = new File(localFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // Закрываем стримы
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignore) {
                }
            }
        }

        return downloadedFile;
    }
    //Я добавил этот функционал, но забыл использовать, возможно пригодится.
    /**
     * Проверяет существует ли файл на удаленном сервере
     * @param remoteFilePath Путь к файлу на сервере
     * @return Вернет true, если да. false - если нет
     */
    public boolean fileExists(String remoteFilePath) {
        try {
            channelSftp.stat(remoteFilePath); // Проверка статуса файла
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * Вернет объект Directory
     * @param remoteFolderPath Путь к папке на удаленном сервере
     * @param parent Родительская папка, если таковая имеется, если нет, допускается null
     * @return Вернет Directory
     * @throws SftpException Любая ошибка подключения
     */
    public Directory getDirectory(String remoteFolderPath, @Nullable IDirectory parent) throws SftpException {
        // Создаем локальные переменные типа List, с шаблоном DirectoryElement
        List<DirectoryElement> jsons = new ArrayList<>();
        List<DirectoryElement> folders = new ArrayList<>();
        remoteFolderPath = remoteFolderPath.replace("\\", "/"); // Приводим путь к корректному
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteFolderPath); // Получаем вектор файлов в удаленной директории
        for (ChannelSftp.LsEntry entry : files) { // Проходим по всем файлам в векторе
            if (entry.getFilename().equals("..") || entry.getFilename().equals(".")) { //Пропускаем все файлы с именем "." или ".."
                continue;
            }
            if (entry.getAttrs().isDir()) { // Если файлом является директория, то добавляем файл в список директорий
                folders.add(new DirectoryElement(remoteFolderPath + File.separator + entry.getFilename(), parent, false));
                continue; // Переходим к следующему шагу цикла
            }
            //Если файл не директория, то определяем его расширение
            int dotIndex = entry.getFilename().lastIndexOf("."); // Ищем последнюю точку в имени файла
            if (dotIndex != -1 && dotIndex < entry.getFilename().length() - 1) {
                String fileExtension = entry.getFilename().substring(dotIndex + 1); // Определяем его расширение
                if (fileExtension.equals("json")) { //Если json, то добавляем в список json-файлов
                    jsons.add(new DirectoryElement(remoteFolderPath + File.separator + entry.getFilename(), parent, true));
                }
            }
        }
        // Возвращаем директорию
        return new Directory(folders, jsons, remoteFolderPath, true);
    }

    /**
     * Загрузка файла на сервер
     * @param remoteFilePath Удаленный путь к файлу
     * @param localFilePath Локальный путь к файлу
     * @throws SftpException Любое исключение подключения
     * @throws IOException Вызывается, если файл на локальном компьютере не найден или доступ к нему не может быть получен
     */
    public void uploadFile(String remoteFilePath, String localFilePath) throws SftpException, IOException {
        remoteFilePath = remoteFilePath.replace("\\", "/"); // Преобразовываем путь к корректному
        InputStream inputStream = null;

        try {
            File localFile = new File(localFilePath); // Создаем объект файла на локальном компьютере
            inputStream = Files.newInputStream(localFile.toPath()); // Создаем выходной поток данных

            channelSftp.put(inputStream, remoteFilePath, ChannelSftp.OVERWRITE); // Записываем файл на сервер с параметром ПЕРЕЗАПИСИ
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (inputStream != null) { // Закрываем стримы
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Создает файл на удаленном сервере
     * @param remoteFilePath Путь к файлу на сервере
     * @throws SftpException Любое исключение подключения, создания файла на сервере
     */
    public void createFile(String remoteFilePath) throws SftpException {
        remoteFilePath = remoteFilePath.replace("\\", "/"); // Преобразовываем путь к корректному
        channelSftp.put(new ByteArrayInputStream(new byte[0]), remoteFilePath); // Создаем файл
    }

    /**
     * Создает директорию на удаленном сервере
     * @param remoteDirectoryPath Путь к папке на сервере
     * @throws SftpException Любое исключение подключения, создания директории на сервере
     */
    public void createDirectory(String remoteDirectoryPath) throws SftpException {
        remoteDirectoryPath = remoteDirectoryPath.replace("\\", "/"); // Преобразовываем путь к корректному
        channelSftp.mkdir(remoteDirectoryPath); // Создаем папку
    }

    /**
     * Удаляет файл с сервера
     * @param remoteFilePath Путь к файлу, который нужно удалить
     * @throws SftpException Любое исключение подключения, удаления(Например, нет прав)
     */
    public void deleteFile(String remoteFilePath) throws SftpException {
        remoteFilePath = remoteFilePath.replace("\\", "/");// Преобразовываем путь к корректному
        channelSftp.rm(remoteFilePath); // Удаляем файл
    }

    /**
     * Удаляет директорию с сервера
     * @param remoteDirectoryPath Путь к директории на сервере
     * @throws SftpException Любое исключение подключения, удаления(Например, нет прав)
     */
    public void deleteDirectory(String remoteDirectoryPath) throws SftpException {
        remoteDirectoryPath = remoteDirectoryPath.replace("\\", "/"); // Преобразовываем путь к корректному
        channelSftp.rmdir(remoteDirectoryPath); // Удаляем директорию

    }
}
