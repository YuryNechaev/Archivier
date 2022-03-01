package Archiver.command;

import Archiver.ConsoleHelper;
import Archiver.ZipFileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipRemoveCommand extends ZipCommand{
    @Override
    public void execute() throws Exception {

            ConsoleHelper.writeMessage("Удаление файла из архива");
            ZipFileManager zipFileManager = getZipFileManager();
            ConsoleHelper.writeMessage("Укажите файл для удаления");
            Path removePath = Paths.get(ConsoleHelper.readString());
            zipFileManager.removeFile(removePath);
            ConsoleHelper.writeMessage("Файл удален");
    }
}
