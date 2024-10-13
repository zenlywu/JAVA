package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;
    private boolean paused = false;
    private boolean recordSaved = false;
    private boolean win = false; // 新增标志位
    private boolean swordActive = false; 
    private boolean shieldActive = false; 

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 25;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 20;
    private int PACMAN_SPEED = 4;
    private int POWERUP_NUM = 0;
    private int POWERDOWN_NUM = 0;
    private int swordTimer = 0; 
    private int shieldTimer = 0; // 盾牌计时器

    private int N_GHOSTS;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy;
    private int ghostSpeed;

    private Image heart, ghost;
    private Image up, down, left, right;
    private Image powerup, powerdown;
    private Image sword,shield;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;

    private  short levelData[];
    private  short levelData1[] = {
        19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,  5, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 16, 24, 24, 24, 20,  5, 17, 24, 24, 24, 16, 24, 24, 24, 24, 24, 16, 20,
        17, 20,  3,  2,  2,  2,  6, 21,  3,  2,  6, 21,  5, 21,  3,  2,  6, 21,  3,  2,  2,  2,  6, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21,  5, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21, 13, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  9,  8,  8,  8, 12, 21,  9,  8, 12, 17, 18, 20,  9,  8, 12, 21,  9,  8,  8,  8, 12, 17, 20,
        17, 16, 26, 26, 18, 26, 26, 64, 26, 18, 26, 24, 24, 24, 26, 18, 26, 16, 26, 26, 18, 26, 26, 32, 20,
        17, 20,  3,  6, 21,  3,  6, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21,  3,  6, 21,  3,  6, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 25, 26, 22,  5, 19, 26, 28,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  1, 10, 10, 21, 13, 21, 11, 10,  4, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 19, 26, 24, 18, 24, 26, 22,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  9, 12, 21,  9, 12, 21,  13,21,  3, 14,  5, 11,  6, 21, 13, 21,  9, 12, 21,  9, 12, 17, 20,
        17, 16, 26, 26, 24, 26, 26, 16, 26, 20,  5,  7,  5,  7,  5, 17, 26, 16, 26, 26, 24, 26, 26, 16, 20,
        17, 20, 11, 10, 10, 10,  6, 21,  7, 21,  5,  5,  5,  5,  5, 21,  7, 21,  3, 10, 10, 10, 14, 17, 20,
        17, 16, 18, 18, 18, 22,  5, 21,  5, 21,  5,  9,  8, 12,  5, 21,  5, 21,  5, 19, 18, 18, 18, 16, 20,
        25, 24, 24, 24, 24, 20,  5, 21, 13, 21,  9, 10, 10, 10, 12, 21, 13, 21,  5, 17, 24, 24, 24, 24, 28,
         3,  2,  2,  2,  6, 21,  5, 17, 26, 16, 26, 26, 26, 26, 26,256, 26, 20,  5, 21,  3,  2,  2,  2,  6,
         9,  8,  8,  8, 12, 21, 13, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21, 13, 21,  9,  8,  8,  8, 12,
        19, 18, 18, 18, 18,128, 18, 20,  5, 17, 18, 22,  5, 19, 18, 20,  5, 17, 18, 16, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 28,  5, 25, 24, 20,  5, 17, 24, 28,  5, 25, 24, 24, 24, 24, 24, 16, 20,
        17, 20, 11, 10, 10, 10, 10, 10,  8, 10, 14, 21, 13, 21, 11, 10,  8, 10, 10, 10, 10, 10, 14, 17, 20,
        17, 32, 18, 18, 18, 26, 26, 26, 26, 26, 26, 16, 26, 16, 26, 26, 26, 26, 26, 26, 18, 18, 18, 64, 20,
        17, 24, 24, 24, 28,  3, 10, 10, 10, 10, 14, 21,  7, 21, 11, 10, 10, 10, 10,  6, 25, 24, 24, 24, 20,
        21, 11, 10, 10, 10, 12, 19, 18, 18, 18, 18, 20, 13, 17, 18, 18, 18, 18, 22,  9, 10, 10, 10, 14, 21,
        25, 26, 26, 26, 26, 26, 24, 24, 24, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 26, 26, 26, 26, 26, 28
    };
    private  short levelData2[] = {
        19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,  5, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 16, 24, 24, 24, 20,  5, 17, 24, 24, 24, 16, 24, 24, 24, 24, 24, 16, 20,
        17, 20,  3,  2,  2,  2,  6, 21,  3,  2,  6, 21,  5, 21,  3,  2,  6, 21,  3,  2,  2,  2,  6, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21,  5, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21, 13, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  9,  8,  8,  8, 12, 21,  9,  8, 12, 17, 18, 20,  9,  8, 12, 21,  9,  8,  8,  8, 12, 17, 20,
        17, 16, 26, 26, 18, 26, 26, 64, 26, 18, 26, 24, 24, 24, 26, 18, 26, 16, 26, 26, 18, 26, 26,128, 20,
        17, 20,  3,  6, 21,  3,  6, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21,  3,  6, 21,  3,  6, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 25, 26, 22,  5, 19, 26, 28,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  1, 10, 10, 21, 13, 21, 11, 10,  4, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 19, 26, 24, 18, 24, 26, 22,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  9, 12, 21,  9, 12, 21,  13,21,  3, 14,  5, 11,  6, 21, 13, 21,  9, 12, 21,  9, 12, 17, 20,
        17, 16, 26, 26, 24, 26, 26, 16, 26, 20,  5,  7,  5,  7,  5, 17, 26, 32, 26, 26, 24, 26, 26, 16, 20,
        17, 20, 11, 10, 10, 10,  6, 21,  7, 21,  5,  5,  5,  5,  5, 21,  7, 21,  3, 10, 10, 10, 14, 17, 20,
        17, 16, 18, 18, 18, 22,  5, 21,  5, 21,  5,  9,  8, 12,  5, 21,  5, 21,  5, 19, 18, 18, 18, 16, 20,
        25, 24, 24, 24, 24, 20,  5, 21, 13, 21,  9, 10, 10, 10, 12, 21, 13, 21,  5, 17, 24, 24, 24, 24, 28,
         3,  2,  2,  2,  6, 21,  5, 17, 26, 16, 26, 26, 26, 26, 26, 16, 26, 20,  5, 21,  3,  2,  2,  2,  6,
         9,  8,  8,  8, 12, 21, 13, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21, 13, 21,  9,  8,  8,  8, 12,
        19, 18, 18, 18, 18,256, 18, 20,  5, 17, 18, 22,  5, 19, 18, 20,  5, 17, 18, 64, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 28,  5, 25, 24, 20,  5, 17, 24, 28,  5, 25, 24, 24, 24, 24, 24, 16, 20,
        17, 20, 11, 10, 10, 10, 10, 10,  8, 10, 14, 21, 13, 21, 11, 10,  8, 10, 10, 10, 10, 10, 14, 17, 20,
        17, 32, 18, 18, 18, 26, 26, 26, 26, 26, 26, 16, 26, 16, 26, 26, 26, 26, 26, 26, 18, 18, 18, 16, 20,
        17, 24, 24, 24, 28,  3, 10, 10, 10, 10, 14, 21,  7, 21, 11, 10, 10, 10, 10,  6, 25, 24, 24, 24, 20,
        21, 11, 10, 10, 10, 12, 19, 18, 18, 18, 18, 20, 13, 17, 18, 18, 18, 18, 22,  9, 10, 10, 10, 14, 21,
        25, 26, 26, 26, 26, 26, 24, 24, 24, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 26, 26, 26, 26, 26, 28
    };
    private  short levelData3[] = {
        19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,  5, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 16, 24, 24, 24, 20,  5, 17, 24, 24, 24, 16, 24, 24, 24, 24, 24, 16, 20,
        17, 20,  3,  2,  2,  2,  6, 21,  3,  2,  6, 21,  5, 21,  3,  2,  6, 21,  3,  2,  2,  2,  6, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21,  5, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  1,  0,  0,  0,  4, 21,  1,  0,  4, 21, 13, 21,  1,  0,  4, 21,  1,  0,  0,  0,  4, 17, 20,
        17, 20,  9,  8,  8,  8, 12, 21,  9,  8, 12, 17, 18, 20,  9,  8, 12, 21,  9,  8,  8,  8, 12, 17, 20,
        17, 16, 26, 26, 18, 26, 26, 64, 26, 18, 26, 24, 24, 24, 26, 18, 26, 16, 26, 26, 18, 26, 26, 32, 20,
        17, 20,  3,  6, 21,  3,  6, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21,  3,  6, 21,  3,  6, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 25, 26, 22,  5, 19, 26, 28,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  1, 10, 10, 21, 13, 21, 11, 10,  4, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  1,  4, 21,  1,  4, 21,  5, 19, 26, 24, 18, 24, 26, 22,  5, 21,  1,  4, 21,  1,  4, 17, 20,
        17, 20,  9, 12, 21,  9, 12, 21,  13,21,  3, 14,  5, 11,  6, 21, 13, 21,  9, 12, 21,  9, 12, 17, 20,
        17, 16, 26, 26, 24, 26, 26,256, 26, 20,  5,  7,  5,  7,  5, 17, 26,128, 26, 26, 24, 26, 26, 16, 20,
        17, 20, 11, 10, 10, 10,  6, 21,  7, 21,  5,  5,  5,  5,  5, 21,  7, 21,  3, 10, 10, 10, 14, 17, 20,
        17, 16, 18, 18, 18, 22,  5, 21,  5, 21,  5,  9,  8, 12,  5, 21,  5, 21,  5, 19, 18, 18, 18, 16, 20,
        25, 24, 24, 24, 24, 20,  5, 21, 13, 21,  9, 10, 10, 10, 12, 21, 13, 21,  5, 17, 24, 24, 24, 24, 28,
         3,  2,  2,  2,  6, 21,  5, 17, 26, 16, 26, 26, 26, 26, 26, 16, 26, 20,  5, 21,  3,  2,  2,  2,  6,
         9,  8,  8,  8, 12, 21, 13, 21,  7, 21, 11, 10,  2, 10, 14, 21,  7, 21, 13, 21,  9,  8,  8,  8, 12,
        19, 18, 18, 18, 18, 16, 18, 20,  5, 17, 18, 22,  5, 19, 18, 20,  5, 17, 18, 64, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 24, 24, 28,  5, 25, 24, 20,  5, 17, 24, 28,  5, 25, 24, 24, 24, 24, 24, 16, 20,
        17, 20, 11, 10, 10, 10, 10, 10,  8, 10, 14, 21, 13, 21, 11, 10,  8, 10, 10, 10, 10, 10, 14, 17, 20,
        17, 32, 18, 18, 18, 26, 26, 26, 26, 26, 26,256, 26, 16, 26, 26, 26, 26, 26, 26, 18, 18, 18, 16, 20,
        17, 24, 24, 24, 28,  3, 10, 10, 10, 10, 14, 21,  7, 21, 11, 10, 10, 10, 10,  6, 25, 24, 24, 24, 20,
        21, 11, 10, 10, 10, 12, 19, 18, 18, 18, 18, 20, 13, 17, 18, 18, 18, 18, 22,  9, 10, 10, 10, 14, 21,
        25, 26, 26, 26, 26, 26, 24, 24, 24, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 26, 26, 26, 26, 26, 28
    };
    //private final int maxSpeed = 6;
    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    private Timer gameTimer; 
    private int elapsedTimeInSeconds; 

    private int[][] scoresAndTimes = new int[3][2]; 
    private int gameIndex = 0; 

    private int difficulty;

    public Model(int difficulty) {
        this.difficulty = difficulty;
        setDifficulty(difficulty);
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    private void loadImages() {
        down = new ImageIcon(getClass().getResource("/pacman/images/down.gif")).getImage();
        up = new ImageIcon(getClass().getResource("/pacman/images/up.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/pacman/images/left.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/pacman/images/right.gif")).getImage();
        ghost = new ImageIcon(getClass().getResource("/pacman/images/ghost.gif")).getImage();
        heart = new ImageIcon(getClass().getResource("/pacman/images/heart.png")).getImage();
        powerup = new ImageIcon(getClass().getResource("/pacman/images/powerup.png")).getImage(); 
        powerdown = new ImageIcon(getClass().getResource("/pacman/images/powerdown.png")).getImage();
        sword = new ImageIcon(getClass().getResource("/pacman/images/sword.png")).getImage();  
        shield = new ImageIcon(getClass().getResource("/pacman/images/shield.png")).getImage();
    }

    private void setDifficulty(int difficulty) {
        switch (difficulty) {
            case 0: 
                ghostSpeed = 2;
                N_GHOSTS = 6;
                levelData = levelData1;
                break;
            case 1: 
                ghostSpeed = 4;
                N_GHOSTS = 12;
                levelData = levelData2;
                break;
            case 2: 
                ghostSpeed = 6;
                N_GHOSTS = 16;
                levelData = levelData3;
                break;
            default:
                ghostSpeed = 2; 
                N_GHOSTS = 6;
        }
    }

    private void initVariables() {
        if (levelData.length > N_BLOCKS * N_BLOCKS) {
            throw new IllegalStateException("Level data size does not match N_BLOCKS * N_BLOCKS");
        }
        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(SCREEN_SIZE, SCREEN_SIZE);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
    
        timer = new Timer(40, this);
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTimeInSeconds++;
                if (swordActive) {
                    swordTimer++;
                    if (swordTimer >= 15) {
                        swordActive = false;
                        swordTimer = 0;
                    }
                }
                if (shieldActive) {
                    shieldTimer++;
                    if (shieldTimer >= 15) {
                        shieldActive = false;
                        shieldTimer = 0;
                    }
                }
            }
        });
        loadRecords(); // 加载记录
    }
    
    private String getFileName() {
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
    private void loadRecords() {
        String fileName = getFileName();
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainMenuPanel.setGameRecords(difficulty, records);
    }
    
    private void playGame(Graphics2D g2d) {
        if (dying) {
            death();
        } else {
            if (PACMAN_SPEED == 0) {
                death();
            } else {
                movePacman();
                drawPacman(g2d);
                moveGhosts(g2d);
                checkMaze();
            }
        }
    }
    
    private int[] getHighestScoreData() {
        int highestScore = 0;
        int correspondingTime = Integer.MAX_VALUE;
    
        for (int i = 0; i < scoresAndTimes.length; i++) {
            if (scoresAndTimes[i][0] > highestScore || 
                (scoresAndTimes[i][0] == highestScore && scoresAndTimes[i][1] < correspondingTime)) {
                highestScore = scoresAndTimes[i][0];
                correspondingTime = scoresAndTimes[i][1];
            }
        }
    
        return new int[]{highestScore, correspondingTime};
    }
    
    
    private void showIntroScreen(Graphics2D g2d) {
        Font startFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(startFont);
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, 150, 260);
        Font EscFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(EscFont);
        String Esc = "Press Esc to menu";
        g2d.setColor(Color.YELLOW);
        g2d.drawString(Esc, 170, 220);
    }

    private void showIntroGameOver(Graphics2D g2d) {
        Font EscFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(EscFont);
        String Esc = "Press Esc to menu";
        g2d.setColor(Color.YELLOW);
        g2d.drawString(Esc, 170, 220);
    
        Font gameOverFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(gameOverFont);
        String gameOver = "Game Over";
        g2d.setColor(Color.red);
        int gameOverWidth = g2d.getFontMetrics().stringWidth(gameOver);
        int gameOverX = (SCREEN_SIZE - gameOverWidth) / 2;
        int gameOverY = SCREEN_SIZE / 2;
        g2d.drawString(gameOver, gameOverX, gameOverY);
    
        // 获取最高分和对应时间
        int[] highestScoreData = getHighestScoreData();
        int highestScore = highestScoreData[0];
        int correspondingTime = highestScoreData[1];
    
        // 显示最高分和时间
        String highestScoreText = "Highest Score: " + highestScore;
        String correspondingTimeText = "Time: " + correspondingTime + " s";
        int highestScoreWidth = g2d.getFontMetrics().stringWidth(highestScoreText);
        int correspondingTimeWidth = g2d.getFontMetrics().stringWidth(correspondingTimeText);
        g2d.drawString(highestScoreText, (SCREEN_SIZE - highestScoreWidth) / 2, gameOverY + 40);
        g2d.drawString(correspondingTimeText, (SCREEN_SIZE - correspondingTimeWidth) / 2, gameOverY + 80);
    
        // 保存记录
        if (!recordSaved) {
            saveRecord(highestScore, correspondingTime);
            recordSaved = true;
        }
        highestScore = 0;
        correspondingTime = 0;
    }
    
    private void saveRecord(int highestScore, int correspondingTime) {
        MainMenuPanel.addGameRecord(difficulty, highestScore, correspondingTime);
        MainMenuPanel.saveRecords(difficulty);
        MainMenuPanel.loadRecords();
    }
    
    private void showWinScreen(Graphics2D g2d) {
        Font winFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(winFont);
        String winMessage = "YOU WIN!";
        g2d.setColor(Color.GREEN);
        int winWidth = g2d.getFontMetrics().stringWidth(winMessage);
        g2d.drawString(winMessage, (SCREEN_SIZE - winWidth) / 2, SCREEN_SIZE / 2);
    
        // 添加额外的提示信息
        Font infoFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(infoFont);
        String escMessage = "Press ESC to menu";
        String startMessage = "Press SPACE to start";
        int escWidth = g2d.getFontMetrics().stringWidth(escMessage);
        int startWidth = g2d.getFontMetrics().stringWidth(startMessage);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(escMessage, (SCREEN_SIZE - escWidth) / 2, SCREEN_SIZE / 2 + 40);
        g2d.drawString(startMessage, (SCREEN_SIZE - startWidth) / 2, SCREEN_SIZE / 2 + 80);
    
        // 保存记录
        if (!recordSaved) {
            saveRecord(score, elapsedTimeInSeconds);
            recordSaved = true;
        }
    }
    
    private void drawScore(Graphics2D g) {
        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    
        g.setFont(smallFont);
    
        // 定义文本和颜色
        String scoreText = "Score: " + score;
        String timeText = "Time: " + elapsedTimeInSeconds + "s";
        String swordText = "Sword: " + (swordActive ? (15 - swordTimer) + "s" : " ");
        String shieldText = "Shield: " + (shieldActive ? (15 - shieldTimer) + "s" : " ");
    
        // 获取每个文本的宽度
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        int timeWidth = g.getFontMetrics().stringWidth(timeText);
        int swordWidth = g.getFontMetrics().stringWidth(swordText);
        int shieldWidth = g.getFontMetrics().stringWidth(shieldText);
    
        // 预留额外的宽度给 Sword 和 Shield 的秒数部分
        int swordBuffer = g.getFontMetrics().stringWidth("Sword: 15s");
        int shieldBuffer = g.getFontMetrics().stringWidth("Shield: 15s");
    
        // 总宽度和可用空间
        int totalTextWidth = scoreWidth + timeWidth + swordBuffer + shieldBuffer;
        int availableSpace = SCREEN_SIZE - 16 - (lives * 28) - totalTextWidth;
        int spacing = availableSpace / 3; // 计算每个文本之间的间隔
    
        // 绘制 Score
        g.setColor(new Color(5, 181, 79));
        int x = 8 + (lives * 28) + 10;
        g.drawString(scoreText, x, SCREEN_SIZE + 16);
        x += scoreWidth + spacing;
    
        // 绘制 Time
        g.drawString(timeText, x, SCREEN_SIZE + 16);
        x += timeWidth + spacing;
    
        // 绘制 Sword
        g.setColor(new Color(255, 0, 0));
        g.drawString(swordText, x, SCREEN_SIZE + 16);
        x += swordBuffer + spacing; // 使用 buffer 宽度
    
        // 绘制 Shield
        g.setColor(new Color(0, 0, 255));
        g.drawString(shieldText, x, SCREEN_SIZE + 16);
    }
    
    private void checkMaze() {
        int i = 0;
        boolean finished = true;
    
        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i] & 16) != 0) { // 如果有豆子
                finished = false;
            }
            i++;
        }
    
        if (finished) {
            score += 50;
            win = true; // 设置游戏通关标志位
            inGame = false; // 结束游戏
            gameTimer.stop();
            timer.stop();
            
            // 保存记录
            if (!recordSaved) {
                saveRecord(score, elapsedTimeInSeconds);
                recordSaved = true;
            }
        }
    }

    private void recordGame() {
        // Add the current game's score and time to scoresAndTimes
        scoresAndTimes[gameIndex][0] = score;
        scoresAndTimes[gameIndex][1] = elapsedTimeInSeconds;
    
        int[] highestScoreData = getHighestScoreData();
        int highestScore = highestScoreData[0];
        int correspondingTime = highestScoreData[1];
    
        MainMenuPanel.addGameRecord(difficulty, highestScore, correspondingTime);
        MainMenuPanel.saveRecords(difficulty);
    }
    

    private void death() {
        lives--;
    
        if (lives == 0) {
            inGame = false;
            gameTimer.stop();
            timer.stop();
            recordGame(); // 确保在游戏结束时记录游戏分数
            recordSaved = false;
        } else {
            scoresAndTimes[gameIndex][0] = score;
            scoresAndTimes[gameIndex][1] = elapsedTimeInSeconds;
            gameIndex++;
            PACMAN_SPEED -= 2 * POWERUP_NUM;
            PACMAN_SPEED += 2 * POWERDOWN_NUM;
            POWERUP_NUM = 0;
            POWERDOWN_NUM = 0;
            elapsedTimeInSeconds = 0;
            resetLevel();
        }
    
        continueLevel();
    }
    

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;
    
        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);
    
                count = 0;
    
                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }
    
                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }
    
                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }
    
                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }
    
                if (count == 0) {
    
                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }
    
                } else {
    
                    count = (int) (Math.random() * count);
    
                    if (count > 3) {
                        count = 3;
                    }
    
                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }
    
            }
    
            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);
    
            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                && inGame) {
                if (swordActive) {
                    ghost_x[i] = -100; 
                    ghost_y[i] = -100; 
                    score += 50;
                } else if (shieldActive) {
                    // Do nothing, Pacman is protected
                } else {
                    dying = true;
                }
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
    }

    private void movePacman() {
        if (PACMAN_SPEED == 0) {
            death();
            return;
        }
        int pos;
        short ch;
    
        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];
    
            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }
    
            if ((ch & 32) != 0) {
                screenData[pos] = (short) (ch & 15);
                POWERUP_NUM += 1;
                PACMAN_SPEED += 2;
            }
    
            if ((ch & 64) != 0) {
                screenData[pos] = (short) (ch & 15);
                POWERDOWN_NUM += 1;
                PACMAN_SPEED -= 2;
            }
    
            if ((ch & 128) != 0) {
                screenData[pos] = (short) (ch & 15);
                swordActive = true;
                swordTimer = 0;
            }
    
            if ((ch & 256) != 0) { // 处理盾牌道具
                screenData[pos] = (short) (ch & 15);
                shieldActive = true;
                shieldTimer = 0;
            }
    
            if (req_dx != 0 || req_dy != 0) {
                if ((req_dx == -1 && req_dy == 0 && (screenData[pos] & 1) == 0)
                        || (req_dx == 1 && req_dy == 0 && (screenData[pos] & 4) == 0)
                        || (req_dx == 0 && req_dy == -1 && (screenData[pos] & 2) == 0)
                        || (req_dx == 0 && req_dy == 1 && (screenData[pos] & 8) == 0)) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }
    
            if ((pacmand_x == -1 && pacmand_y == 0 && (screenData[pos] & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (screenData[pos] & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (screenData[pos] & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (screenData[pos] & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }
    
    

    private void drawPacman(Graphics2D g2d) {
        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;
    
        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
                g2d.setColor(new Color(0, 72, 251));
                g2d.setStroke(new BasicStroke(5));
    
                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }
    
                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }
    
                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }
    
                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }
    
                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }
    
                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }
    
                if ((screenData[i] & 32) != 0) {
                    g2d.drawImage(powerup, x + 8, y + 8, this);
                }
    
                if ((screenData[i] & 64) != 0) {
                    g2d.drawImage(powerdown, x + 8, y + 8, this);
                }
                if ((screenData[i] & 128) != 0) {
                    g2d.drawImage(sword, x + 8, y + 8, this);
                }
                if ((screenData[i] & 256) != 0) { // 绘制盾牌道具
                    g2d.drawImage(shield, x + 8, y + 8, this);
                }
                i++;
            }
        }
    }
    

    void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        elapsedTimeInSeconds = 0;
        win = false;
        gameTimer.start();
    }

    public void resetGame() {
        inGame = false;
        dying = false;
        win = false;
        lives = 3;
        score = 0;
        elapsedTimeInSeconds = 0;
        gameIndex = 0;
        initLevel();
    }

    private void initLevel() {
        for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_x[i] = 12 * BLOCK_SIZE;
            ghost_y[i] = 14 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }
        }
        pacman_x = 0 * BLOCK_SIZE;
        pacman_y = 24 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        dying = false;
    }

    private void resetLevel() {
        initLevel();
        score = 0;
    }

    private void showPauseScreen(Graphics2D g2d) {
        Font pauseFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(pauseFont);
        String pause = "Paused";
        g2d.setColor(Color.yellow);
        int pauseWidth = g2d.getFontMetrics().stringWidth(pause);
        g2d.drawString(pause, (SCREEN_SIZE - pauseWidth) / 2, SCREEN_SIZE / 2);
    }

    @Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.black);
    g2d.fillRect(0, 0, d.width, d.height);

    drawMaze(g2d);
    drawScore(g2d); // 确保这行代码在这里

    if (inGame) {
        if (paused) {
            showPauseScreen(g2d);
        } else {
            playGame(g2d);
        }
    } else {
        if (win) {
            showWinScreen(g2d); // 显示通关信息
        } else if (lives == 0) {
            showIntroScreen(g2d);
            showIntroGameOver(g2d);
        } else {
            showIntroScreen(g2d); // 确保显示初始屏幕时不被覆盖
        }
    }

    Toolkit.getDefaultToolkit().sync();
    g2d.dispose();
}

    
    private void togglePause() {
        if (paused) {
            paused = false;
            gameTimer.start();
            timer.start();
        } else {
            paused = true;
            gameTimer.stop();
            timer.stop();
        }
    }
    
    class TAdapter extends KeyAdapter {
        @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (inGame) {
            if (key == KeyEvent.VK_LEFT) {
                req_dx = -1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                req_dx = 1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_UP) {
                req_dx = 0;
                req_dy = -1;
            } else if (key == KeyEvent.VK_DOWN) {
                req_dx = 0;
                req_dy = 1;
            } else if (key == KeyEvent.VK_ENTER) {
                togglePause();
            } else if (key == KeyEvent.VK_ESCAPE) {
                Pacman.showMainMenu();
            }
        } else {
            if (key == KeyEvent.VK_SPACE) {
                inGame = true;
                initGame();
                elapsedTimeInSeconds = 0;
                gameTimer.start();
                timer.start();
            } else if (key == KeyEvent.VK_ESCAPE) {
                Pacman.showMainMenu();
            }
        }

        // 添加对通关状态的检测
        if (win) {
            if (key == KeyEvent.VK_SPACE) {
                win = false;
                inGame = true;
                resetGame();
                elapsedTimeInSeconds = 0;
                gameTimer.start();
                timer.start();
            } else if (key == KeyEvent.VK_ESCAPE) {
                Pacman.showMainMenu();
            }
        }
    }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
