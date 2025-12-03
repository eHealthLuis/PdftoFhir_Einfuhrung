/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.einfuehrungsprojekt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.*;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Firma
 */
public class Einfuehrungsprojekt extends JFrame{

    private JTextField pdfPathField;
    private JTextArea jsonOutputArea;
    private JButton browseButton;
    private JButton generateButton;
    private JButton copyButton;
    private JButton saveButton;

    private File selectedPdfFile;

    public Einfuehrungsprojekt() {
        setTitle("FHIR Message Generator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - PDF Selection
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("PDF Auswählen"));

        pdfPathField = new JTextField();
        pdfPathField.setEditable(false);

        browseButton = new JButton("Durchsuchen...");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPdfFile();
            }
        });

        generateButton = new JButton("FHIR Nachricht Generieren");
        generateButton.setEnabled(false);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateFhirMessage();
            }
        });

        JPanel pdfSelectPanel = new JPanel(new BorderLayout(5, 5));
        pdfSelectPanel.add(pdfPathField, BorderLayout.CENTER);
        pdfSelectPanel.add(browseButton, BorderLayout.EAST);

        topPanel.add(pdfSelectPanel, BorderLayout.CENTER);
        topPanel.add(generateButton, BorderLayout.SOUTH);

        // Center Panel - JSON Output
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("FHIR JSON Nachricht"));

        jsonOutputArea = new JTextArea();
        jsonOutputArea.setEditable(false);
        jsonOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        jsonOutputArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(jsonOutputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        copyButton = new JButton("JSON Kopieren");
        copyButton.setEnabled(false);
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyJsonToClipboard();
            }
        });

        saveButton = new JButton("JSON Speichern...");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveJsonToFile();
            }
        });

        bottomPanel.add(copyButton);
        bottomPanel.add(saveButton);

        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void selectPdfFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("docs"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }

            @Override
            public String getDescription() {
                return "PDF Dateien (*.pdf)";
            }
        });

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPdfFile = fileChooser.getSelectedFile();
            pdfPathField.setText(selectedPdfFile.getAbsolutePath());
            generateButton.setEnabled(true);
            jsonOutputArea.setText("");
            copyButton.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    private void generateFhirMessage() {
        if (selectedPdfFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Bitte wählen Sie zuerst eine PDF-Datei aus!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Progress Dialog
        JDialog progressDialog = new JDialog(this, "Verarbeite...", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressDialog.add(BorderLayout.CENTER, progressBar);
        progressDialog.add(BorderLayout.NORTH, new JLabel("PDF wird verarbeitet..."));
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        // Worker Thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    // PDF einlesen
                    String text = readPdf(selectedPdfFile.getAbsolutePath());

                    // Daten parsen
                    Patientendaten patient = new Patientendaten();
                    Praxisdaten praxis = new Praxisdaten();
                    ErhobeneDaten ed = new ErhobeneDaten();

                    parsePatientendaten(patient, text);
                    parsePraxisdaten(praxis, text);
                    parseErhobenenDaten(ed, text);

                    // FHIR Message Generator
                    FhirMessageGenerator generator = new FhirMessageGenerator(patient, praxis, ed);
                    return generator.toJson();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Fehler beim Generieren: " + e.getMessage());
                }
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    String json = get();
                    jsonOutputArea.setText(json);
                    jsonOutputArea.setCaretPosition(0);
                    copyButton.setEnabled(true);
                    saveButton.setEnabled(true);

                    JOptionPane.showMessageDialog(Einfuehrungsprojekt.this,
                            "FHIR-Nachricht erfolgreich generiert!",
                            "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Einfuehrungsprojekt.this,
                            "Fehler beim Generieren:\n" + e.getMessage(),
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void copyJsonToClipboard() {
        String json = jsonOutputArea.getText();

        if (json.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keine JSON-Nachricht zum Kopieren vorhanden!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringSelection selection = new StringSelection(json);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);

        JOptionPane.showMessageDialog(this,
                "JSON wurde in die Zwischenablage kopiert!",
                "Kopiert",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveJsonToFile() {
        String json = jsonOutputArea.getText();

        if (json.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keine JSON-Nachricht zum Speichern vorhanden!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("therapiebericht_fhir.json"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON Dateien (*.json)";
            }
        });

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Stelle sicher, dass die Datei .json Endung hat
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }

            try {
                java.nio.file.Files.write(
                        file.toPath(),
                        json.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );

                JOptionPane.showMessageDialog(this,
                        "JSON wurde gespeichert:\n" + file.getAbsolutePath(),
                        "Gespeichert",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Fehler beim Speichern:\n" + e.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Einfuehrungsprojekt gui = new Einfuehrungsprojekt();
                gui.setVisible(true);
            }
        });
    }

    // Deine bestehenden Methoden bleiben unverändert
    public static String readPdf(String filepath) throws IOException {
        File pdfFile = new File(filepath);

        if (!pdfFile.exists()) {
            throw new IOException("PDF file not found: " + pdfFile.getAbsolutePath());
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {
            // First try normal text extraction
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // If text extraction works, return it
            if (!text.trim().isEmpty()) {
                return text;
            }

            // Otherwise, use OCR for scanned PDFs
            System.out.println("No text found, using OCR...");
            return extractTextWithOCR(document);
        }
    }

    public static String readPdfFromDocs(String filename) throws IOException {
        return readPdf("docs/" + filename);
    }

    private static String extractTextWithOCR(PDDocument document) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        StringBuilder result = new StringBuilder();

        Tesseract tesseract = new Tesseract();
        // Set the path to tessdata folder (adjust this path for your system)
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("deu"); // Use "deu" for German, "eng" for English

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            try {
                // Convert PDF page to image at 300 DPI for better OCR accuracy
                BufferedImage image = renderer.renderImageWithDPI(page, 300);

                // Perform OCR on the image
                String pageText = tesseract.doOCR(image);
                result.append(pageText);
                result.append("\n--- Page ").append(page + 1).append(" ---\n");

            } catch (TesseractException e) {
                System.err.println("OCR failed for page " + (page + 1) + ": " + e.getMessage());
            }
        }

        return result.toString();
    }

    private static String extractValue(String text, String pattern) {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    private static String extractNthValue(String text, String label, int n) {
        Pattern pattern = Pattern.compile(label + "\\s*:\\s*(.+?)(?=\\n|$)");
        Matcher matcher = pattern.matcher(text);

        int count = 0;
        while (matcher.find()) {
            count++;
            if (count == n) {
                return matcher.group(1).trim();
            }
        }
        return null;
    }

    public static void parsePatientendaten(Patientendaten patient, String text) {
        patient.setDocDate(extractDate(text));
        patient.setId(extractValue(text, "KlientinnenID\\s*:\\s*(.+?)\\n"));
        patient.setVorname(extractValue(text, "Vorname\\s*:\\s*(.+?)\\n"));
        patient.setNachname(extractValue(text, "Nachname\\s*:\\s*(.+?)\\n"));
        patient.setSvn(extractValue(text, "Versicherungsnummer\\s*:\\s*(.+?)\\n"));
        patient.setGeschlecht(extractValue(text, "Geschlecht\\s*:\\s*(.+?)\\n"));
        patient.setTelefon(extractNthValue(text, "Telefonnummer", 2));
        String alterStr = extractValue(text, "Alter\\s*:\\s*(\\d+)");
        if (alterStr != null && !alterStr.isEmpty()) {
            patient.setAlter(Integer.parseInt(alterStr.replaceAll("[^0-9]", "")));
        }

        patient.setErstKontakt(extractValue(text, "Erstkontakt\\s*:\\s*(.+?)\\n"));

        String gewichtStr = extractValue(text, "Gewicht\\s*:\\s*([\\d.]+)");
        if (gewichtStr != null && !gewichtStr.isEmpty()) {
            patient.setGewicht(Integer.parseInt(gewichtStr.replaceAll("[^0-9.]", "")));
        }
    }

    public static void parsePraxisdaten(Praxisdaten praxis, String text) {
        praxis.setName(extractValue(text, "Name\\s*:\\s*(.+?)\\n"));
        praxis.setTitel(extractValue(text, "Titel\\s*:\\s*(.+?)\\n"));
        praxis.setPraxisName(extractValue(text, "Praxisname\\s*:\\s*(.+?)\\n"));
        praxis.setAdresse(extractValue(text, "Adresse\\s*:\\s*(.+?)\\n"));
        praxis.setTelefon(extractValue(text, "Telefonnummer\\s*:\\s*(.+?)\\n"));
        praxis.setEmail(extractValue(text, "E-Mail\\s*:\\s*(.+?)\\n"));
        praxis.setOtherInfos(extractValue(text, "Zusätzliche Informationen\\s*:\\s*(.+?)\\n"));
    }

    public static void parseErhobenenDaten(ErhobeneDaten daten, String text) {
        daten.setGewichtsVerlauf(extractValue(text, "Gewichtsverlauf\\s*:\\s*(.+?)\\n"));
        daten.addHauptDiagnosen(extractValue(text, "Hauptdiagnose\\s*:\\s*(.+?)\\n"));
        daten.addHauptDiagnosen(extractValue(text, "Hauptdiagnose 1\\.1\\s*:\\s*(.+?)\\n"));
        daten.addHauptDiagnosen(extractValue(text, "Hauptdiagnose 1\\.2\\s*:\\s*(.+?)\\n"));
        daten.addHauptDiagnosen(extractValue(text, "Nebendiagnose\\s*:\\s*(.+?)\\n"));
        daten.setZuweisung(extractValue(text, "Zuweisung\\s*:\\s*(.+?)\\n"));
        daten.setSerumProteinStatus(extractValue(text, "Serum-Protein-Status\\s*:\\s*(.+?)\\n"));

        String HbA1cString = extractValue(text, "HbA1c-Verlauf\\s*:\\s*(.+?)\\n");
        if (HbA1cString != null && !HbA1cString.isEmpty()) {
            String cleaned = HbA1cString.replaceAll("[^0-9,.]", "");
            cleaned = cleaned.replace(",", ".");
            daten.setHbA1c(Double.parseDouble(cleaned));
        }
    }

    public static String extractDate(String text) {
        Map<String, String> months = Map.ofEntries(
                Map.entry("januar", "01"),
                Map.entry("februar", "02"),
                Map.entry("märz", "03"),
                Map.entry("april", "04"),
                Map.entry("mai", "05"),
                Map.entry("juni", "06"),
                Map.entry("juli", "07"),
                Map.entry("august", "08"),
                Map.entry("september", "09"),
                Map.entry("oktober", "10"),
                Map.entry("november", "11"),
                Map.entry("dezember", "12")
        );

        Pattern pattern = Pattern.compile("(\\d{1,2})\\.\\s*(\\w+)\\s*(\\d{4})");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(1)));
            String month = months.get(matcher.group(2).toLowerCase());
            String year = matcher.group(3);

            return year + "-" + month + "-" + day;
        }

        return null;
    }

}
