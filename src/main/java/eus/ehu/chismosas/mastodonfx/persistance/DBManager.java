package eus.ehu.chismosas.mastodonfx.persistance;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DBManager {
    private static final DataSource dataSource;
    private static Connection connection;

    private static PreparedStatement selectAccountIds;
    private static PreparedStatement insertAccount;
    private static PreparedStatement deleteAccount;
    private static PreparedStatement getAccountToken;


    static {
        try {
            SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
            sqLiteDataSource.setUrl("jdbc:sqlite:AccountTokens.db");
            dataSource = sqLiteDataSource;
            connection = dataSource.getConnection();

            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Account (id TEXT PRIMARY KEY, token TEXT UNIQUE)");

            prepareStatements();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void prepareStatements() throws SQLException {
        selectAccountIds = connection.prepareStatement("SELECT id FROM Account");
        insertAccount = connection.prepareStatement("INSERT INTO Account VALUES (?, ?)");
        deleteAccount = connection.prepareStatement("DELETE FROM Account WHERE id = ?");
        getAccountToken = connection.prepareStatement("SELECT token FROM Account WHERE id = ?");
    }

    public static void open() throws SQLException {
        connection = dataSource.getConnection();
        prepareStatements();
    }

    public static void close() throws SQLException {
        connection.close();
        selectAccountIds.close();
        insertAccount.close();
        deleteAccount.close();
        getAccountToken.close();
    }

    public static Set<String> getLoggableAccountIds() throws SQLException {
        ResultSet queryResult = selectAccountIds.executeQuery();
        HashSet<String> accounts = new HashSet<>();
        while (queryResult.next()) accounts.add(queryResult.getString("id"));
        return accounts;
    }

    public static void storeAccount(String id, String token) throws SQLException {
        insertAccount.setString(1, id);
        insertAccount.setString(2, token);
        insertAccount.executeUpdate();
    }

    public static void deleteAccount(String id) throws SQLException {
        deleteAccount.setString(1, id);
        deleteAccount.executeUpdate();
    }

    public static String getAccountToken(String id) throws SQLException {
        getAccountToken.setString(1, id);
        ResultSet queryResult = getAccountToken.executeQuery();
        return queryResult.next() ? queryResult.getString("token") : null;
    }


}
