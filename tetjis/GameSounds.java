package tetjis;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSounds implements Runnable {
    public static void main(String[] args) {
        // ここで再生メソッドの呼び出し
    }
    private static Clip bgm;
    private static String bgmPath = "tetjis/data/bgm/tetrisb.mid";
    private static Clip[] se = new Clip[7];
    private static String[] sePathes = {"tetjis/data/se/cursor7.wav", "tetjis/data/se/excellent1.wav",
                                        "tetjis/data/se/police-whistle1.wav","tetjis/data/se/puyon1.wav",
                                        "tetjis/data/se/decision9.wav", "tetjis/data/se/kachi42.wav",
                                        "tetjis/data/se/police-whistle2.wav"};
    private Thread gameThread;
    private static HashMap<String, Integer> seMap= new HashMap<String, Integer>() {
        {
            put("start", 2);
            put("finish", 6);
            put("puyon", 3);
            put("kachi", 5);
            put("decision", 4);
            put("cursol", 0);
            put("all", 1);
        }
    };
    public GameSounds() {

    }

    public void setGameThread(Thread thread) {
        this.gameThread = thread;
    }

    public static Clip createClip(URL url) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            AudioFormat af = ais.getFormat();
            DataLine.Info dataLine = new DataLine.Info(Clip.class, af);
            Clip c = (Clip) AudioSystem.getLine(dataLine);
            // 再生準備完了
            c.open(ais);
            return c;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        bgm.loop(Clip.LOOP_CONTINUOUSLY);
        try {
			this.gameThread.join();
		} catch (InterruptedException e) {
			// 例外処理
			e.printStackTrace();
		}
        bgm.stop();
		bgm.flush();
		bgm.setFramePosition(0);
    }

    public static void loadSounds() {
        bgm = createClip(GameSounds.class.getClassLoader().getResource(bgmPath));
        for ( int i = 0; i < sePathes.length; i++ ) {
            se[i] = createClip(GameSounds.class.getClassLoader().getResource(sePathes[i]));
        }
    }

    public static void playSounds(String s){
        Clip clip = se[seMap.get(s)];
        clip.stop();
        clip.setFramePosition(-1);
        clip.start();
    }
}
