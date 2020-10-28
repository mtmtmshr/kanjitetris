package tetjis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Font;


public class NextBlockPanel extends JPanel {
    public static final int WIDTH = Tetjis.sideWIDTH;
    public static final int HEIGHT = Tetjis.HEIGHT-ScorePanel.HEIGHT;
    public static final int TILE_SIZE = Tetjis.TILE_SIZE;
    private int[][] nextBlock;
    private int[][] afterNextBlock;
    private Image blockImage;
    public NextBlockPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.PINK);
        g.fillRoundRect(11, 0, WIDTH-32, 100, 30, 25);
        Font font = new Font("Sazanami Gothic", Font.PLAIN, 16);
        g.fillRoundRect(24, TILE_SIZE*2, WIDTH-32, 100, 30, 25);
        g.setFont(font);
        g.setColor(Color.BLUE);
        g.drawString("NEXT", 21, 20);

        if ( nextBlock == null || afterNextBlock == null ) {
            return;
        }
        
        int k = 0;
        for (int i = 0; i < Block.ROW_SIZE; i++) {
            for (int j = 0; j < Block.COL_SIZE; j++) {
                if (nextBlock[i][j] != 0) {
                    int imageIdx = nextBlock[i][j];
                    blockImage = BlockImages.getBlockImage(imageIdx);
                	g.drawImage(blockImage, TILE_SIZE-5, TILE_SIZE * (k + 1)-10, TILE_SIZE, TILE_SIZE, null);
                    k++;
                }
            }
        }

        for (int i = 0; i < Block.ROW_SIZE; i++) {
            for (int j = 0; j < Block.COL_SIZE; j++) {
                if (afterNextBlock[i][j] != 0) {
                    int imageIdx = afterNextBlock[i][j];
                    blockImage = BlockImages.getBlockImage(imageIdx);
                	g.drawImage(blockImage, TILE_SIZE+8, TILE_SIZE * (k + 1)-10, TILE_SIZE, TILE_SIZE, null);
                    k++;
                }
            }
        }
    }

    public void setBlock(Block nextBlock, Block afterNextBlock) {
        this.nextBlock = nextBlock.getBlock();
        this.afterNextBlock = afterNextBlock.getBlock();
        repaint();
    }
}
