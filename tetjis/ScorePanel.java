package tetjis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JPanel;


public class ScorePanel extends JPanel implements Runnable{
    // パネルサイズ
    public static final int WIDTH = Tetjis.sideWIDTH;
    public static final int HEIGHT = 16;
    // スコア
    private int score;
    private Thread gameThread;
    public ScorePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // スコアを描画
        g.setColor(Color.RED);
        DecimalFormat formatter = new DecimalFormat("000000");
        // フォントを作成
        Font font = new Font("Ariel", Font.BOLD, 16);
        g.setFont(font);

        g.drawString(formatter.format(score), 16, 12);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void upScore(int d) {
        score += d;
    }

    public int getScore() {
        return score;
    }

    public void set() {
        repaint();
    }

    public void setGameThread(Thread thread) {
        this.gameThread = thread;
    }

    public void run() {
        try {
			this.gameThread.join();
		} catch (InterruptedException e) {
			// 例外処理
			e.printStackTrace();
		}
    }
}
