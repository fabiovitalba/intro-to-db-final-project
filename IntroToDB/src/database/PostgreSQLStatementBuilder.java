package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLStatementBuilder {
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
                "SELECT P.code as projectCode, P.description as projectDescription, " +
                    "  P.startingDate as projectStartingDate, P.endingDate as projectEndingDate," +
                    "  P.status as projectStatus, M.code as milestone, M.startingDate as milestoneStartingDate," +
                    "  M.endingDate as milestoneEndingDate, M.critical as critical " +
                    "FROM Milestone M " +
                    "JOIN Project P ON M.project = P.code;"
        );
    }
}
