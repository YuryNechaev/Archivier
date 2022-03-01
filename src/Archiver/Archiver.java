package Archiver;

import Archiver.exception.WrongZipFileException;

import java.io.IOException;

public class Archiver {
    public static void main(String[] args) throws Exception {

        Operation operation = null;

        do {
            try {
                operation = askOperation();
                CommandExecutor.execute(operation);
            } catch (WrongZipFileException e) {
                ConsoleHelper.writeMessage("Вы не выбрали файл архива или выбрали неверный файл.");
            }catch (Exception i){
                ConsoleHelper.writeMessage("Произошла ошибка. Проверьте введенные данные.");
            }

        } while(operation!=Operation.EXIT);


    }
    public static Operation askOperation() throws IOException {
        ConsoleHelper.writeMessage("Введите номер операции");
        ConsoleHelper.writeMessage(Operation.CREATE.ordinal()+ " - упаковать файлы в архив");
        ConsoleHelper.writeMessage(Operation.ADD.ordinal()+ " - добавить файл в архив");
        ConsoleHelper.writeMessage(Operation.REMOVE.ordinal()+ " - удалить файл из архива");
        ConsoleHelper.writeMessage(Operation.EXTRACT.ordinal()+ " - распаковать архив");
        ConsoleHelper.writeMessage(Operation.CONTENT.ordinal()+ " - просмотреть содержимое архива");
        ConsoleHelper.writeMessage(Operation.EXIT.ordinal()+ " - выход");

        return Operation.values()[ConsoleHelper.readInt()];

    }
}
