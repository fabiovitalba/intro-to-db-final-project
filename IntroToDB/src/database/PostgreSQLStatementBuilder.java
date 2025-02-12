package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLStatementBuilder {
    private PostgreSQLStatementBuilder() {}

    public static PreparedStatement getOverdueTasksInProject(Connection conn) throws  SQLException {
        return conn.prepareStatement(
                "SELECT T.taskId, T.description, T.assignedDeveloper, T.dueDate, T.status " +
                "FROM Task T " +
                "WHERE (T.project = ?) AND " +
                    "(T.dueDate < ?) AND " +
                    "(T.status in ('authorized','in progress','test'));"
        );
    }

    public static PreparedStatement getOverdueTasksWithProgressInProject(Connection conn) throws  SQLException {
        return conn.prepareStatement(
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
    }

    // TODO

    public static PreparedStatement getUnestimatedTasksForProject(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "SELECT T.project, T.taskId, T.description, T.status, T.creationDate, T.dueDate " +
                    "FROM Task T " +
                    "LEFT JOIN Estimates E ON E.task = T.taskId " +
                    "WHERE (T.project = ?) AND " +
                        " ((E.estimatedEffortHrs <= 0) OR (E.estimatedEffortHrs IS NULL));"
        );
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
