package code;

import java.awt.Font;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//The Tickets class provides the main interface for CRUD operations on trouble tickets. Access to certain features (like delete) is restricted based on the user's role.
public class Tickets extends JFrame {

    private String currentUser;
    private String currentRole;
    private Dao dao;

    // UI Components
    private JTextField txtTid;
    private JTextField txtUser;
    private JTextArea txtDesc;
    private JTable table;

    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnView;
    private JButton btnClose;
    private JButton btnRefresh;

    public Tickets(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
        this.dao = new Dao();

        // Frame Setup
        setTitle("Tickets - " + currentUser + " (" + currentRole + ")");
        setSize(920, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu Bar setup (Refresh and Logout)
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem miRefresh = new JMenuItem("Refresh");
        JMenuItem miLogout = new JMenuItem("Logout");

        miRefresh.addActionListener(e -> loadTable());
        miLogout.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        menu.add(miRefresh);
        menu.add(miLogout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // UI Labels and Text Fields
        JLabel lblTitle = new JLabel("IT Trouble Ticket Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(280, 15, 350, 30);
        add(lblTitle);

        JLabel lblTid = new JLabel("Ticket ID:");
        lblTid.setBounds(30, 70, 100, 25);
        add(lblTid);

        txtTid = new JTextField();
        txtTid.setBounds(120, 70, 150, 25);
        add(txtTid);

        JLabel lblUser = new JLabel("User:");
        lblUser.setBounds(30, 110, 100, 25);
        add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(120, 110, 200, 25);
        txtUser.setText(currentUser);
        
        // Restriction: Standard users cannot change the "User" field
        if (currentRole.equalsIgnoreCase("user")) {
            txtUser.setEditable(false);
        }
        add(txtUser);

        JLabel lblDesc = new JLabel("Ticket Description:");
        lblDesc.setBounds(30, 150, 120, 25);
        add(lblDesc);

        txtDesc = new JTextArea();
        JScrollPane descPane = new JScrollPane(txtDesc);
        descPane.setBounds(150, 150, 320, 90);
        add(descPane);

        // Action Buttons
        btnInsert = new JButton("Insert");
        btnInsert.setBounds(520, 70, 120, 30);
        add(btnInsert);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(660, 70, 120, 30);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(520, 110, 120, 30);
        add(btnDelete);

        btnView = new JButton("View by ID");
        btnView.setBounds(660, 110, 120, 30);
        add(btnView);

        btnClose = new JButton("Close Ticket");
        btnClose.setBounds(520, 150, 120, 30);
        add(btnClose);

        btnRefresh = new JButton("Refresh Table");
        btnRefresh.setBounds(660, 150, 120, 30);
        add(btnRefresh);

        // Data Table with scroll pane
        table = new JTable();
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBounds(30, 270, 840, 250);
        add(tablePane);

        // Register action listeners
        btnInsert.addActionListener(e -> insertTicket());
        btnUpdate.addActionListener(e -> updateTicket());
        btnDelete.addActionListener(e -> deleteTicket());
        btnView.addActionListener(e -> viewTicket());
        btnClose.addActionListener(e -> closeTicket());
        btnRefresh.addActionListener(e -> loadTable());

        // Restriction Only admins can delete tickets
        if (currentRole.equalsIgnoreCase("user")) {
            btnDelete.setEnabled(false);
        }

        // Initial data load
        loadTable();
    }

    //Creates a new ticket in the database.
    private void insertTicket() {
        String user = txtUser.getText().trim();
        String desc = txtDesc.getText().trim();

        if (user.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User and description are required.");
            return;
        }

        boolean success = dao.insertTicket(user, desc, new java.sql.Date(System.currentTimeMillis()));

        if (success) {
            JOptionPane.showMessageDialog(this, "Ticket inserted successfully.");
            clearFields();
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed.");
        }
    }

    //Updates the description of an existing ticket based on Ticket ID.
    private void updateTicket() {
        if (txtTid.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ticket ID.");
            return;
        }

        String desc = txtDesc.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.");
            return;
        }

        try {
            int tid = Integer.parseInt(txtTid.getText().trim());
            boolean success = dao.updateTicket(tid, desc);

            if (success) {
                JOptionPane.showMessageDialog(this, "Ticket updated successfully.");
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "TID must be a number.");
        }
    }

    //Deletes a ticket record after confirmation.
    private void deleteTicket() {
        if (txtTid.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ticket ID.");
            return;
        }

        int tid = Integer.parseInt(txtTid.getText().trim());

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete ticket number " + tid + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = dao.deleteTicket(tid);
            if (success) {
                JOptionPane.showMessageDialog(this, "Ticket deleted successfully.");
                clearFields();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }

  //searches for ticket by ID and populates fields/shows details.
    private void viewTicket() {
        if (txtTid.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ticket ID.");
            return;
        }

        try {
            int tid = Integer.parseInt(txtTid.getText().trim());
            ResultSet rs = dao.getTicketById(tid);
            if (rs != null && rs.next()) {
                txtUser.setText(rs.getString("user"));
                txtDesc.setText(rs.getString("ticket_desc"));

                JOptionPane.showMessageDialog(this,
                        "Ticket Found\n"
                        + "ID: " + rs.getInt("tid")
                        + "\nUser: " + rs.getString("user")
                        + "\nDescription: " + rs.getString("ticket_desc")
                        + "\nStart Date: " + rs.getDate("start_date")
                        + "\nEnd Date: " + rs.getDate("end_date")
                        + "\nStatus: " + rs.getString("status"));
            } else {
                JOptionPane.showMessageDialog(this, "Ticket not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing ticket.");
        }
    }

   //Marks a ticket as Closed&records the current end date.
    private void closeTicket() {
        if (txtTid.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ticket ID.");
            return;
        }

        int tid = Integer.parseInt(txtTid.getText().trim());
        boolean success = dao.closeTicket(tid);

        if (success) {
            JOptionPane.showMessageDialog(this, "Ticket closed successfully.");
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Close ticket failed.");
        }
    }

    // Refreshes the JTable. Admins see all tickets, users only see their own.
    private void loadTable() {
        if (currentRole.equalsIgnoreCase("admin")) {
            table.setModel(dao.getAllTickets());
        } else {
            table.setModel(dao.getTicketsByUser(currentUser));
        }
    }

    // Resets input fields to default state.
    private void clearFields() {
        txtTid.setText("");
        txtDesc.setText("");
        txtUser.setText(currentUser);
    }
}