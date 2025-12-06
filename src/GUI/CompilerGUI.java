package GUI;

import Lexer.Lexer;
import Parser.ParseException;
import Parser.TryCatchParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompilerGUI extends JFrame {

    private final JTextArea codeTextArea;
    private final JTextArea consoleTextArea;

    public CompilerGUI() {
        super("Analyseur  Try-Catch en Java Script");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // --- Components ---
        codeTextArea = new JTextArea();
        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        JButton testFileButton = new JButton("Tester avec un fichier...");
        JButton testCodeButton = new JButton("Tester le code ci-dessus");
        JButton clearButton = new JButton("Effacer");

        // --- Layout ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Code à analyser"));
        topPanel.add(new JScrollPane(codeTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(testFileButton);
        buttonPanel.add(testCodeButton);
        buttonPanel.add(clearButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Console"));
        bottomPanel.add(new JScrollPane(consoleTextArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.6);

        add(splitPane);

        // --- Redirect Console Output ---
        PrintStream printStream = new PrintStream(new CustomOutputStream(consoleTextArea));
        System.setOut(printStream);
        System.setErr(printStream);

        // --- Actions ---
        testFileButton.addActionListener(event -> chooseAndTestFile());
        testCodeButton.addActionListener(event -> testEditorCode());
        clearButton.addActionListener(event -> clearFields());
    }

    private void chooseAndTestFile() {
        JFileChooser fileChooser = new JFileChooser();
        // Proposer le répertoire du projet comme point de départ
        fileChooser.setCurrentDirectory(new java.io.File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                codeTextArea.setText(content);
                consoleTextArea.setText("--- Test avec le fichier " + selectedFile.getName() + " ---\n");
                analyzeCode(content);
            } catch (IOException ex) {
                consoleTextArea.append("Erreur de lecture du fichier : " + ex.getMessage());
            }
        }
    }

    private void clearFields() {
        codeTextArea.setText("");
        consoleTextArea.setText("");
    }

    private void testEditorCode() {
        String code = codeTextArea.getText();
        if (code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La zone de code est vide.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        consoleTextArea.setText("--- Test avec le code de l'éditeur ---\n");
        analyzeCode(code);
    }

    private void analyzeCode(String code) {
        try {
            Lexer lexer = new Lexer(code);
            TryCatchParser parser = new TryCatchParser(lexer);
            parser.anlysertoust();
            System.out.println("Analyse réussie: aucune erreur de syntaxe try/catch détectée.");
        } catch (ParseException pe) {
            System.err.printf("Erreur de syntaxe: %s (ligne %d, colonne %d)\n", pe.getMessage(), pe.getLine(), pe.getColumn());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'analyse : " + e.getMessage());
        }
    }

    public static class CustomOutputStream extends OutputStream {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            // redirects data to the text area
            textArea.append(String.valueOf((char)b));
            // scrolls the text area to the end of data
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}

