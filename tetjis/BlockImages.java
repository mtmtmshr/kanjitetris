package tetjis;
import java.awt.Image;
import javax.swing.ImageIcon;


public class BlockImages {
    public static int NUM = 1004;
    public static Image blockImages[] = new Image[NUM];
    public static Image colorBlockImages[] = new Image[NUM];
    public static Image batsuImage;
    public static Image titleImage;
    public static Image finishImage;

    static {
        blockImages[1] = loadImage("tetjis/data/image/1.png");
        for ( int i=2; i < NUM; i++ ) {
            blockImages[i] = loadImage("tetjis/data/image/" + i + ".png");
            colorBlockImages[i] = loadImage("tetjis/data/image/color/" + i + ".png");
        }
        batsuImage = loadImage("tetjis/data/image/batsu.png");
        titleImage = loadImage("tetjis/data/image/titleimage1.png");
        finishImage = loadImage("tetjis/data/image/3131_2.jpg");
    }

    public static Image loadImage(String filename) {
        // ブロックのイメージを読み込む
        ImageIcon icon = new ImageIcon(BlockImages.class.getClassLoader().getResource(filename));
        return icon.getImage();        
    }

    public static Image getColorBlockImage(int idx) {
        return colorBlockImages[idx];
    }

    public static Image getBlockImage(int idx) {
        return blockImages[idx];
    }

    public static Image getBatsu() {
        return batsuImage;
    }

    public static Image getTitleImage() {
        return titleImage;
    }

    public static Image getFinishImage() {
        return finishImage;
    }
}