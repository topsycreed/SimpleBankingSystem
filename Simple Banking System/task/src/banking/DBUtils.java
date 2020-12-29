package banking;

import java.sql.*;
import static banking.Main.*;

public class DBUtils {

    static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    static void closeConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void createDB() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id INTEGER,\n"
                + "	number TEXT,\n"
                + "	pin TEXT,\n"
                + "	balance INTEGER DEFAULT 0\n"
                + ");";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void dropTable() {
        String sql = "DROP TABLE IF EXISTS card;";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void addCard(String cardNumber, String pin) {
        String sql = "INSERT INTO card(number, pin) VALUES(?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static int getBalanceByCardNumber(String cardNumber) {
        int balance = 0;
        String sql = "SELECT balance "
                + "FROM card WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, cardNumber);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    static int checkCardExists(String cardNumber) {
        int count = 0;
        String sql = "SELECT COUNT(*) "
                + "FROM card WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, cardNumber);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                count = Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    static String getPinByCardNumber(String cardNumber) {
        String pin = "";
        String sql = "SELECT pin "
                + "FROM card WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, cardNumber);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                pin = rs.getString("pin");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pin;
    }

    static void increaseBalance(String cardNumber, int balance) {
        String sql = "UPDATE card SET balance = balance + ? "
                + "WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setInt(1, balance);
            pstmt.setString(2, cardNumber);
            //
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void decreaseBalance(String cardNumber, int balance) {
        String sql = "UPDATE card SET balance = balance - ? "
                + "WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setInt(1, balance);
            pstmt.setString(2, cardNumber);
            //
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void deleteAccount(String cardNumber) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, cardNumber);
            //
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
