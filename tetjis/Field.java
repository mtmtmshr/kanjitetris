package tetjis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Image;


public class Field {
    // フィールドのサイズ（単位：マス）
    public static final int COL = 8;
    public static final int ROW = 14;
    // マスのサイズ
    public static final int TILE_SIZE = Tetjis.TILE_SIZE;
    public static final int X = 1;
    public static final int Y = 0;
    // フィールド
    private int[][] field;
    public Field() {
        field = new int[ROW][COL];
        // フィールドを初期化
        init(field);
    }

    public void init(int[][] field) {
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COL; x++) {
                // 壁をつくる
                if (x == 0 || x == COL - 1) {
                    field[y][x] = 1;
                } else if (y == ROW - 1) {
                    field[y][x] = 1;
                } else {
                    field[y][x] = 0;
                }
            }
        }
    }

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] f) {
        field = f;
    }



    public void fixBlock(Block b, int ROW, int COL){
        int[][] block = b.getBlock();
        Point pos = b.getPos();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (block[i][j] != 0) {
                    if (pos.y + i < 0) continue;
                    field[pos.y + i][pos.x + j] = block[i][j];  // フィールドをブロックで埋める
                }
            }
        }
    }

    public void drawWallandBatsu(Graphics g){
        Image blockImage;
        blockImage = BlockImages.getBatsu();
        g.drawImage(blockImage, 3 * TILE_SIZE,
                    0, TILE_SIZE, TILE_SIZE, null);
        blockImage = BlockImages.getBlockImage(1);
        for ( int y=1; y < ROW; y++) {
            g.drawImage(blockImage, 0,
                        (y-1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, null); 
            g.drawImage(blockImage, TILE_SIZE * (COL-1),
                        (y-1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, null); 
        }
        for (int x = 1; x < COL-1; x++) {
            g.drawImage(blockImage, TILE_SIZE * x,
                        (ROW-2) * TILE_SIZE, TILE_SIZE, TILE_SIZE, null); 
        }
        g.setColor(Color.WHITE);
        g.fillRect(TILE_SIZE, 0, 
                   MainPanel.WIDTH-TILE_SIZE*2, MainPanel.HEIGHT-TILE_SIZE);
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(TILE_SIZE, 0, 
                   MainPanel.WIDTH-TILE_SIZE*2, MainPanel.HEIGHT-TILE_SIZE);
        for (int y = 1; y < ROW-1; y++) {
            for (int x = 1; x < COL-1; x++) {
                if (field[y][x] != 0) {
                    int pngIdx = field[y][x];
                    g.drawImage(BlockImages.getBlockImage(pngIdx), x * TILE_SIZE,
                                (y-1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);  
                }
            }
        }
    }

    public boolean isMovable(Point newPos, int[][] block, int ROW, int COL) {
        // block!=のマスすべてについて衝突しているか調べる
        // どれか1マスでも衝突してたら移動できない
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (block[i][j] != 0) { // ROWxCOL内でブロックのあるマスのみ調べる
                    if (newPos.y + i < 0) { // そのマスが画面の上端外のとき
                        // ブロックのあるマスが壁のある0列目以下または
                        // COL-1列目以上に移動しようとしている場合は移動できない
                        if (newPos.x + j <= 0 || newPos.x + j >= COL - 1) {
                            return false;
                        }
                    }
                    else if (field[newPos.y + i][newPos.x + j] != 0) { // フィールド内で
                        // 移動先にすでにブロック（壁含む）がある場合は移動できない
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean isStacked(){
        if ( field[1][3] == 0 ) {
            return false;
        }
        return true;
    }


    // ブロックが固定されたあとの自由落下
    public boolean fallBlock() {
        int x, y, iy;
        // まだ落とせるか
        boolean flag = false;
        for ( x = 1; x < COL-1; x++ ) {
            // 一番下は壁なのでROW-2から。
            for ( y = ROW-2; y >= 0; y-- ) {
                if ( field[y][x] == 0 ) {
                    // 空の場所の読み飛ばし
                    for ( iy = y-1; iy>=0 && field[iy][x] == 0; iy-- );
                    if ( iy < 0 ) { break; }
                    // まだ落とせる！！
                    flag = true; 
                    for ( iy = y; iy >= 0; iy-- ) {
                        // 下に空白がある文字を1マスずつ落とす
                        if ( iy-1 >= 0 ) {
                            field[iy][x] = field[iy-1][x];
                        } else {
                            field[iy][x] = 0;
                        }
                    }
                    break;
                }
            }
        }
        return flag;
    }

    public boolean allDelete() {
        int x;
        for ( x = 1; x < COL-2; x++ ) {
            if ( field[ROW-2][x] != 0 ) {
                return false;
            }
        }
        return true;
    }
}
