package com.khalilganiga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Java class that simulates mouse click event.
 *
 * @author Khalil Ganiga
 */
public class AutoClickerApp extends JFrame {

    private Timer timer;
    private JProgressBar progressBar;
    private final Logger logger;
    private JTextField timerIntervalField;

    public AutoClickerApp() {
        // Initialize logger
        logger = Logger.getLogger(AutoClickerApp.class.getName());
        try {
            FileHandler fileHandler = new FileHandler("AutoClickerLog.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Exception occurred in AutoClickerApp()" + e);
        }

        setTitle("Auto Clicker");
        setSize(400, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton startButton = new JButton("Start Auto Clicker");
        startButton.addActionListener(e -> {
            startAutoClicker();
            logger.info("Auto Clicker started");
        });


        JButton stopButton = new JButton("Stop Auto Clicker");
        stopButton.addActionListener(e -> {
            stopAutoClicker();
            logger.info("Auto Clicker stopped");
        });


        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            exitApplication();
            logger.info("Application exited");
        });


        timerIntervalField = new JTextField("30", 5); // Initial value set to 30 seconds
        JLabel timerLabel = new JLabel("Timer Interval (seconds): ");

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        Color lightGreen = new Color(144, 238, 144);
        progressBar.setForeground(lightGreen);
        progressBar.setStringPainted(true);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(exitButton);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(timerLabel);
        inputPanel.add(timerIntervalField);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(inputPanel);
        panel.add(buttonPanel);
        panel.add(progressBar);
        int topMargin = 10;
        panel.setBorder(BorderFactory.createEmptyBorder(topMargin, 10, 10, 10));

        add(panel, BorderLayout.CENTER);

    }

    private void startAutoClicker() {
        int interval = Integer.parseInt(timerIntervalField.getText());
        progressBar.setMaximum(interval);

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(1000, new ActionListener() {
            int secondsRemaining = interval;

            @Override
            public void actionPerformed(ActionEvent e) {
                secondsRemaining--;
                progressBar.setValue(interval - secondsRemaining);
                progressBar.setString(secondsRemaining + " seconds remaining");

                if (secondsRemaining <= 0) {
                    simulateMouseClick();
                    secondsRemaining = interval;
                    logger.info("Mouse click simulated");
                }
            }
        });

        timer.setInitialDelay(0);
        timer.setDelay(1000);
        timer.start();
        timer.getActionListeners()[0].actionPerformed(null); // Trigger the first tick immediately
    }

    private void stopAutoClicker() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        progressBar.setValue(0);
        progressBar.setString("Auto Clicker stopped");
    }

    private void simulateMouseClick() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {

            logger.severe("Error simulating mouse click: " + e.getMessage());
        }
    }

    private void exitApplication() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutoClickerApp().setVisible(true));
    }
}
