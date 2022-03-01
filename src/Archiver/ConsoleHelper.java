package Archiver;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleHelper {

    private static Scanner scanner = new Scanner(System.in);

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString() throws IOException{
        String line = scanner.nextLine();
        return line;
    }

    public static int readInt() throws IOException {
        int number = Integer.parseInt(scanner.nextLine());
        return number;
    }

}
