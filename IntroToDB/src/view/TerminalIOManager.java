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

    public int askUserForInt(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        if (scannerInput.equalsIgnoreCase("null"))
            return 0;
        return Integer.parseInt(scannerInput); // might return null
    }

    public Date askUserForDate(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        if (scannerInput.equalsIgnoreCase("null"))
            return null;
        return Date.valueOf(LocalDate.parse(scannerInput));
    }

    public Time askUserForTime(String valueQuestion) {
        System.out.println(valueQuestion);
        String scannerInput = scanner.next();
        if (scannerInput.equalsIgnoreCase("null"))
            return null;
        return Time.valueOf(LocalTime.parse(scannerInput));
    }

    public static void printActivityMenu() {
        System.out.println("1) Create a Task");
        System.out.println("2) Release a Task");
        System.out.println("3) Assign a Task to a Developer");
        System.out.println("4) Find overdue Tasks in a Project");
        System.out.println("5) Find overdue Tasks in a Project (w/ Progress)");
        System.out.println("6) Assign worked time to a Task of a Developer");
        System.out.println("7) Find all Tasks without estimate in a Project");
        System.out.println("8) Find all assigned, workable Tasks for the current week for all Developers");
        System.out.println("9) List all Developers");
        System.out.println("10) List all Tasks");
        System.out.println("11) List all Projects with Milestones");
        System.out.println("-------------------------------------");
        System.out.println("99) Exit application");
        System.out.println();
    }

    public static void printError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public static void printErrorWithStackTrace(String errorMessage, Exception e) {
        System.out.println(errorMessage + " " + e.getMessage());
        e.printStackTrace();
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
        int noOfColumns = rsMetaData.getColumnCount();

        System.out.println();
        // Column Captions
        for (int i = 1; i <= noOfColumns; i++) {
            System.out.print("|");
            System.out.print(padTableCell(rsMetaData.getColumnLabel(i), rsMetaData.getColumnDisplaySize(i) + 1));
        }
        System.out.println("|");

        // Table Rows
        while(resultSet.next()) {
            for (int i = 1; i <= noOfColumns; i++) {
                System.out.print("|");
                System.out.print(padTableCell(resultSet.getString(i), rsMetaData.getColumnDisplaySize(i) + 1));
            }
            System.out.println("|");
        }
        System.out.println();
        System.out.println();
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
