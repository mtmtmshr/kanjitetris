package tetjis;

import tetjis.*;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Tetjis extends JFrame {
    public static final int WIDTH = 448;
    public static final int HEIGHT = 416;
    public static final int TILE_SIZE = 32;
    public static final int gameWIDTH = 256;
    public static final int sideWIDTH = 96;
    private StartPanel startPanel;
    private MainPanel mainPanel;
    private ScorePanel scorePanel;
    private NextBlockPanel nextBlockPanel;
    private HoldBlockPanel holdBlockPanel;
    private FinishPanel finishPanel;
    private Container contentPane = getContentPane();
    public Tetjis(){
        GameSounds.loadSounds();
        BlockImages.loadImages();
        scorePanel = new ScorePanel();
        nextBlockPanel = new NextBlockPanel();
        holdBlockPanel = new HoldBlockPanel();
        //mainPanel = new MainPanel(WIDTH, HEIGHT, difficulty, scorePanel, nextBlockPanel, holdBlockPanel)
    }

    public void startMenu() {
        startPanel = new StartPanel(WIDTH, HEIGHT, this);
        contentPane.removeAll();
        contentPane.add(startPanel, BorderLayout.CENTER);
        startPanel.requestFocus();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void gameStart(int gameDifficulty){
        contentPane.removeAll();
        JPanel rightPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        mainPanel = new MainPanel(gameWIDTH, HEIGHT, gameDifficulty, scorePanel, 
                                  nextBlockPanel, holdBlockPanel, this);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        rightPanel.add(scorePanel, BorderLayout.NORTH);
        rightPanel.add(nextBlockPanel, BorderLayout.CENTER);
        leftPanel.add(holdBlockPanel, BorderLayout.NORTH);
        contentPane.add(rightPanel, BorderLayout.EAST);
        
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(leftPanel, BorderLayout.WEST);
        mainPanel.requestFocus();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        Thread gameThread = new Thread(mainPanel);
        Thread scoreThread = new Thread(scorePanel);
        GameSounds sounds = new GameSounds();
        sounds.setGameThread(gameThread);
        scorePanel.setGameThread(gameThread);
        Thread soundsThread = new Thread(sounds);
        gameThread.start();
        scoreThread.start();
        soundsThread.start();
    }


    public void finishMenu(int difficulty) {
        contentPane.removeAll();
        finishPanel = new FinishPanel(WIDTH, HEIGHT, this, difficulty, scorePanel);
        contentPane.add(finishPanel, BorderLayout.CENTER);
        finishPanel.requestFocus();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        Tetjis tetjis = new Tetjis();
        tetjis.startMenu();
    }
}