package tetjis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

import java.awt.Font;
import java.text.DecimalFormat;


public class FinishPanel extends JPanel implements KeyListener{
    private Image backGroundImage;
    private String menu [] = {"もう一度", "タイトル画面へ"};
    private int selectedMenu = 0;
    private int difficulty = 0;
    private Tetjis tetjis;
    private int WIDTH, HEIGHT;
    private ScorePanel scorePanel;
    public FinishPanel(int WIDTH, int HEIGHT, Tetjis tetjis, int difficulty, ScorePanel scorePanel) {
        // パネルの推奨サイズを設定、pack()するときに必要
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        // パネルがキー入力を受け付けるようにする
        setFocusable(true);
        this.scorePanel = scorePanel;
        this.HEIGHT = HEIGHT;
        this.WIDTH = WIDTH;
        this.tetjis = tetjis;
        this.difficulty = difficulty;
        try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void paintComponent(Graphics g) {
        finishMenu(g);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            //移動SE
            GameSounds.playSounds("cursol");
            if (selectedMenu > 0) {
                selectedMenu--;
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            //移動SE
            GameSounds.playSounds("cursol");
            if (selectedMenu < menu.length-1) {
                selectedMenu++;
            }
        } else if (key == KeyEvent.VK_ENTER) {
            GameSounds.playSounds("decision");
            if ( selectedMenu == 0 ) {
                tetjis.gameStart(difficulty);
            } else {
                tetjis.startMenu();
            }
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }


    public void finishMenu(Graphics g) {
        DecimalFormat formatter = new DecimalFormat("000000");
        Font font1 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40);
        Font font2 = new Font("MSゴシック体", Font.BOLD, 20);
        /*
        backGroundImage = BlockImages.getFinishImage();
        g.drawImage(backGroundImage, 0, 0, this);
        */
        g.drawRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.RED);
        g.setFont(font1);
        g.drawString("GAME OVER", 96, 180);
        g.setFont(font2);
        g.setColor(Color.ORANGE);
        g.drawString("SCORE：" + formatter.format(scorePanel.getScore()), 136, 325);
        if (selectedMenu == 0) {
            g.setColor(Color.BLUE);
            g.setFont(font2);
            g.drawString("もう一度！", 96, 360);
            g.setColor(Color.BLACK);
            g.setFont(font2);
            g.drawString("タイトル画面へ", 201, 370);
        } else {
            g.setColor(Color.BLACK);
            g.setFont(font2);
            g.drawString("もう一度！", 90, 370);
            g.setColor(Color.BLUE);
            g.setFont(font2);
            g.drawString("タイトル画面へ", 201, 360);
        }
    }
}
