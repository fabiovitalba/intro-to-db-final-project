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
                "SELECT P.code as ProjectCode, P.description as ProjectDescription, " +
                    "  P.startingDate as ProjectStartingDate, P.endingDate as ProjectEndingDate," +
                    "  P.status as ProjectStatus, M.code as Milestone, M.startingDate as MilestoneStartingDate," +
                    "  M.endingDate as MilestoneEndingDate, M.critical as Critical " +
                    "FROM Milestone M " +
                    "JOIN Project P ON M.project = P.code;"
        );
    }
}
