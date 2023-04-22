package eus.ehu.chismosas.mastodonfx.persistance;

import eus.ehu.chismosas.mastodonfx.presentation.AccountSelection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBMannager {
    Connection conn = null;
    String dbpath;

    private static DBMannager instance = new DBMannager();
    public void open() {
        try {
            String url = "jdbc:sqlite:" + dbpath;
            conn = DriverManager.getConnection(url);

            System.out.println("Database connection established");
        } catch (Exception e) {
            System.err.println("Cannot connect to database server " + e);
        }
    }

    public void close() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        System.out.println("Database connection closed");
    }

    private DBMannager() {
        dbpath = "accounts.db";
    }

    public static DBMannager getInstance(){
        return instance;
    }

    public void storeAccount(String id, String token) {

        this.open();
        String sql = "INSERT INTO account (id, token) VALUES(?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, token);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.close();
    }

    public List<String> getAllAccounts()  {
        var accounts  = new ArrayList<String>();
        this.open();

        try {
            String query = "SELECT id FROM accounts";
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

    public void deleteAccount(String id){
        this.open();
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.close();
    }
}
