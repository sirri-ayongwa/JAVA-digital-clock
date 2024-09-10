import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.awt.GridLayout;

public class DigitalClockApp extends JFrame {

    public DigitalClockApp() {
        setTitle("Digital Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1)); // 2 rows, 1 column layout

        // Create local time clock
        ZoneId systemZoneId = ZoneId.systemDefault();
        JPanel localClockPanel = addClockPanelWithDropdown("Local Time", systemZoneId); // Add local clock with dropdown
                                                                                        // menu
        add(localClockPanel);

        // Create panel for international clocks
        JPanel internationalClocksPanel = new JPanel(new GridLayout(1, 4)); // 1 row, 4 columns
        String[] internationalLocations = { "America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney" };
        for (String location : internationalLocations) {
            JPanel clockPanel = addClockPanelWithDropdown(location); // Add clock with dropdown menu
            internationalClocksPanel.add(clockPanel);
        }
        add(internationalClocksPanel);

        setVisible(true); // Show the frame
        pack(); // Adjust frame size
        setLocationRelativeTo(null); // Center the frame on screen
    }

    // Method to add clock panel with dropdown menu
    private JPanel addClockPanelWithDropdown(String location) {
        JPanel clockPanel = new ClockPanel(location);
        return clockPanel;
    }

    // Overloaded method to add clock panel with initial time zone
    private JPanel addClockPanelWithDropdown(String location, ZoneId initialZoneId) {
        JPanel clockPanel = new ClockPanel(location, initialZoneId);
        return clockPanel;
    }

    // Main method to create and show GUI on EDT
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DigitalClockApp::new);
    }
}

class ClockPanel extends JPanel {
    private JLabel timeLabel;
    private JComboBox<String> timeZoneComboBox;

    // Constructor to create clock panel with dropdown menu
    public ClockPanel(String location) {
        setLayout(new GridBagLayout());

        // Update time label every second
        Timer timer = new Timer(1000, e -> updateTime(location));
        timer.start();

        // Constraints for time label
        GridBagConstraints timeLabelConstraints = new GridBagConstraints();
        timeLabelConstraints.gridx = 0;
        timeLabelConstraints.gridy = 0;
        timeLabelConstraints.insets = new Insets(10, 0, 0, 0); // Add top margin
        timeLabelConstraints.anchor = GridBagConstraints.PAGE_START;

        // Constraints for dropdown menu
        GridBagConstraints comboBoxConstraints = new GridBagConstraints();
        comboBoxConstraints.gridx = 0;
        comboBoxConstraints.gridy = 1;
        comboBoxConstraints.insets = new Insets(5, 0, 0, 0); // Add top margin
        comboBoxConstraints.anchor = GridBagConstraints.PAGE_START;

        // Time label at the top
        timeLabel = new JLabel();
        updateTime(location);
        add(timeLabel, timeLabelConstraints);

        // Dropdown menu at the center
        timeZoneComboBox = new JComboBox<>(ZoneId.getAvailableZoneIds().toArray(new String[0]));
        timeZoneComboBox.setSelectedItem(location);
        timeZoneComboBox.addActionListener(e -> {
            String selectedTimeZone = (String) timeZoneComboBox.getSelectedItem();
            updateTime(selectedTimeZone);
        });
        add(timeZoneComboBox, comboBoxConstraints);
    }

    // Overloaded constructor to create clock panel with initial time zone
    public ClockPanel(String location, ZoneId initialZoneId) {
        this(location);
        timeZoneComboBox.setSelectedItem(initialZoneId.getId());
    }

    // Method to update time label based on selected time zone
    private void updateTime(String location) {
        ZoneId zoneId = location.equals("Local Time") ? ZoneId.systemDefault() : ZoneId.of(location);
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss a"); // Pattern to include AM/PM
        String formattedTime = currentTime.format(formatter);
        timeLabel.setText(formattedTime);
    }
}