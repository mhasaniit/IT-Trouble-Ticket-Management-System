package code;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

// Dao class handles all JDBC communication with the MySQL database.
public class Dao {

    // Database connection details
    private final String url = "jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false";
    private final String dbUsername = "fp411";
    private final String dbPassword = "411";

    // SQL Table names Should be updated to reflect current database 
    private final String USER_TABLE = "jsmit_users";
    private final String TICKET_TABLE = "jsmit_tickets";

    //Establishes a connection to the MySQL server.
    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, dbUsername, dbPassword);
    }

    // Checks if username and password exist in the user table.
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM " + USER_TABLE + " WHERE username=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // returns true if a record was found

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves the permission level (role) for a specific user.
    public String getUserRole(String username, String password) {
        String sql = "SELECT role FROM " + USER_TABLE + " WHERE username=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Adds a new ticket record to the database.
    public boolean insertTicket(String user, String desc, Date startDate) {
        String sql = "INSERT INTO " + TICKET_TABLE + " (user, ticket_desc, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, desc);
            ps.setDate(3, startDate);
            ps.setDate(4, null); // end_date is null for new open tickets
            ps.setString(5, "Open");

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updates the description of a ticket via its primary key tid.
    public boolean updateTicket(int tid, String desc) {
        String sql = "UPDATE " + TICKET_TABLE + " SET ticket_desc=? WHERE tid=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, desc);
            ps.setInt(2, tid);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Removes a ticket from the database.
    public boolean deleteTicket(int tid) {
        String sql = "DELETE FROM " + TICKET_TABLE + " WHERE tid=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tid);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sets a ticket status to Closed and updates the end date.
    public boolean closeTicket(int tid) {
        String sql = "UPDATE " + TICKET_TABLE + " SET status='Closed', end_date=? WHERE tid=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new Date(System.currentTimeMillis()));
            ps.setInt(2, tid);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetches a specific ticket record. Note: ResultSet is left open; usually handled by caller.
    public ResultSet getTicketById(int tid) {
        String sql = "SELECT * FROM " + TICKET_TABLE + " WHERE tid=?";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tid);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieves all tickets for Admin view and maps them to a JTable model.
    public DefaultTableModel getAllTickets() {
        String[] cols = {"Ticket ID", "User", "Description", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        String sql = "SELECT * FROM " + TICKET_TABLE + " ORDER BY tid";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("tid"),
                    rs.getString("user"),
                    rs.getString("ticket_desc"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }

    // Retrieves only tickets belonging to a specific user for non-admin view.
    public DefaultTableModel getTicketsByUser(String username) {
        String[] cols = {"Ticket ID", "User", "Description", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        String sql = "SELECT * FROM " + TICKET_TABLE + " WHERE user=? ORDER BY tid";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("tid"),
                    rs.getString("user"),
                    rs.getString("ticket_desc"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }
}