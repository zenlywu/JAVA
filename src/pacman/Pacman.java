package pacman;

import javax.swing.*;

public class Pacman extends JFrame {

    private static Pacman pac;
    private static MainMenuPanel mainMenuPanel;
    private static Model gamePanel;
    private static RecordsPanel recordsPanel;

    public Pacman() {
        mainMenuPanel = new MainMenuPanel();
        add(mainMenuPanel);
    }

    public static void showGamePanel() {
        pac.getContentPane().removeAll();
        int selectedDifficulty = mainMenuPanel.getSelectedDifficulty();
        gamePanel = new Model(selectedDifficulty); 
        pac.add(gamePanel);
        pac.revalidate();
        pac.repaint();
        gamePanel.requestFocusInWindow(); 
    }

    public static void showMainMenu() {
        MainMenuPanel.loadRecords(); // 重新加载记录，确保不重复添加
        pac.getContentPane().removeAll();
        pac.add(mainMenuPanel);
        pac.revalidate();
        pac.repaint();
    }
    

    public static void showRecordsPanel() {
        pac.getContentPane().removeAll();
        recordsPanel = new RecordsPanel();
        pac.add(recordsPanel);
        pac.revalidate();
        pac.repaint();
    }

    public static void main(String[] args) {
        pac = new Pacman();
        pac.setVisible(true);
        pac.setTitle("Pacman");

        int blockSize = 24;
        int numBlocks = 25;
        int gameSize = blockSize * numBlocks;
        int windowWidth = gameSize + 16;
        int windowHeight = gameSize + 60;

        pac.setSize(windowWidth, windowHeight);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);
    }
}
