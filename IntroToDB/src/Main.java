import database.PostgreSQLConnector;
import database.PostgreSQLStatementBuilder;
import view.TerminalIOManager;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            boolean exitProgram = false;
            int timeout = 10; // Seconds to wait before invalidating the connection
            TerminalIOManager terminalIOManager = new TerminalIOManager(new Scanner(System.in));
            Connection conn = PostgreSQLConnector.openConnection();
            while(!exitProgram && conn.isValid(timeout)) {
                System.out.println("Project Task management for IT Company.");
                TerminalIOManager.printActivityMenu();
                String selActivity = terminalIOManager.askUserForString("Please select the activity to perform:");
                int activity = Integer.parseInt(selActivity);
                switch (activity) {
                    case 1:
                        createATaskActivity(conn, terminalIOManager);
                        break;
                    case 2:
                        createADeveloperActivity(conn, terminalIOManager);
                        break;
                    case 3:
                        assignATaskActivity(conn, terminalIOManager);
                        break;
                    case 4:
                        findOverdueTasksActivity(conn, terminalIOManager);
                        break;
                    case 5:
                        assignAPeriodOfTimeActivity(conn, terminalIOManager);
                        break;
                    case 6:
                        findAllTaskWithoutEstimateActivity(conn, terminalIOManager);
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
            TerminalIOManager.printErrorWithStackTrace("PostgreSQL Driver could not be loaded. Error:", e);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("Database Connection could not be established. Error:", e);
        }
    }

    private static void createATaskActivity(Connection conn, TerminalIOManager terminalIOManager) {

    }

    private static void createADeveloperActivity(Connection conn, TerminalIOManager terminalIOManager) {

    }

    private static void assignATaskActivity(Connection conn, TerminalIOManager terminalIOManager) {

    }

    private static void findOverdueTasksActivity(Connection conn, TerminalIOManager terminalIOManager) {

    }

    private static void assignAPeriodOfTimeActivity(Connection conn, TerminalIOManager terminalIOManager) {

    }

    private static void findAllTaskWithoutEstimateActivity(Connection conn, TerminalIOManager terminalIOManager) {
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getUnestimatedTasksForProject(conn);
                preparedStatement.setString(1, projectCode);
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    private static void findAllAssignedWorkableTasksActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperWorkableTasksStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllDevelopersActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllTasksActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllProjectsWithMilestonesActivity(Connection conn) {
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getProjectWithMilestoneList(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }
}
