package tetjis;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Image;


public class Block{
    // ブロックのサイズ
    public static final int ROW_SIZE = 3;
    public static final int COL_SIZE = 3;

    // 1マスのサイズ
    private static final int TILE_SIZE = Tetjis.TILE_SIZE;

    // 移動方向
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int FALL = 3;
    private static int difficulty = 0;
    private int blockFolm; // 縦のとき0 横のとき1
    // ブロックの形を格納
    private int[][] block = new int[ROW_SIZE][COL_SIZE];
    // 位置（単位：マス）
    private Point pos;
    // フィールドへの参照
    private Field field;
    private boolean fixedFlag;

    public static void setDifficulty(int difficulty) {
        Block.difficulty = difficulty;
    }

    public Block(Field field) {
        this.field = field;
        init();
        // 四角いブロックを作成
        // □■□
        // □■□
        // □□□
        block[0][1] = WeightedRandom.RandomInt(difficulty);
        block[1][1] = WeightedRandom.RandomInt(difficulty);
        fixedFlag = false;
        // ブロックの形は縦
        blockFolm = 0;
        // 初期位置をセット
        posInit();
    }


    /**
     * ブロックの初期化
     */
    public void init() {
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                block[i][j] = 0;
            }
        }
    }

    public boolean isFixed(){
        return fixedFlag;
    }

    /**
     * ブロックの描画
     * 
     * @param g 描画オブジェクト
     */
    public void draw(Graphics g) {
        Image blockImage;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                if (block[i][j] != 0) {
                    int pngIdx = block[i][j];
                    blockImage = BlockImages.getBlockImage(pngIdx);
                    // posの位置を基準とする点に注意！
                    g.drawImage(blockImage, (pos.x + j) * TILE_SIZE,
                            (pos.y + i-1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    /**
     * dirの方向にブロックを移動
     * 
     * @param dir 方向
     * @return フィールドに固定されたらtrueを返す
     */
    
    public void move(int dir) {
        if ( isEmptyBlock() ) {
            return;
        }
        switch (dir) {
            case LEFT :
                Point newPos = new Point(pos.x - 1, pos.y);
                if (field.isMovable(newPos, block, ROW_SIZE, COL_SIZE)){
                    pos = newPos;
                }
                break;
            case RIGHT :
                newPos = new Point(pos.x + 1, pos.y);
                if (field.isMovable(newPos, block, ROW_SIZE, COL_SIZE)){
                    pos = newPos;
                }
                break;
            case DOWN :
                newPos = new Point(pos.x, pos.y + 1);
                if (field.isMovable(newPos, block, ROW_SIZE, COL_SIZE)){
                    pos = newPos;
                } else {
                    fixedFlag = true;
                    field.fixBlock(this, ROW_SIZE, COL_SIZE);
                    init();
                }
                break;
            case FALL :
                newPos = new Point(pos.x, pos.y + 1);
                while (field.isMovable(newPos, block, ROW_SIZE, COL_SIZE)) {
                    pos = newPos;
                    newPos = new Point(pos.x, pos.y + 1);
                }
                fixedFlag = true;
                field.fixBlock(this, ROW_SIZE, COL_SIZE);
                init();
                break;
        }        
    }

    private boolean isEmptyBlock(){
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                if ( block[i][j] != 0 ) {
                    return false;
                }
            }
        }
        return true;
    }
    /*
    /**
     * ブロックを回転させる
     */
    public void turn(int dir) {
        // 回転したブロック
        int[][] turnedBlock = new int[ROW_SIZE][COL_SIZE];
        if ( dir == RIGHT ) { // 右回転
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    turnedBlock[j][ROW_SIZE - 1 - i] = block[i][j];
                }
            }
        } else {  // 左回転
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    turnedBlock[ROW_SIZE - 1 - j][i] = block[i][j];
                }
            }            
        }

        // 回転可能か調べる
        if ( field.isMovable(pos, turnedBlock, ROW_SIZE, COL_SIZE)) {
            block = turnedBlock; // 回転
            // 縦横変換
            if ( blockFolm == 1 ) {
                blockFolm = 0;
            } else {
                blockFolm = 1;
            }
        } else { // 回転できない時
            if ( blockFolm == 0 ) { // ブロックが縦の時
                Point newPos = new Point(pos.x - 1, pos.y);
                // 左に1マススライドして回転
                if ( field.isMovable(newPos, block, ROW_SIZE, COL_SIZE) ) {
                    pos = newPos;
                    block = turnedBlock;
                    blockFolm = 1;
                    return;
                }
                // 右に1マススライドして回転
                newPos = new Point(pos.x + 1, pos.y);
                if ( field.isMovable(newPos, block, ROW_SIZE, COL_SIZE) ) {
                    pos = newPos;
                    block = turnedBlock;
                    blockFolm = 1;
                    return;
                }

                // 右にも左にもスライドできない時
                newPos = new Point(pos.x, pos.y+1);
                turnedBlock = new int[ROW_SIZE][COL_SIZE];
                for ( int i = 0; i < ROW_SIZE; i++ ) {
                    for ( int j = 0; j < COL_SIZE; j++ ) {
                        // 上下反転
                        turnedBlock[j][i] = block[COL_SIZE-j-1][i];
                    }
                }
                if (field.isMovable(newPos, block, ROW_SIZE, COL_SIZE)) {
                    block = turnedBlock;
                }
            }
        }
    }

    public void posInit() {
        pos = new Point(2, -1);
    }

    public int[][] getBlock(){
        return this.block;
    }

    public Point getPos(){
        return this.pos;
    }
}
