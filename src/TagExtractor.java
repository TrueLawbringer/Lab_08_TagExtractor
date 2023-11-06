import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea textArea;
    private JButton openFileBtn;
    private JButton openStopWordBtn;
    private JButton extractTagsBtn;
    private JButton saveTagsBtn;
    private File selectedFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagFrequency;

    public TagExtractor() {
        setTitle("Tag Extractor");
        setSize(800,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(10,40);
        JScrollPane scrollPane = new JScrollPane(textArea);
        openFileBtn = new JButton("Select Text File");
        openStopWordBtn = new JButton("Select Stop Words File");
        extractTagsBtn = new JButton("Extract Tags");
        saveTagsBtn = new JButton("Save Tags");

        JFileChooser fileChooser = new JFileChooser();

        openFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    selectedFile = fileChooser.getSelectedFile();
                    textArea.setText("File Selected: " + selectedFile.getName());
                }
            }
        });

        openStopWordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    File stopWordsFile = fileChooser.getSelectedFile();
                    stopWords = loadStopWordsFromFile(stopWordsFile);
                    textArea.setText("Stop Words Loaded");
                }
            }
        });

        extractTagsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null || stopWords == null){
                    textArea.setText("Please select a text file and load stop words file.");
                    return;
                }
                tagFrequency = extractTags(selectedFile, stopWords);
                DisplayTagFrequency(tagFrequency);
            }
        });

        saveTagsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tagFrequency == null){
                    textArea.setText("No tags to save. Please extract tags first.");
                    return;
                }
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                    File outputFile = fileChooser.getSelectedFile();
                    SaveTagsToFile(outputFile, tagFrequency);
                    textArea.append("\nTags saved to:" + outputFile.getName());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(openFileBtn);
        panel.add(openStopWordBtn);
        panel.add(extractTagsBtn);
        panel.add(saveTagsBtn);

        Container contentPane = getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(panel, BorderLayout.NORTH);

    }

    private Set<String> loadStopWordsFromFile(File stopWordsFile) {
        Set<String> stopWords = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while((line = reader.readLine()) != null){
                stopWords.add(line.toLowerCase());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stopWords;
    }

    private Map<String, Integer> extractTags(File textFile, Set<String> stopWords) {
        Map<String, Integer> tagFrequency = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(textFile))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] words = line.split("\\s+");
                for (String word : words){
                    word = word.toLowerCase().replaceAll("[^a-z]" , "");
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tagFrequency;
    }

    private void DisplayTagFrequency(Map<String, Integer> tagFrequency){
        textArea.setText("Tags and Frequencies:\n");
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void SaveTagsToFile(File outputFile, Map<String, Integer> tagFrequency) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()){
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}