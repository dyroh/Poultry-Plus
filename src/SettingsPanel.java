import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class SettingsPanel extends JPanel {
    private AppController controller;
    private Properties config; // Properties for theme configuration
    private static final String LOG_FILE = "login_attempts.log"; // Log file path

    public SettingsPanel(AppController controller) {
        this.controller = controller;
        setLayout(new GridLayout(4, 1));

        JButton changePasswordButton = new JButton("Change Password");
        JButton changeThemeButton = new JButton("Toggle Dark/Light Theme");
        JButton viewLogButton = new JButton("View Login Logs");

        loadConfig(); // Load theme settings from the config file

        // Adjust colors based on the theme
        if (controller.isDarkTheme()) {
            setBackground(Color.DARK_GRAY);
            setForeground(Color.WHITE);
            styleButton(changePasswordButton, Color.DARK_GRAY, Color.WHITE);
            styleButton(changeThemeButton, Color.DARK_GRAY, Color.WHITE);
            styleButton(viewLogButton, Color.DARK_GRAY, Color.WHITE);
        } else {
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
            styleButton(changePasswordButton, Color.LIGHT_GRAY, Color.BLACK);
            styleButton(changeThemeButton, Color.LIGHT_GRAY, Color.BLACK);
            styleButton(viewLogButton, Color.LIGHT_GRAY, Color.BLACK);
        }

        // Handle Change Password button functionality
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame changePasswordFrame = new JFrame("Change Password");
                changePasswordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                changePasswordFrame.setSize(400, 300);
                changePasswordFrame.setLocationRelativeTo(null);
                changePasswordFrame.add(new ChangePasswordPanel(controller));
                changePasswordFrame.setVisible(true);
            }
        });

        // Handle Toggle Theme button functionality
        changeThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.toggleTheme(); // Toggle between dark and light themes
                saveThemeToPropertiesFile(controller.isDarkTheme() ? "dark" : "light"); // Save theme in config file
                logAction("Theme changed to " + (controller.isDarkTheme() ? "Dark" : "Light") + " Theme");
                JOptionPane.showMessageDialog(null, "Theme toggled successfully!");
                updateTheme(); // Update the panel to reflect the new theme
            }
        });

        // View login logs
        viewLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewLogFile();
            }
        });

        add(changePasswordButton);
        add(changeThemeButton);
        add(viewLogButton);
    }

    // Method to update the panel's theme based on the current settings
    private void updateTheme() {
        if (controller.isDarkTheme()) {
            setBackground(Color.DARK_GRAY);
            setForeground(Color.WHITE);
        } else {
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
        }
        repaint();
        revalidate();
    }

    // Method to save the selected theme to an external properties file
    private void saveThemeToPropertiesFile(String theme) {
        try (OutputStream output = new FileOutputStream("config.properties")) {
            config.setProperty("theme", theme);
            config.store(output, null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving theme to configuration file: " + ex.getMessage());
        }
    }

    // Method to load the theme settings from the properties file
    private void loadConfig() {
        config = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            config.load(input);
            String theme = config.getProperty("theme", "dark");
            if (theme.equals("dark") && !controller.isDarkTheme()) {
                controller.toggleTheme(); // Set to dark if necessary
            } else if (theme.equals("light") && controller.isDarkTheme()) {
                controller.toggleTheme(); // Set to light if necessary
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No configuration found: Default settings");
        }
    }

    // Log the actions into a log file
    private void logAction(String action) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.println(action + " - " + new java.util.Date());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error logging action: " + e.getMessage());
        }
    }

    // View the log file contents
    private void viewLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            JTextArea logTextArea = new JTextArea(10, 30);
            logTextArea.read(reader, null);
            logTextArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(logTextArea);
            JOptionPane.showMessageDialog(null, scrollPane, "Login Logs", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading log file: " + e.getMessage());
        }
    }

    // Button styling method with adjustable background and foreground colors
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(fg, 2));
    }
}
