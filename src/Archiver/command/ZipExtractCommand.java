package Archiver.command;

import Archiver.ConsoleHelper;
import Archiver.ZipFileManager;
import Archiver.exception.WrongZipFileException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipExtractCommand extends ZipCommand{
    @Override
    public void execute() throws Exception {

        try {
            ConsoleHelper.writeMessage("аспаковка архива");
            ZipFileManager zipFileManager = getZipFileManager();
            ConsoleHelper.writeMessage("Укажите место для распаковки архива");
            Path extractPath = Paths.get(ConsoleHelper.readString());
            zipFileManager.extractAll(extractPath);
            ConsoleHelper.writeMessage("Архив распакован");
        }catch (WrongZipFileException e) {
            ConsoleHelper.writeMessage("Вы неверно указали путь для извлечения архива");
        }

    }
}
