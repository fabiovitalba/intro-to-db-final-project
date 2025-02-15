package view;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class TerminalIOManager {
    private final Scanner scanner;

    public TerminalIOManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public String askUserForString(String valueQuestion) {
        System.out.println(valueQuestion);
        return scanner.next();
    }

    /**
     * This function asks the user for an input. The input may be null or a number. Any other string that cannot be
     * converted to a number, will return -1.
     * @param valueQuestion Question to ask the user for the input.
     * @return User input converted to integer
     */
    public int askUserForInt(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        if (scannerInput.equalsIgnoreCase("null"))
            return 0;
        try {
            return Integer.parseInt(scannerInput);
        } catch (NumberFormatException e) {
            printError(scannerInput + " cannot be converted to a number.");
            return -1;
        }
    }

    public Date askUserForDate(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        return switch (scannerInput.toLowerCase()) {
            case "null" -> null;
            case "today" -> Date.valueOf(LocalDate.now());
            case "yesterday" -> Date.valueOf(LocalDate.now().minusDays(1));
            case "tomorrow" -> Date.valueOf(LocalDate.now().plusDays(1));
            default -> Date.valueOf(LocalDate.parse(scannerInput));
        };
    }

    public Time askUserForTime(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        return switch (scannerInput.toLowerCase()) {
            case "null" -> null;
            case "now" -> Time.valueOf(LocalTime.now());
            default -> Time.valueOf(LocalTime.parse(scannerInput));
        };
    }

    public static void printActivityMenu() {
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("| 1) Create a Task                                                             |");
        System.out.println("| 2) Release a Task                                                            |");
        System.out.println("| 3) Assign a Task to a Developer                                              |");
        System.out.println("| 4) Assign a time Estimate for a Task                                         |");
        System.out.println("| 5) Assign worked time to a Task of a Developer                               |");
        System.out.println("| 6) Delete a Task                                                             |");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("| 7) Find overdue Tasks in a Project                                           |");
        System.out.println("| 8) Find overdue Tasks in a Project (with Progress)                           |");
        System.out.println("| 9) Find all Tasks without estimate in a Project                              |");
        System.out.println("| 10) Find all assigned, workable Tasks for all Developers                      |");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("| 11) List all Developers                                                      |");
        System.out.println("| 12) List all Tasks                                                           |");
        System.out.println("| 13) List all Tasks (with Progress)                                           |");
        System.out.println("| 14) List all Projects with Milestones                                        |");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("| 99) Exit application                                                         |");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println("|------------------------------------------------------------------------------|");
        System.out.println();
    }

    public static void printSuccess(String message) {
        System.out.println("\033[0;1m\u001B[32m" + message + "\u001B[0m\n\n");
    }

    public static void printError(String errorMessage) {
        System.out.println("\033[0;1m\u001B[31m" + errorMessage + "\u001B[0m\n\n");
    }

    public static void printErrorWithStackTrace(String errorMessage, Exception e) {
        //e.printStackTrace();
        System.out.println("\033[0;1m\u001B[31m" + errorMessage + " " + e.getMessage() + "\u001B[0m\n\n");
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
        int noOfColumns = rsMetaData.getColumnCount();

        System.out.println("\033[0;1m\u001B[34m");
        // Column Captions
        for (int i = 1; i <= noOfColumns; i++) {
            System.out.print("|");
            System.out.print(padTableCell(rsMetaData.getColumnLabel(i), rsMetaData.getColumnDisplaySize(i) + 1));
        }
        System.out.println("|\u001B[0m\u001B[34m");

        // Table Rows
        while(resultSet.next()) {
            for (int i = 1; i <= noOfColumns; i++) {
                System.out.print("|");
                System.out.print(padTableCell(resultSet.getString(i), rsMetaData.getColumnDisplaySize(i) + 1));
            }
            System.out.println("|");
        }
        System.out.println("\u001B[0m\n\n");
    }

    private static String padTableCell(String cellValue, int width) {
        int localWidth = width;
        if (localWidth > 50)
            localWidth = 50;
        String localCellValue = cellValue;
        if ((cellValue != null) && (localCellValue.length() > localWidth))
            localCellValue = localCellValue.substring(0,localWidth);
        return String.format(" %1$-" + localWidth + "s ", localCellValue);
    }
}
