package fruitbasket.com.audioprocessor.play;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 播放普通音频的任务
 */
public class CommonPlayTask implements Runnable {

    private String audioPath;//音频文件的路径，例如“/AudioProcess./test.mp3"
    private MediaPlayer mediaPlayer;
    private AudioOutConfig audioOutConfig;

    public CommonPlayTask(String audioPath){
        this.audioPath = audioPath;
    }

    public CommonPlayTask(String audioPath, AudioOutConfig audioOutConfig){
        this.audioPath = audioPath;
        this.audioOutConfig=audioOutConfig;
    }

    @Override
    public void run(){
        mediaPlayer=new MediaPlayerWrapper(audioOutConfig).getMediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopPlaying(){
        if(mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

}
