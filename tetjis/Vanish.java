package tetjis;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.Image;


class VanishData{
    public Point pos;
    public int len;
    public int dir;
    public VanishData(Point pos, int len, int dir) {
        this.pos = pos;
        this.len = len;
        this.dir = dir;
    }
}


public class Vanish implements Runnable{
    private static HashMap<Integer, String> map = new HashMap<>();
    private static ArrayList<String> dictionary = new ArrayList<String>();
    ArrayList<Integer> wordCandidate = new ArrayList<Integer>();
    ArrayList<VanishData> vanishList = new ArrayList<VanishData>();
    private VanishData vanishData;
    private static final int X = 1;
    private static final int Y = 0;
    private int[][] weight;
    private int[][] field;
    private Field f;
    private static final int TILE_SIZE = Tetjis.TILE_SIZE;
    private JPanel panel;
    private ScorePanel scorePanel;

    public Vanish(Field field, JPanel frame, ScorePanel scorePanel){
        f = field;
        this.scorePanel = scorePanel;
        this.field = f.getField();
        this.panel = frame;
    }


    static{
        makeDic();
        makeCharList();
    }

    public void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        Graphics g = panel.getGraphics();
        // 連鎖数
        int chain = 1;
        while ( vanishWeight() ) {
            // 同時消しの個数
            int cnt = 1;
            // 同時消しの回数だけループ
            for ( int j = 0; j < vanishList.size(); j++ ) {
                vanishData = vanishList.get(j);
                drawVanishArea();
                threadSleep(1500);
                int len = vanish(vanishData); // 消去した文字列の長さ
                f.draw(g);
                GameSounds.playSounds("puyon");
                scorePanel.upScore(100 * len * (chain*chain) * cnt);
                scorePanel.set();
                cnt += 1;
            }
            chain += 1;
            while( f.fallBlock() ) {
                threadSleep(100);
                f.draw(g);
            }
            f.draw(g);
            field = f.getField();
            threadSleep(300);
        }
    }

    public void drawVanishArea(){
        Graphics g = panel.getGraphics();
        g.setColor(Color.MAGENTA);
        
        if ( vanishData.dir == X ) { // 横方向に文字が消えるとき
            for ( int i = 0; i < vanishData.len; i++ ) {
                int pngIdx = field[vanishData.pos.y][vanishData.pos.x+i];
                g.drawImage(BlockImages.getColorBlockImage(pngIdx), (vanishData.pos.x + i)* TILE_SIZE, (vanishData.pos.y-1) * TILE_SIZE,
                            TILE_SIZE, TILE_SIZE, null);
            }
        } else {
            for ( int i = 0; i < vanishData.len; i++ ) { //縦方向に文字が消えるとき
                int pngIdx = field[vanishData.pos.y+i][vanishData.pos.x];
                g.drawImage(BlockImages.getColorBlockImage(pngIdx), vanishData.pos.x* TILE_SIZE, (vanishData.pos.y+i-1) * TILE_SIZE, TILE_SIZE,
                            TILE_SIZE, null);
            }
        }        
    }

    public void init(int[][] field) {
        for (int y = 0; y < Field.ROW; y++) {
            for (int x = 0; x < Field.COL; x++) {
                // 壁をつくる
                if (x == 0 || x == Field.COL - 1) {
                    field[y][x] = 1;
                } else if (y == Field.ROW - 1) {
                    field[y][x] = 1;
                } else {
                    field[y][x] = 0;
                }
            }
        }
    }

    public int vanish(VanishData vanishData) {
    int num;
        if ( vanishData.dir == X ) {
            for ( int i = 0; i < vanishData.len; i++ ) {
                weight[vanishData.pos.y][vanishData.pos.x+i] -= 1;
                if ( weight[vanishData.pos.y][vanishData.pos.x+i] <= 0 ) {
                    field[vanishData.pos.y][vanishData.pos.x+i] = 0;
                }
            }
            num = vanishData.len;
            f.setField(field);
            return num;
        } else if (vanishData.dir == Y) {
            for ( int i = 0; i < vanishData.len; i++ ) {
                weight[vanishData.pos.y+i][vanishData.pos.x] -= 1;
                if ( weight[vanishData.pos.y+i][vanishData.pos.x] <= 0 ) {
                    field[vanishData.pos.y+i][vanishData.pos.x] = 0;
                }
            }
            num = vanishData.len;
            f.setField(field);
            return num;
        }
        return 0;
    }

    public boolean addVanishList(int x, int y, int dir) {
        // wordCandidateを連結した文字列が削除できる(辞書に含まれている)場合
        if ( Vanish.canVanish(wordCandidate) ) {
            Point pos = new Point(x, y);
            // 削除する単語のリストに追加
            vanishList.add(new VanishData(pos, wordCandidate.size(), dir));
            return true;
        }
        return false;         
    }

    /* 
    同時消しのための重み計算
        [巨, 大]
        [　, 根]
        ↓↓↓
        [1, 2]
        [0, 1]
    ("巨大"と"大根"で消える)
    */
    public boolean vanishWeight() {
        int x, y, iy, ix, i;
        boolean flag = false;
        // 削除データの初期化
        vanishList.clear();
        weight = new int[Field.ROW][Field.COL];
        // 重みの初期化
        init(weight);
        for ( x = 1; x < Field.COL-1; x++ ) {
            // 一番上(y=0)は消さない！
            for ( y = 1; y < Field.ROW-1; y++ ) {
                if ( field[y][x] != 0 ) {
                    // 単語候補の初期化
                    wordCandidate.clear();
                    // field[y][x]の文字(1文字目)をwordCandidateに追加
                    wordCandidate.add(field[y][x]);
                    //  横方向の検査
                    for ( ix = x+1; ix < Field.COL-1; ix++ ) {
                        //  field[y][x]の位置より右の文字を1文字ずつ追加
                        wordCandidate.add(field[y][ix]);
                        //  wordCandidateを連結した文字が削除リストに追加された場合
                        if ( addVanishList(x, y, X) ) {
                            flag = true;
                            // 消去する単語の位置の重みをインクリメント
                            for ( i = 0; i < wordCandidate.size(); i++ ) {
                                weight[y][x+i] += 1;
                            }
                        }              
                    }
                    // 単語候補の初期化
                    wordCandidate.clear();
                    // field[y][x]の文字(1文字目)をwordCandidateに追加
                    wordCandidate.add(field[y][x]);
                    // 縦方向の検査
                    for ( iy = y+1; iy < Field.ROW - 1; iy++ ) {
                        //  field[y][x]の位置より下の文字を1文字ずつ追加
                        wordCandidate.add(field[iy][x]);
                        //  wordCandidateを連結した文字が削除リストに追加された場合
                        if ( addVanishList(x, y, Y) ) {
                            flag = true;
                            for ( i = 0; i < wordCandidate.size(); i++ ) {
                                weight[y+i][x] += 1;
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }

    public static boolean canVanish(ArrayList<Integer> wordCandidate) {
        String word = "";
        for ( int i = 0; i < wordCandidate.size(); i++ ) {
            word += map.get(wordCandidate.get(i));
        }
        return dictionary.contains(word);
    }

    public static void makeCharList() {
        String filename = "tetjis/data/character.txt";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Vanish.class.getClassLoader().getResourceAsStream(filename), "UTF-8"))){
            String line;
            while((line = in.readLine()) != null) {
                String[] data =line.split("\\s+");
                try {
                    int num = Integer.parseInt(data[1]);
                    map.put(num, data[0]);
                } catch ( NumberFormatException e ) {
                    System.exit(0);
                }
            }
        } catch (FileNotFoundException e){ 
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e){ 
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void makeDic() {
        String filename = "tetjis/data/dic.txt";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Vanish.class.getClassLoader().getResourceAsStream(filename), "UTF-8"))){
            String line;
            while((line = in.readLine()) != null) {
                dictionary.add(line);
            }
        } catch (FileNotFoundException e){ 
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e){ 
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
