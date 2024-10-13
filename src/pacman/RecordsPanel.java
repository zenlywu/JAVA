package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecordsPanel extends JPanel {
    private JTextArea recordsTextArea;
    private int currentDifficulty = 0;

    public RecordsPanel() {
        setLayout(new BorderLayout());

        recordsTextArea = new JTextArea();
        recordsTextArea.setEditable(false);
        recordsTextArea.setFont(new Font("Arial", Font.BOLD, 18));

        updateRecordsDisplay();

        JScrollPane scrollPane = new JScrollPane(recordsTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

        JButton easyButton = new JButton("Easy");
        easyButton.setFont(new Font("Arial", Font.BOLD, 24));
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDifficulty = 0;
                updateRecordsDisplay();
            }
        });
        buttonPanel.add(easyButton);

        JButton mediumButton = new JButton("Medium");
        mediumButton.setFont(new Font("Arial", Font.BOLD, 24));
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDifficulty = 1;
                updateRecordsDisplay();
            }
        });
        buttonPanel.add(mediumButton);

        JButton hardButton = new JButton("Hard");
        hardButton.setFont(new Font("Arial", Font.BOLD, 24));
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDifficulty = 2;
                updateRecordsDisplay();
            }
        });
        buttonPanel.add(hardButton);

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pacman.showMainMenu();
            }
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateRecordsDisplay() {
        recordsTextArea.setText("");
        for (String record : MainMenuPanel.getGameRecords(currentDifficulty)) {
            recordsTextArea.append(record + "\n");
        }
    }
}
