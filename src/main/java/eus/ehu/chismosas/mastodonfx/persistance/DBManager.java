package eus.ehu.chismosas.mastodonfx.persistance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static DBManager instance = new DBManager();
    Connection conn;
    String dbpath;

    private DBManager() {
        dbpath = "accounts.db";
    }

    public static DBManager getInstance() {
        return instance;
    }

    public void open() {
        try {
            String url = "jdbc:sqlite:" + dbpath;
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void storeAccount(String id, String token) {

        this.open();
        String sql = "INSERT INTO account (id, token) VALUES(?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, token);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.close();
    }

    public List<String> getAllAccounts() {
        var accounts = new ArrayList<String>();
        this.open();

        try {
            String query = "SELECT id FROM account";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                accounts.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.close();
        return accounts;
    }

    public String getAccountToken(String id) {

        this.open();
        String query = "SELECT token FROM account WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("token");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAccount(String id) {
        this.open();
        String sql = "DELETE FROM account WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.close();
    }
}
