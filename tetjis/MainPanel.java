package tetjis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import java.awt.*;

public class MainPanel extends JPanel implements KeyListener, Runnable {
    private Field field;
    private Block block, nextBlock, afterNextBlock, holdBlock;
    public static int WIDTH;
    public static int HEIGHT;
    private Vanish vanish;
    private int difficulty;
    private boolean holdFlag = true;
    private NextBlockPanel nextBlockPanel;
    private HoldBlockPanel holdBlockPanel;
    private ScorePanel scorePanel;
    private Tetjis tetjis;
    public MainPanel(int WIDTH, int HEIGHT, int difficulty, ScorePanel scorePanel,
                     NextBlockPanel nextBlockPanel, HoldBlockPanel holdBlockPanel, Tetjis tetjis) {
        // パネルの推奨サイズを設定、pack()するときに必要
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // パネルがキー入力を受け付けるようにする
        setFocusable(true);
        MainPanel.WIDTH = WIDTH;
        MainPanel.HEIGHT = HEIGHT;
        this.nextBlockPanel = nextBlockPanel;
        this.holdBlockPanel = holdBlockPanel;
        this.scorePanel = scorePanel;
        this.difficulty = difficulty;

        Vanish.loadData();
        Block.setDifficulty(difficulty);
        field = new Field();
        block = new Block(field);
        this.tetjis = tetjis;
    }

    public void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        scorePanel.setScore(0);
        field = new Field();
        block = new Block(field);
        nextBlock = new Block(field);
        afterNextBlock = new Block(field);
        vanish = new Vanish(field, this, scorePanel);
        GameSounds.playSounds("start");
        Graphics g = this.getGraphics();
        addKeyListener(this);
        while (true) {
            // 自由落下
            nextBlockPanel.setBlock(nextBlock,afterNextBlock);
            while ( ! block.isFixed() ) {
                field.draw(g);
                block.draw(g);
                block.move(Block.DOWN);
                threadSleep(500);
            }

            // ブロックが固定された後に落ちるやつ(ぷ◯◯よ的な。)
            while ( field.fallBlock() ) {
                field.draw(g);
                block.draw(g);
                threadSleep(100);
            }
            GameSounds.playSounds("kachi");
            vanish = new Vanish(field, this, scorePanel);
            Thread vanishThread = new Thread(vanish);
            vanishThread.start();

            // 消去が終わるまでキー入力を受けつけない
            removeKeyListener(this);
            // 消去完了まで待機
            try {
				vanishThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            addKeyListener(this);

            if (field.allDelete() == true) {
                scorePanel.upScore(500);
                scorePanel.set();
                GameSounds.playSounds("all");
            }

            if ( field.isStacked() ) {
                break;   // ゲームオーバー
            }
            block = nextBlock;
            nextBlock = afterNextBlock;
            afterNextBlock = new Block(field);
            holdFlag = true;
        }
        GameSounds.playSounds("finish");
        tetjis.finishMenu(difficulty);
    }

    public void paintComponent(Graphics g) {
        // 壁などは変わらないので先に描いておく
        field.drawWallandBatsu(g);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        Graphics g = getGraphics();
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            block.move(Block.FALL);
        } else if (key == KeyEvent.VK_DOWN) {
            block.move(Block.DOWN);
        } else if (key == KeyEvent.VK_RIGHT) {
            block.move(Block.RIGHT);
        } else if (key == KeyEvent.VK_LEFT) {
            block.move(Block.LEFT);
        } else if (key == KeyEvent.VK_S) {
            block.turn(Block.RIGHT);
        } else if (key == KeyEvent.VK_A) {
            block.turn(Block.LEFT);
        } else if (key == KeyEvent.VK_W) {
            // ホールドが可能ならば
            if ( holdFlag ) {
                // ホールド
                blockHold();
            }
        }
        field.draw(g);
        block.draw(g);
    }

    public void keyReleased(KeyEvent e) {
    }

    private void blockHold(){
        if ( holdBlock == null ) {
            // ホールドブロック更新
            holdBlock = block;
            // ブロックの更新
            block = nextBlock;
            nextBlock = afterNextBlock;
            afterNextBlock = new Block(field);
            nextBlockPanel.setBlock(nextBlock, afterNextBlock);
        } else {
            Block tmpBlock = holdBlock;
            holdBlock = block;
            // ホールドからブロックを戻して初期位置に。
            block = tmpBlock;
            block.posInit();
            // 一度ホールドから戻したブロックはホールドできない
            holdFlag = false;
        }
        // ホールドブロックをholdBlockPanelにセット
        holdBlockPanel.set(holdBlock);
    }

}
