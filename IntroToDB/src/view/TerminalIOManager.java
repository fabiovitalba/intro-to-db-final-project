package view;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    public static void printActivityMenu() {
        System.out.println("1) Create a Task");
        System.out.println("2) Create a Developer");
        System.out.println("3) Assign a Task to a Developer");
        System.out.println("4) Find overdue Tasks in a Project");
        System.out.println("5) Assign a period of time as worked time to a Task of a Developer");
        System.out.println("6) Find all Tasks without estimate in a Project");
        System.out.println("7) Find all assigned, workable Tasks for the current week for all Developers");
        System.out.println("8) List all Developers");
        System.out.println("9) List all Tasks");
        System.out.println("10) List all Projects with Milestones");
        System.out.println("-------------------------------------");
        System.out.println("99) Exit application");
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
    }

    private static String padTableCell(String cellValue, int width) {
        return String.format(" %1$-" + width + "s ", cellValue);
    }
}
