package tetjis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class HoldBlockPanel extends JPanel {
    public static final int WIDTH = Tetjis.sideWIDTH;
    public static final int HEIGHT = Tetjis.HEIGHT;
    public static final int TILE_SIZE = Tetjis.TILE_SIZE;
    private Block holdBlock;
    private int[][] block;
    private Image blockImage;

    public HoldBlockPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.CYAN);
        g.fillRoundRect(16, 10, WIDTH-32, 100, 30, 30);
        Font font = new Font("Sazanami Gothic", Font.PLAIN, 16);
        g.setFont(font);
        g.setColor(Color.BLUE);
        g.drawString("HOLD", 27, 25);

        if ( block == null ) {
            return;
        }
        // ホールドブロックを描画
        int k = 0;
        for (int i = 0; i < Block.ROW_SIZE; i++) {
            for (int j = 0; j < Block.COL_SIZE; j++) {
                if (block[i][j] != 0) {
                    int imageIdx = block[i][j];
                    blockImage = BlockImages.getBlockImage(imageIdx);
                	g.drawImage(blockImage, TILE_SIZE, TILE_SIZE * (k + 1), TILE_SIZE, TILE_SIZE, null);
                    k++;
                }
            }
        }
    }

    /**
     * ホールドブロックをセット
     * 
     * @param holdBlock ホールドブロック
     * @param blockImage ブロックイメージ
     */
    public void set() {
        repaint();
    }
    public void set(Block holdBlock) {
        this.holdBlock = holdBlock;
        this.block = holdBlock.getBlock();
        repaint();
    }

    public Block get() {
        return holdBlock;
    }

    public void holdReset() {
        block = null;
        repaint();
    }
}
