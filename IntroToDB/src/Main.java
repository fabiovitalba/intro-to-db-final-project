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
                        TerminalIOManager.printError("Selected activity does not exist. Please retry.");
                }
            }
            conn.close();
        } catch (ClassNotFoundException e) {
            TerminalIOManager.printErrorWithStackTrace("PostgreSQL Driver could not be loaded. Error:", e);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("Database Connection could not be established. Error:", e);
        }
    }

    /**
     * Allows the user to create new tasks. For date fields the user may input values using "today", "tomorrow",
     * or "yesterday" keywords for easier input.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void createATaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Create Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        if (taskId < 0)
            return;
        String taskDesc = terminalIOManager.askUserForString("Description: ");
        String taskStatus = terminalIOManager.askUserForString("Status: ");
        Date dueDate = terminalIOManager.askUserForDate("Due Date (YYYY-MM-DD): ");
        Date creationDate = Date.valueOf(LocalDate.now());
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        String milestoneCode = terminalIOManager.askUserForString("Milestone code: ");
        int assignedDeveloperId = terminalIOManager.askUserForInt("Assigned Developer ID: ");
        if (assignedDeveloperId < 0)
            return;
        String taskType = terminalIOManager.askUserForString("Type (Bugfix, Feature, CodeReview):");

        try {
            conn.setAutoCommit(false); // bundle transactions

            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.insertTask(conn,
                    taskId, taskDesc, taskStatus, dueDate, null, creationDate, null,
                    projectCode, milestoneCode, assignedDeveloperId);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            switch (taskType.toLowerCase()) {
                case "bugfix":
                    String impact = terminalIOManager.askUserForString("Impact (low, medium, high): ");
                    PreparedStatement bugfixPreparedStatement = PostgreSQLStatementBuilder.insertBugfixTask(conn, taskId, impact);
                    bugfixPreparedStatement.executeUpdate();
                    bugfixPreparedStatement.close();
                    break;
                case "feature":
                    String complexity = terminalIOManager.askUserForString("Complexity (low, medium, high): ");
                    PreparedStatement featurePreparedStatement = PostgreSQLStatementBuilder.insertFeatureTask(conn, taskId, complexity);
                    featurePreparedStatement.executeUpdate();
                    featurePreparedStatement.close();
                    break;
                case "codereview":
                    PreparedStatement codeReviewPrepStatement = PostgreSQLStatementBuilder.insertCodeReviewTask(conn, taskId);
                    codeReviewPrepStatement.executeUpdate();
                    codeReviewPrepStatement.close();
                    break;
            }

            conn.commit(); // commit transaction
            conn.setAutoCommit(true); // restore to default
            TerminalIOManager.printSuccess("Task was added!");
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ignored) { }
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or executed. Error:", e);

        }
    }

    /**
     * This activity allows the user to see the list of unreleased tasks (in status "not authorized").
     * The user then may choose one of the tasks of said list, and the task will be updated to status "authorized".
     * Additionally, the dateReleased is set automatically to the current date.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void releaseTaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Release Task");
        System.out.println("This is the list of unreleased Tasks:");

        ResultSet resultSet;
        PreparedStatement tasksPreparedStatement;
        try {
            tasksPreparedStatement = PostgreSQLStatementBuilder.getUnreleasedTaskListStatement(conn);
            resultSet = tasksPreparedStatement.executeQuery();
            if (!resultSet.first()) {
                TerminalIOManager.printError("There are no unreleased Tasks to release.");
                return;
            }
            resultSet.beforeFirst(); // reset position
            TerminalIOManager.printResultSet(resultSet);
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            return;
        }

        int taskId = terminalIOManager.askUserForInt("Task ID to release: ");
        if (taskId < 0)
            return;

        try {
            boolean taskCanBeReleased = checkIfIntValueInResultSet(resultSet, "taskId", taskId);
            tasksPreparedStatement.close();
            if (!taskCanBeReleased) {
                TerminalIOManager.printError("Task " + taskId + " cannot be released.");
                return;
            }
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("ResultSet cannot be interpreted. Error:", e);
            return;
        }

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.releaseTask(conn, taskId, Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            TerminalIOManager.printSuccess("Task was released!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or executed. Error:", e);
        }
    }

    /**
     * Allows the user to provide a Task and Developer, said Task is then assigned to provided Developer.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void assignATaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Assign a Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        if (taskId < 0)
            return;
        int developerId = terminalIOManager.askUserForInt("Developer ID: ");
        if (developerId < 0)
            return;

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.assignTask(conn, taskId, developerId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            TerminalIOManager.printSuccess("Task was assigned!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or executed. Error:", e);
        }
    }

    /**
     * Allows the user to delete a Task.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void deleteTaskActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Delete Task");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        if (taskId < 0)
            return;

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.deleteTask(conn, taskId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            TerminalIOManager.printSuccess("Task was deleted!");
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or executed. Error:", e);
        }
    }

    /**
     * Lists all Tasks that are over their Due Date.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void findOverdueTasksActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find overdue Tasks in a Project");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getOverdueTasksInProject(conn, projectCode, Date.valueOf(LocalDate.now()));
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
                preparedStatement.close();
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    /**
     * Lists all Tasks that are over their Due Date and displays their progress calculated by used hours compared to
     * estimated hours.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void findOverdueTasksWithProgressActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find overdue Tasks in a Project (with Progress)");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getOverdueTasksWithProgressInProject(conn, projectCode, Date.valueOf(LocalDate.now()));
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
                preparedStatement.close();
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    /**
     * Assigns a period of time as worked time to a Task by a developer.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void assignAPeriodOfTimeActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Assign worked time to a Task of a Developer");
        int taskId = terminalIOManager.askUserForInt("Task ID: ");
        if (taskId < 0)
            return;
        int developerId = terminalIOManager.askUserForInt("Developer ID: ");
        if (developerId < 0)
            return;
        Date startDate = terminalIOManager.askUserForDate("Start Date: ");
        Time startTime = terminalIOManager.askUserForTime("Start Time: ");
        Date endDate = terminalIOManager.askUserForDate("End Date: ");
        Time endTime = terminalIOManager.askUserForTime("End Time: ");

        LocalDateTime startDateTime = LocalDateTime.of(startDate.toLocalDate(), startTime.toLocalTime());
        LocalDateTime endDateTime = LocalDateTime.of(endDate.toLocalDate(), endTime.toLocalTime());
        Duration duration = Duration.between(startDateTime, endDateTime);
        double hours = Math.round(duration.toSeconds() * 100.0 / 3600.0) / 100.0; // calculate Hours and round to two decimals

        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.insertTimeLog(conn, taskId, developerId, startDate, startTime, endDate, endTime, hours);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     * Lists all Tasks without an estimate within a provided Project.
     * @param conn Already established database connection
     * @param terminalIOManager Object to handle Input with Terminal
     */
    private static void findAllTaskWithoutEstimateActivity(Connection conn, TerminalIOManager terminalIOManager) {
        System.out.println("Selected: Find Tasks without an estimate in a Project");
        String projectCode = terminalIOManager.askUserForString("Project code: ");
        if (!projectCode.isEmpty()) {
            try {
                PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getUnestimatedTasksForProject(conn, projectCode);
                ResultSet resultSet = preparedStatement.executeQuery();
                TerminalIOManager.printResultSet(resultSet);
                preparedStatement.close();
            } catch (SQLException e) {
                TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
            }
        } else {
            TerminalIOManager.printError("Provided Project Code was empty.");
        }
    }

    /**
     * Lists all Tasks in status "authorized" or "in progress", sorted by developer.
     * @param conn Already established database connection
     */
    private static void findAllAssignedWorkableTasksActivity(Connection conn) {
        System.out.println("Selected: List all workable Tasks per Developer");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperWorkableTasksStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     * Lists all Developers.
     * @param conn Already established database connection
     */
    private static void listAllDevelopersActivity(Connection conn) {
        System.out.println("Selected: List all Developers");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getDeveloperListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     * Lists all Tasks.
     * @param conn Already established database connection
     */
    private static void listAllTasksActivity(Connection conn) {
        System.out.println("Selected: List all Tasks");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListStatement(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     * Lists all Tasks with a progress % calculated based on the used hours compared to the estimated hours.
     * @param conn Already established database connection
     */
    private static void listAllTasksWProgressActivity(Connection conn) {
        System.out.println("Selected: List all Tasks (with progress)");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getTaskListWithProgress(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     * Lists all Milestones together with their Project's information. List is ordered by Project, Milestone.
     * @param conn Already established database connection
     */
    private static void listAllProjectsWithMilestonesActivity(Connection conn) {
        System.out.println("Selected: List all Projects (with Milestones)");
        try {
            PreparedStatement preparedStatement = PostgreSQLStatementBuilder.getProjectWithMilestoneList(conn);
            ResultSet resultSet = preparedStatement.executeQuery();
            TerminalIOManager.printResultSet(resultSet);
            preparedStatement.close();
        } catch (SQLException e) {
            TerminalIOManager.printErrorWithStackTrace("SQL Statement could not be prepared, or evaluated. Error:", e);
        }
    }

    /**
     *
     * @param resultSet Data to be searched, must be `TYPE_SCROLL_SENSITIVE`.
     * @param columnLabel Label for the column in `resultSet` to be searched in.
     * @param valueToSearch Value to be searched.
     * @return `true` if `valueToSearch` is present in some field of column `columnLabel` within the `resultSet`. `false` otherwise.
     * @throws SQLException In case provided `columnLabel` does not exist in `resultSet`.
     */
    private static boolean checkIfIntValueInResultSet(ResultSet resultSet, String columnLabel, int valueToSearch) throws SQLException {
        int columnId = resultSet.findColumn(columnLabel);
        boolean valueFound = false;
        resultSet.beforeFirst(); // reset position
        while(!valueFound && resultSet.next()) {
            if (resultSet.getInt(columnId) == valueToSearch)
                valueFound = true;
        }
        return valueFound;
    }
}
