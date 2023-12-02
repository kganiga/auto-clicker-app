package com.khalilganiga;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.PlainTextOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class BatchDecompilerApp extends JFrame {

    private JTextField inputDirectoryField;
    private JTextField outputDirectoryField;

    public BatchDecompilerApp() {
        super("Java Decompiler");

        // Set layout manager
        setLayout(new BorderLayout());

        // Create components
        inputDirectoryField = new JTextField(30);
        JButton inputDirectoryButton = new JButton("Choose Input Directory");
        outputDirectoryField = new JTextField(30);
        JButton outputDirectoryButton = new JButton("Choose Output Directory");
        JButton runButton = new JButton("Run");

        // Add components to the frame
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Input Directory:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(inputDirectoryField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        panel.add(inputDirectoryButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Output Directory:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(outputDirectoryField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        panel.add(outputDirectoryButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(runButton, constraints);

        // Add action listeners
        inputDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(inputDirectoryField);
            }
        });

        outputDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(outputDirectoryField);
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDecompiler();
            }
        });

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 150);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        // Add the panel to the frame
        add(panel, BorderLayout.CENTER);
    }

    private void chooseDirectory(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            textField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void runDecompiler() {
        String inputDirectoryPath = inputDirectoryField.getText();
        String outputDirectoryPath = outputDirectoryField.getText();

        File inputDir = new File(inputDirectoryPath);
        File outputDir = new File(outputDirectoryPath);

        if (!inputDir.exists() || !inputDir.isDirectory() || !outputDir.exists()) {
            JOptionPane.showMessageDialog(this, "Invalid input or output directory", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        processDirectory(inputDir, outputDir);

        JOptionPane.showMessageDialog(this, "Decompilation completed", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void processDirectory(File inputDir, File outputDir) {
        File[] classFiles = inputDir.listFiles((dir, name) -> name.endsWith(".class") && !new File(dir, name).isDirectory());

        if (classFiles != null && classFiles.length > 0) {
            Arrays.sort(classFiles, Comparator.comparing(File::getName));

            for (File classFile : classFiles) {
                String relativePath = inputDir.toURI().relativize(classFile.toURI()).getPath();
                String outputFilePath = outputDir.getAbsolutePath() + File.separator + relativePath.replace(".class", ".java");
                decompileClassFile(classFile.getAbsolutePath(), outputFilePath);
            }
        }

        File[] subDirectories = inputDir.listFiles(File::isDirectory);
        if (subDirectories != null) {
            for (File subDir : subDirectories) {
                File newOutputDir = new File(outputDir, subDir.getName());
                if (!newOutputDir.exists() && !newOutputDir.mkdirs()) {
                    JOptionPane.showMessageDialog(this, "Failed to create output directory: " + newOutputDir.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                processDirectory(subDir, newOutputDir);
            }
        }
    }

    private void decompileClassFile(String classFilePath, String outputFilePath) {
        try {
            PlainTextOutput output = new PlainTextOutput();
            Decompiler.decompile(classFilePath, output);

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
                fileOutputStream.write(output.toString().getBytes());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error during decompilation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BatchDecompilerApp());
    }
}
