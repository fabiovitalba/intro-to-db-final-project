package database;

import java.sql.*;

public class PostgreSQLStatementBuilder {
    private PostgreSQLStatementBuilder() {}

    public static PreparedStatement insertTask(Connection conn, int taskId, String desc, String status, Date dueDate, Date dateReleased, Date creationDate, Date completionDate, String projectCode, String milestoneCode, int developerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO Task VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        prepStmt.setInt(1, taskId);
        prepStmt.setString(2, desc);
        prepStmt.setString(3, status);
        prepStmt.setDate(4, dueDate);
        if (dateReleased == null)
            prepStmt.setNull(5,0);
        else
            prepStmt.setDate(5, dateReleased);
        prepStmt.setDate(6, creationDate);
        if (completionDate == null)
            prepStmt.setNull(7,0);
        else
            prepStmt.setDate(7, completionDate);
        prepStmt.setString(8, projectCode);
        prepStmt.setString(9, milestoneCode);
        if (developerId <= 0)
            prepStmt.setNull(10,0);
        else
            prepStmt.setInt(10, developerId);
        return prepStmt;
    }

    public static PreparedStatement insertBugfixTask(Connection conn, int taskId, String impact) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO BugfixTask VALUES (?, ?);");
        prepStmt.setInt(1, taskId);
        prepStmt.setString(2, impact);
        return prepStmt;
    }

    public static PreparedStatement insertFeatureTask(Connection conn, int taskId, String complexity) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO FeatureTask VALUES (?, ?);");
        prepStmt.setInt(1, taskId);
        prepStmt.setString(2, complexity);
        return prepStmt;
    }

    public static PreparedStatement insertCodeReviewTask(Connection conn, int taskId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO CodeReviewTask VALUES (?);");
        prepStmt.setInt(1, taskId);
        return prepStmt;
    }

    public static PreparedStatement releaseTask(Connection conn, int taskId, Date dateReleased) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "UPDATE Task " +
                    "SET status = 'authorized', dateReleased = ? " +
                    "WHERE (taskId = ?) AND (status = 'not authorized');"
        );
        prepStmt.setDate(1, dateReleased);
        prepStmt.setInt(2, taskId);
        return prepStmt;
    }

    public static PreparedStatement assignTask(Connection conn, int taskId, int developerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "UPDATE Task " +
                "SET assignedDeveloper = ? " +
                "WHERE taskId = ?;"
        );
        prepStmt.setInt(1, developerId);
        prepStmt.setInt(2, taskId);
        return prepStmt;
    }

    public static PreparedStatement estimateTask(Connection conn, int taskId, int developerId, int estimateInHrs, Date dateOfEstimate) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "INSERT INTO Estimates VALUES (?, ?, ?, ?);"
        );
        prepStmt.setInt(1, taskId);
        prepStmt.setInt(2, developerId);
        prepStmt.setInt(3, estimateInHrs);
        prepStmt.setDate(4, dateOfEstimate);
        return prepStmt;
    }

    public static PreparedStatement deleteTask(Connection conn, int taskId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("DELETE FROM Task WHERE taskId = ?;");
        prepStmt.setInt(1, taskId);
        return prepStmt;
    }

    public static PreparedStatement insertTimeLog(Connection conn, int taskId, int developerId, Date startDate, Time startTime, Date endDate, Time endTime, double duration) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO TimeLog VALUES (?, ?, ?, ?, ?, ?, ?);");
        prepStmt.setInt(1, developerId);
        prepStmt.setInt(2, taskId);
        prepStmt.setDate(3, startDate);
        prepStmt.setTime(4, startTime);
        prepStmt.setDate(5, endDate);
        prepStmt.setTime(6, endTime);
        prepStmt.setDouble(7, duration);
        return prepStmt;
    }

    public static PreparedStatement getOverdueTasksInProject(Connection conn, String projectCode, Date referenceDate) throws  SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT T.taskId, T.description, T.assignedDeveloper, T.dueDate, T.status " +
                "FROM Task T " +
                "WHERE (T.project = ?) AND " +
                    "(T.dueDate < ?) AND " +
                    "(T.status in ('authorized','in progress','test'));"
        );
        prepStmt.setString(1, projectCode);
        prepStmt.setDate(2, referenceDate);
        return prepStmt;
    }

    public static PreparedStatement getOverdueTasksWithProgressInProject(Connection conn, String projectCode, Date referenceDate) throws  SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT TWP.taskId, TWP.description, D.name, TWP.dueDate, TWP.status, TWP.workedHrs, " +
                        "TWP.estimateHrs, TWP.progressPerc " +
                    "FROM ( " +
                        "SELECT T.taskId, T.description, T.assignedDeveloper, T.dueDate, T.status, " +
                            "SUM(TL.timeWorkedHrs) as workedHrs, SUM(E.estimatedEffortHrs) as estimateHrs, " +
                            "ROUND((SUM(TL.timeWorkedHrs)/SUM(E.estimatedEffortHrs))*100,2) as progressPerc " +
                        "FROM Task T " +
                        "LEFT JOIN TimeLog TL ON TL.task = T.taskId " +
                        "LEFT JOIN Estimates E ON E.task = T.taskId " +
                        "WHERE (T.project = ?) AND " +
                            "(T.dueDate < ?) AND " +
                            "(T.status in ('authorized','in progress','test')) " +
                        "GROUP BY T.taskId" +
                    ") TWP " +
                    "LEFT JOIN Developer D ON TWP.assignedDeveloper = D.employeeId;"
        );
        prepStmt.setString(1, projectCode);
        prepStmt.setDate(2, referenceDate);
        return prepStmt;
    }

    public static PreparedStatement getUnestimatedTasksForProject(Connection conn, String projectCode) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT T.project, T.taskId, T.description, T.status, T.creationDate, T.dueDate " +
                    "FROM Task T " +
                    "LEFT JOIN Estimates E ON E.task = T.taskId " +
                    "WHERE (T.project = ?) AND " +
                        " ((E.estimatedEffortHrs <= 0) OR (E.estimatedEffortHrs IS NULL));"
        );
        prepStmt.setString(1, projectCode);
        return prepStmt;
    }

    public static PreparedStatement getDeveloperWorkableTasksStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT D.name as developer, T.taskId, T.description, T.status, T.dueDate " +
                    "FROM Developer D " +
                    "JOIN Task T ON T.assignedDeveloper = D.employeeId " +
                    "WHERE T.status in ('authorized','in progress') " +
                    "ORDER BY D.name ASC, T.dueDate ASC;"
        );
    }

    public static PreparedStatement getDeveloperListStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT * FROM Developer;"
        );
    }

    public static PreparedStatement getTaskListStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT * FROM Task;"
        );
    }

    public static PreparedStatement getUnreleasedTaskListStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT * " +
                    "FROM Task " +
                    "WHERE status = 'not authorized';",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
    }

    public static PreparedStatement getTaskListWithProgress(Connection conn) throws  SQLException {
        return conn.prepareStatement(
            "SELECT TWP.taskId, TWP.description, TWP.assignedDeveloper, D.name, TWP.dueDate, TWP.status, TWP.workedHrs, " +
                    "TWP.estimateHrs, TWP.progressPerc " +
                    "FROM ( " +
                        "SELECT T.taskId, T.description, T.assignedDeveloper, T.dueDate, T.status, " +
                            "SUM(TL.timeWorkedHrs) as workedHrs, SUM(E.estimatedEffortHrs) as estimateHrs, " +
                            "ROUND((SUM(TL.timeWorkedHrs)/SUM(E.estimatedEffortHrs))*100,2) as progressPerc " +
                        "FROM Task T " +
                        "LEFT JOIN TimeLog TL ON TL.task = T.taskId " +
                        "LEFT JOIN Estimates E ON E.task = T.taskId " +
                        "GROUP BY T.taskId " +
                        "ORDER BY T.taskId" +
                    ") TWP " +
                    "LEFT JOIN Developer D ON TWP.assignedDeveloper = D.employeeId;"
        );
    }

    public static PreparedStatement getProjectWithMilestoneList(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT P.code as project, P.description as pDescription, " +
                    "  P.startingDate as pStartingDate, P.endingDate as pEndingDate," +
                    "  P.status as pStatus, M.code as milestone, M.startingDate as mStartingDate," +
                    "  M.endingDate as mEndingDate, M.critical as mCritical " +
                    "FROM Milestone M " +
                    "JOIN Project P ON M.project = P.code " +
                    "ORDER BY project, mStartingDate, mEndingDate, milestone;"
        );
    }
}
