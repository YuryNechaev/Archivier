package Archiver;

import Archiver.exception.PathIsNotFoundException;
import Archiver.exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {

    private Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void createZip(Path source) throws Exception {
        // Проверяем, существует ли директория, где будет создаваться архив
        // При необходимости создаем ее
        Path zipDirectory = zipFile.getParent();
        if (Files.notExists(zipDirectory))
            Files.createDirectories(zipDirectory);

        // Создаем zip поток
        ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
        if (Files.isDirectory(source)) {
            // Если архивируем директорию, то нужно получить список файлов в ней
            FileManager fileManager = new FileManager(source);
            List<Path> filenames = fileManager.getFileList();

            // Добавляем каждый файл в архив
            for (Path filename : filenames) {
                addNewZipEntry(zipOutputStream, source, filename);
            }
        } else if (Files.isRegularFile(source)) {
            // Если архивируем отдельный файл, то нужно получить его директорию и имя
            addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
        } else {
            // Если переданный source не директория и не файл, бросаем исключение
            throw new PathIsNotFoundException();
        }
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
        Path fullPath = filePath.resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(fullPath)) {

            ZipEntry zipEntry = new ZipEntry(fileName.toString());
            zipOutputStream.putNextEntry(zipEntry);

            copyData(inputStream, zipOutputStream);

            zipOutputStream.closeEntry();
        }
    }

    private void copyData(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    public List<FileProperties> getFilesList() throws Exception {
        // Проверяем существует ли zip файл
        if (!Files.isRegularFile(zipFile))
            throw new WrongZipFileException();

        List<FileProperties> fileProperties = new ArrayList();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                // Поля "размер" и "сжатый размер" не известны, пока элемент не будет прочитан
                // Давайте вычитаем его в какой-то выходной поток

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                copyData(zipInputStream, outputStream);

                FileProperties file = new FileProperties(zipEntry.getName(), zipEntry.getSize(), zipEntry.getCompressedSize(), zipEntry.getMethod());
                fileProperties.add(file);
                zipEntry = zipInputStream.getNextEntry();
            }
        }
        return fileProperties;
    }

    public void extractAll(Path outputFolder) throws Exception {
        // Проверяем существует ли zip файл
        if (!Files.isRegularFile(zipFile))
            throw new WrongZipFileException();

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            // Создаем директорию вывода, если она не существует
            if (Files.notExists(outputFolder))
                Files.createDirectories(outputFolder);

            // Проходимся по содержимому zip потока (файла)
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                Path fileFullName = outputFolder.resolve(fileName);

                // Создаем необходимые директории
                Path parent = fileFullName.getParent();
                if (Files.notExists(parent))
                    Files.createDirectories(parent);

                try (OutputStream outputStream = Files.newOutputStream(fileFullName)) {
                    copyData(zipInputStream, outputStream);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    public void removeFile(Path path) throws Exception {
        removeFiles(Collections.singletonList(path));
    }

    public void removeFiles(List<Path> pathList) throws Exception {
        // Проверяем существует ли zip файл
        if (!Files.isRegularFile(zipFile))
            throw new WrongZipFileException();

        // Создаем временный файл
        Path tempZipfile = Files.createTempFile(null, null);

        try (ZipOutputStream outputStream = new ZipOutputStream(Files.newOutputStream(tempZipfile))) {
            try (ZipInputStream inputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                ZipEntry zipEntry = inputStream.getNextEntry();

                while (zipEntry != null) {
                    Path archiveFile = Paths.get(zipEntry.getName());

                    if (!pathList.contains(archiveFile)) {
                        String fileName = zipEntry.getName();
                        outputStream.putNextEntry(new ZipEntry(fileName));

                        copyData(inputStream, outputStream);

                        outputStream.closeEntry();
                        inputStream.closeEntry();
                    } else {
                        ConsoleHelper.writeMessage(String.format("Файл '%s' удален из архива.", archiveFile.toString()));
                    }
                    zipEntry = inputStream.getNextEntry();
                }
            }
        }
        // Перемещаем временный файл на место оригинального
        Files.move(tempZipfile, zipFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public void addFile(Path absolutePath) throws Exception{
        addFiles(Collections.singletonList(absolutePath));
    }

    public void addFiles(List<Path> absolutePathList) throws Exception {

        // Проверяем существует ли zip файл
        if (!Files.isRegularFile(zipFile))
            throw new WrongZipFileException();

        Path tempArchive = Files.createTempFile(null, null);
        List<Path> archiveFiles = new ArrayList<>();

        try (ZipOutputStream outputStream = new ZipOutputStream(Files.newOutputStream(tempArchive))) {
            try (ZipInputStream inputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                ZipEntry zipEntry = inputStream.getNextEntry();

                while (zipEntry != null) {
                    String fileName = zipEntry.getName();
                    archiveFiles.add(Paths.get(fileName));

                    outputStream.putNextEntry(new ZipEntry(fileName));
                    copyData(inputStream, outputStream);

                    inputStream.closeEntry();
                    outputStream.closeEntry();

                    zipEntry = inputStream.getNextEntry();
                }
            }
            // Архивируем новые файлы
            for (Path file : absolutePathList) {
                if (Files.isRegularFile(file)) {
                    if (archiveFiles.contains(file.getFileName()))
                        ConsoleHelper.writeMessage(String.format("Файл '%s' уже существует в архиве.", file.toString()));
                    else {
                        addNewZipEntry(outputStream, file.getParent(), file.getFileName());
                        ConsoleHelper.writeMessage(String.format("Файл '%s' добавлен в архиве.", file.toString()));
                    }
                } else
                    throw new PathIsNotFoundException();
            }
        }

        // Перемещаем временный файл на место оригинального
        Files.move(tempArchive, zipFile, StandardCopyOption.REPLACE_EXISTING);
    }
}

