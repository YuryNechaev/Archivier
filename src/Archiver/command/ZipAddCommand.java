package Archiver.command;

import Archiver.ConsoleHelper;
import Archiver.ZipFileManager;
import Archiver.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipAddCommand extends ZipCommand{
    @Override
    public void execute() throws Exception {

        try {
            ConsoleHelper.writeMessage("Добавление файла в архив.");
            ZipFileManager zipFileManager = getZipFileManager();
            ConsoleHelper.writeMessage("Введите полное имя файла для добавления:");
            Path newFilePath = Paths.get(ConsoleHelper.readString());
            zipFileManager.addFile(newFilePath);
            ConsoleHelper.writeMessage("Файл добавлен в архив");
        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Вы неверно указали имя файла или директории.");
        }

    }
}
