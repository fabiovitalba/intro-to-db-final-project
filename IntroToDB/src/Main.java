import database.PostgreSQLConnector;
import database.PostgreSQLStatementBuilder;
import view.TerminalOutputManager;

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
                TerminalOutputManager.printActivityMenu();
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
            TerminalOutputManager.printErrorWithStackTrace("PostgreSQL Driver could not be loaded. Error:", e);
        } catch (SQLException e) {
            TerminalOutputManager.printErrorWithStackTrace("Database Connection could not be established. Error:", e);
        }
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
            TerminalOutputManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalOutputManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllTasksActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalOutputManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalOutputManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllProjectsWithMilestonesActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getProjectWithMilestoneList(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalOutputManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalOutputManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }
}
