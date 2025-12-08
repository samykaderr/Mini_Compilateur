package GUI;

import Lexer.Lexer;
import Lexer.Token;
import Parser.ParseException;
import Parser.TryCatchParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

public class IDEGUI extends JFrame {

    private final JTextArea codeTextArea;
    private final JTextArea consoleTextArea;
    private final JFileChooser fileChooser;

    public IDEGUI() {
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
        PrintStream printStream = new PrintStream(new affichage(consoleTextArea));
        System.setOut(printStream);
        System.setErr(printStream);

        // --- Actions ---
        testFileButton.addActionListener(e -> choisirefichier ());
        testCodeButton.addActionListener(e -> testercode ());
        clearButton.addActionListener(e -> supprimer ());

        fileChooser = new JFileChooser();
    }

    private void choisirefichier () {
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

    private void supprimer () {
        codeTextArea.setText("");
        consoleTextArea.setText("");
    }

    private void testercode () {
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
            // analyse lexicale et affichage des tokens
            Lexer tokenLexer = new Lexer(code);
            List<Token> tokens = tokenLexer.getAllTokens();
            System.out.println("--- Tokens ---");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("\n--- Analyse Syntaxique ---");

            // analyse synthaxique et détection des erreurs try/catch
            Lexer parserLexer = new Lexer(code); 
            TryCatchParser parser = new TryCatchParser(parserLexer);
            parser.anlysertoust();
            System.out.println("Analyse réussie: aucune erreur de syntaxe try/catch détectée.");
        } catch (ParseException pe) {
            System.err.printf("Erreur de syntaxe: %s (ligne %d, colonne %d)\n", pe.getMessage(), pe.getLine(), pe.getColumn());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'analyse : " + e.getMessage());
        }
    }

    public static class affichage extends OutputStream {
        private final JTextArea textArea;

        public affichage(JTextArea textArea) {
            
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            // mettre les donne dans le text area
            textArea.append(String.valueOf((char)b));
            // rassurer que il prend tout le code inssererdans le ide
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
