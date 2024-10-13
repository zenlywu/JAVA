package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenuPanel extends JPanel {
    private JComboBox<String> difficultyComboBox;
    private static Map<Integer, List<String>> gameRecords = new HashMap<>();

    static {
        gameRecords.put(0, new ArrayList<>());
        gameRecords.put(1, new ArrayList<>());
        gameRecords.put(2, new ArrayList<>());
    }

    public MainMenuPanel() {
        setLayout(new BorderLayout());

        // 添加标题
        JLabel titleLabel = new JLabel("Welcome to Pacman", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        // 创建中心面板用于放置其他组件
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5)); // 修改为5行

        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(difficultyComboBox);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setMargin(new Insets(5, 10, 5, 10));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pacman.showGamePanel();
            }
        });
        centerPanel.add(startButton);

        JButton recordsButton = new JButton("View Records");
        recordsButton.setFont(new Font("Arial", Font.BOLD, 18));
        recordsButton.setMargin(new Insets(5, 10, 5, 10));
        recordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pacman.showRecordsPanel();
            }
        });
        centerPanel.add(recordsButton);

        // 新增显示玩法的按钮
        JButton instructionsButton = new JButton("How to Play");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 18));
        instructionsButton.setMargin(new Insets(5, 10, 5, 10));
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInstructionsDialog();
            }
        });
        centerPanel.add(instructionsButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void showInstructionsDialog() {
        JDialog instructionsDialog = new JDialog((Frame) null, "How to Play", true);
        instructionsDialog.setSize(400, 300);
        instructionsDialog.setLayout(new BorderLayout());

        JTextArea instructionsText = new JTextArea();
        instructionsText.setFont(new Font("宋体", Font.PLAIN, 16)); // 使用支持中文的字体
        instructionsText.setText(
            "遊戲玩法說明：\n" +
            "1. 使用方向鍵移動小精靈。\n" +
            "2. 吃掉所有豆子得以通關。\n" +
            "3. 避免被幽靈抓住，否則會失去一條生命。\n" +
            "4. 吃掉能量增強物來獲得特殊能力。\n" +
            "5. 清除所有關卡以贏得遊戲。\n"+
            "------------------------\n"+
            "道具說明:\n"+
            "飛鞋: 讓速度 += 2;\n"+
            "藍色藥水: 讓速度 -= 2;\n"+
            "劍: 在15秒內可以吃掉鬼;\n"+
            "盾牌: 在15秒內碰到鬼不會死;\n"+
            "------------------------\n"+
            "操作說明:\n"+
            "方向鍵上,下,左,右:操作小精靈;\n"+
            "ESC:回到MENU;\n"+
            "SPACE:開始遊戲;\n"+
            "ENTER: 暫停遊戲;\n"
        );
        instructionsText.setEditable(false);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(instructionsText);
        instructionsDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instructionsDialog.dispose();
            }
        });

        instructionsDialog.add(closeButton, BorderLayout.SOUTH);
        instructionsDialog.setLocationRelativeTo(null); // 居中显示
        instructionsDialog.setVisible(true);
    }

    public int getSelectedDifficulty() {
        return difficultyComboBox.getSelectedIndex();
    }

    public static List<String> getGameRecords(int difficulty) {
        return gameRecords.getOrDefault(difficulty, new ArrayList<>());
    }

    public static void setGameRecords(int difficulty, List<String> records) {
        gameRecords.put(difficulty, records);
    }

    public static void addGameRecord(int difficulty, int score, int time) {
        List<String> records = getGameRecords(difficulty);
        boolean recordExists = false;
        for (String record : records) {
            if (record.contains("Score: " + score + ", Time: " + time + "s")) {
                recordExists = true;
                break;
            }
        }

        if (!recordExists) {
            records.add("Score: " + score + ", Time: " + time + "s");
            recordExists = true;
            sortRecords(records); // 对记录进行排序
            //updateRankings(records); // 更新排名
            setGameRecords(difficulty, records);
        }
    }

    private static void sortRecords(List<String> records) {
        records.sort((o1, o2) -> {
            int score1 = Integer.parseInt(o1.split(",")[0].split(":")[1].trim());
            int score2 = Integer.parseInt(o2.split(",")[0].split(":")[1].trim());
            int time1 = Integer.parseInt(o1.split(",")[1].split(":")[1].replace("s", "").trim());
            int time2 = Integer.parseInt(o2.split(",")[1].split(":")[1].replace("s", "").trim());

            if (score1 != score2) {
                return score2 - score1; // 分数从高到低排序
            } else {
                return time1 - time2; // 时间从低到高排序
            }
        });
    }

    public static List<String> getRankedRecords(int difficulty) {
        List<String> records = new ArrayList<>(getGameRecords(difficulty));
        List<String> rankedRecords = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            String record = records.get(i);
            String rankedRecord = (i + 1) + ". " + record;
            rankedRecords.add(rankedRecord);
        }
        return rankedRecords;
    }

    public static void loadRecords() {
        for (int difficulty = 0; difficulty < 3; difficulty++) {
            String fileName = getFileName(difficulty);
            List<String> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    records.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            setGameRecords(difficulty, records);
        }
    }

    public static void saveRecords(int difficulty) {
        String fileName = getFileName(difficulty);
        List<String> records = getGameRecords(difficulty);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) { // 使用 false 以覆盖模式写入文件
            for (String record : records) {
                bw.write(record);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(int difficulty) {
        switch (difficulty) {
            case 0:
                return "./s1113318_project/src/pacman/easyRecords.txt";
            case 1:
                return "./s1113318_project/src/pacman/mediumRecords.txt";
            case 2:
                return "./s1113318_project/src/pacman/hardRecords.txt";
            default:
                return "./s1113318_project/src/pacman/easyRecords.txt";
        }
    }
}
