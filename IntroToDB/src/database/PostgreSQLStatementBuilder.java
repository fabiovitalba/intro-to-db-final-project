package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLStatementBuilder {
    private PostgreSQLStatementBuilder() {}

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
