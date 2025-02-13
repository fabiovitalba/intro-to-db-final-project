import database.PostgreSQLConnector;
import database.PostgreSQLStatementBuilder;
import view.TerminalIOManager;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            boolean exitProgram = false;
            int timeout = 10; // Seconds to wait before invalidating the connection
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            TerminalIOManager terminalIOManager = new TerminalIOManager(scanner);
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
                        releaseTaskActivity(conn, terminalIOManager);
                        break;
                    case 3:
                        assignATaskActivity(conn, terminalIOManager);
                        break;
                    case 4:
                        assignAPeriodOfTimeActivity(conn, terminalIOManager);
                        break;
                    case 5:
                        deleteTaskActivity(conn, terminalIOManager);
                        break;
                    case 6:
                        findOverdueTasksActivity(conn, terminalIOManager);
                        break;
                    case 7:
                        findOverdueTasksWithProgressActivity(conn, terminalIOManager);
                        break;
                    case 8:
                        findAllTaskWithoutEstimateActivity(conn, terminalIOManager);
                        break;
                    case 9:
                        findAllAssignedWorkableTasksActivity(conn);
                        break;
                    case 10:
                        listAllDevelopersActivity(conn);
                        break;
                    case 11:
                        listAllTasksActivity(conn);
                        break;
                    case 12:
                        listAllTasksWProgressActivity(conn);
                        break;
                    case 13:
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
        System.out.println("Selected: Create Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        String taskDesc = terminalIOManager.askUserForString("Description: ");
        String taskStatus = terminalIOManager.askUserForString("Status: ");
        Date dueDate = terminalIOManager.askUserForDate("Due Date (YYYY-MM-DD): ");
        Date creationDate = Date.valueOf(LocalDate.now());
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        String milestoneCode = terminalIOManager.askUserForString("Milestone code: ");
        int assignedDeveloperId = terminalIOManager.askUserForInt("Assigned Developer ID: ");
        String taskType = terminalIOManager.askUserForString("Type (Bugfix, Feature, CodeReview):");

        try {
            conn.setAutoCommit(false); // bundle transactions

            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.insertTask(conn,
                    taskId, taskDesc, taskStatus, dueDate, null, creationDate, null,
                    projectCode, milestoneCode, assignedDeveloperId);
            preparedStatement.executeUpdate();

            switch (taskType.toLowerCase()) {
                case "bugfix":
                    String impact = terminalIOManager.askUserForString("Impact (low, medium, high): ");
                    PreparedStatement bugfixPreparedStatement = PostgreSQLStatementBuilder.insertBugfixTask(conn, taskId, impact);
                    bugfixPreparedStatement.executeUpdate();
                    break;
                case "feature":
                    String complexity = terminalIOManager.askUserForString("Complexity (low, medium, high): ");
                    PreparedStatement featurePreparedStatement = PostgreSQLStatementBuilder.insertFeatureTask(conn, taskId, complexity);
                    featurePreparedStatement.executeUpdate();
                    break;
                case "codereview":
                    PreparedStatement codeReviewPrepStatement = PostgreSQLStatementBuilder.insertCodeReviewTask(conn, taskId);
                    codeReviewPrepStatement.executeUpdate();
                    break;
            }

            conn.commit(); // commit transaction
            conn.setAutoCommit(true); // restore to default
            System.out.println("Task was added!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void releaseTaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Release Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.releaseTask(conn, taskId);
            preparedStatement.executeUpdate();
            System.out.println("Task was released!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void assignATaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Assign a Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        int developerId = terminalIOManager.askUserForInt("Developer ID: ");

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.assignTask(conn, taskId, developerId);
            preparedStatement.executeUpdate();
            System.out.println("Task was assigned!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void deleteTaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Delete Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.deleteTask(conn, taskId);
            preparedStatement.executeUpdate();
            System.out.println("Task was deleted!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void findOverdueTasksActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find overdue Tasks in a Project");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getOverdueTasksInProject(conn, projectCode, Date.valueOf(LocalDate.now()));
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    private static void findOverdueTasksWithProgressActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find overdue Tasks in a Project (with Progress)");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getOverdueTasksWithProgressInProject(conn, projectCode, Date.valueOf(LocalDate.now()));
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    private static void assignAPeriodOfTimeActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Assign worked time to a Task of a Developer");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        int developerId = terminalIOManager.askUserForInt("Developer ID: ");
        Date startDate = terminalIOManager.askUserForDate("Start Date: ");
        Time startTime = terminalIOManager.askUserForTime("Start Time: ");
        Date endDate = terminalIOManager.askUserForDate("End Date: ");
        Time endTime = terminalIOManager.askUserForTime("End Time: ");

        LocalDateTime startDateTime = LocalDateTime.of(startDate.toLocalDate(), startTime.toLocalTime());
        LocalDateTime endDateTime = LocalDateTime.of(endDate.toLocalDate(), endTime.toLocalTime());
        Duration duration = Duration.between(startDateTime, endDateTime);
        double hours = Math.round(duration.toSeconds() * 100.0 / 3600.0) / 100.0;

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.insertTimeLog(conn, taskId, developerId, startDate, startTime, endDate, endTime, hours);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void findAllTaskWithoutEstimateActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find Tasks without an estimate in a Project");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getUnestimatedTasksForProject(conn, projectCode);
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
        System.out.println("Selected: List all workable Tasks per Developer");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperWorkableTasksStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllDevelopersActivity(Connection conn) {
        System.out.println("Selected: List all Developers");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllTasksActivity(Connection conn) {
        System.out.println("Selected: List all Tasks");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllTasksWProgressActivity(Connection conn) {
        System.out.println("Selected: List all Tasks (with progress)");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListWithProgress(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    private static void listAllProjectsWithMilestonesActivity(Connection conn) {
        System.out.println("Selected: List all Projects (with Milestones)");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getProjectWithMilestoneList(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }
}
