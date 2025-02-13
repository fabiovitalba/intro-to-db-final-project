package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLStatementBuilder {
    private PostgreSQLStatementBuilder() {}

    public static PreparedStatement insertTask(Connection conn, int taskId, String desc, String status, Date dueDate, Date dateReleased, Date creationDate, Date completionDate, String projectCode, String milestoneCode, int developerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO Task VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO BugfixTask VALUES (?, ?)");
        prepStmt.setInt(1, taskId);
        prepStmt.setString(2, impact);
        return prepStmt;
    }

    public static PreparedStatement insertFeatureTask(Connection conn, int taskId, String complexity) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO FeatureTask VALUES (?, ?)");
        prepStmt.setInt(1, taskId);
        prepStmt.setString(2, complexity);
        return prepStmt;
    }

    public static PreparedStatement insertCodeReviewTask(Connection conn, int taskId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO CodeReviewTask VALUES (?)");
        prepStmt.setInt(1, taskId);
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

    // TODO

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
                    "ORDER BY D.name ASC, T.dueDate ASC"
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
