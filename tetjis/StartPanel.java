package tetjis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

import java.awt.Point;
import java.awt.Font;


public class StartPanel extends JPanel implements KeyListener{
    private Image backGroundImage;
    private boolean helpFlag = false; // Trueならば操作方法画面
    private String menu [] = {"激甘", "中辛", "激辛", "操作説明"};
    private String helps [] = {"A ... 左回転", "S ... 右回転", "← ... 左移動",
                               "↓ ... 下移動", "↑ ... 瞬間落下", "W ... ホールド"};
    
    private Point menuPos = new Point(116, 120); // メニューの表記位置
    private int menuSpace = 50; // メニュー同士の高さの間隔
    private Point helpsPos = new Point(111, 140); // ヘルプの表記位置
    private int helpsSpace = 20; // ヘルプ同士の高さの間隔

    private int selectedMenu = 0;
    private boolean gameStart = false;
    protected int difficulty = 0;
    private Tetjis tetjis;

    public StartPanel(int WIDTH, int HEIGHT, Tetjis frame) {
        // パネルの推奨サイズを設定、pack()するときに必要
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        // パネルがキー入力を受け付けるようにする
        setFocusable(true);
        this.tetjis = frame;
        gameStart = false;
        try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void paintComponent(Graphics g) {
        if ( helpFlag ) {
            showHelp(g);
        } else {
            showStartMenu(g, selectedMenu);
        }
    }

    public void showStartMenu(Graphics g, int selectedMenu) {
        backGroundImage = BlockImages.getTitleImage();
        g.drawImage(backGroundImage, 0, 0, this);
        Font font1 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40);
        Font font2 = new Font("メイリオ", Font.BOLD, 30);
        g.setColor(Color.RED);
        g.setFont(font1);
        g.drawString("漢字テトリス", 96, 45);
        g.setFont(font2);
        for (int i = 0; i < menu.length; i++) {
            // 選択されているメニューは色とx座標を変える
            if (i == selectedMenu) {
                g.setColor(Color.ORANGE);
                g.drawString(menu[i], menuPos.x+10, menuPos.y+menuSpace*i);
            } else {
                g.setColor(Color.BLACK);
                g.drawString(menu[i], menuPos.x, menuPos.y+menuSpace*i);
            }
        }
    }

    public void showHelp(Graphics g) {
        Font font = new Font("MSゴシック体", Font.BOLD, 20);
        g.drawImage(backGroundImage, 0, 0, this);
        
        g.setColor(Color.MAGENTA);
        g.setFont(font);
        g.drawString("操作方法", 126, 110);

        for (int i = 0; i < helps.length; i++) {
            g.drawString(helps[i], helpsPos.x, helpsPos.y+helpsSpace*i);
        }
        g.setColor(Color.BLUE);
        g.drawString("メニューに戻る", 101, 340);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (helpFlag){ // ヘルプ画面の時
            if (key == KeyEvent.VK_ENTER) {
                helpFlag = false;
                GameSounds.playSounds("decision");
            }
        } else { // メニュー画面の時
            if (key == KeyEvent.VK_UP) {
                //移動SE
                GameSounds.playSounds("cursol");
                if (selectedMenu > 0) {
                    selectedMenu--;
                }
            } else if (key == KeyEvent.VK_DOWN) {
                //移動SE
                GameSounds.playSounds("cursol");
                if (selectedMenu < menu.length-1) {
                    selectedMenu++;
                }
            } else if (key == KeyEvent.VK_ENTER) {
                GameSounds.playSounds("decision");
                if (selectedMenu == menu.length-1) {
                    helpFlag = true;
                } else {
                    difficulty = selectedMenu;
                    tetjis.gameStart(difficulty);
                }
            }
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public boolean startGame(){
        return gameStart;
    }

    public int getSelectedDifficulty(){
        return difficulty;
    }

}
