import database.PostgreSQLConnector;
import database.PostgreSQLStatementBuilder;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            boolean exitProgram = false;
            int timeout = 10; // Seconds to wait before invalidating the connection
            Scanner scan = new Scanner(System.in);
            Connection conn = PostgreSQLConnector.openConnection();
            while(!exitProgram && conn.isValid(timeout)) {
                System.out.println("Project Task management for IT Company.");
                printActivityMenu();
                System.out.println("Please select the activity to perform:");
                String selActivity = scan.next();
                int activity = Integer.parseInt(selActivity);
                switch (activity) {
                    case 1:
                        createATaskActivity(conn);
                        break;
                    case 2:
                        createADeveloperActivity(conn);
                        break;
                    case 3:
                        assignATaskActivity(conn);
                        break;
                    case 4:
                        findOverdueTasksActivity(conn);
                        break;
                    case 5:
                        assignAPeriodOfTimeActivity(conn);
                        break;
                    case 6:
                        findAllTaskWithoutEstimateActivity(conn);
                        break;
                    case 7:
                        findAllAssignedWorkableTasksActivity(conn);
                        break;
                    case 8:
                        listAllDevelopersActivity(conn);
                        break;
                    case 9:
                        listAllTasksActivity(conn);
                        break;
                    case 10:
                        listAllProjectsWithMilestonesActivity(conn);
                        break;
                    case 99:
                        exitProgram = true;
                        break;
                    default:
                        System.out.println("Selected activity does not exist. Please retry.");
                }
            }
            conn.close();
        } catch (ClassNotFoundException e) {
            printErrorWithStackTrace("PostgreSQL Driver could not be loaded. Error:", e);
        } catch (SQLException e) {
            printErrorWithStackTrace("Database Connection could not be established. Error:", e);
        }
    }

    private static void printActivityMenu() {
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

    private static void createATaskActivity(Connection conn) {

    }

    private static void createADeveloperActivity(Connection conn) {

    }

    private static void assignATaskActivity(Connection conn) {

    }

    private static void findOverdueTasksActivity(Connection conn) {

    }

    private static void assignAPeriodOfTimeActivity(Connection conn) {

    }

    private static void findAllTaskWithoutEstimateActivity(Connection conn) {

    }

    private static void findAllAssignedWorkableTasksActivity(Connection conn) {

    }

    private static void listAllDevelopersActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            printResultSet(resultSet);
        } catch (SQLException e) {
            printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllTasksActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            printResultSet(resultSet);
        } catch (SQLException e) {
            printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllProjectsWithMilestonesActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getProjectWithMilestoneList(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            printResultSet(resultSet);
        } catch (SQLException e) {
            printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void printErrorWithStackTrace(String errorMessage, Exception e) {
        System.out.println(errorMessage + " " + e.getMessage());
        e.printStackTrace();
    }

    private static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
        int noOfColumns = rsMetaData.getColumnCount();

        System.out.println();
        // Column Captions
        for (int i = 0; i < noOfColumns; i++) {
            System.out.print("|");
            System.out.print(padTableCell(rsMetaData.getColumnLabel(i),25));
        }
        System.out.println("|");

        // Table Rows
        while(resultSet.next()) {
            for (int i = 0; i < noOfColumns; i++) {
                System.out.print("|");
                System.out.print(padTableCell(resultSet.getString(i),25));
            }
            System.out.println("|");
        }
    }

    private static String padTableCell(String cellValue, int width) {
        return String.format("%1$-" + width + "s", cellValue);
    }
}
