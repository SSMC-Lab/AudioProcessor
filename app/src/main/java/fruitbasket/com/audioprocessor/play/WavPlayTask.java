package fruitbasket.com.audioprocessor.play;

import android.media.MediaPlayer;

import java.io.IOException;

public class WavPlayTask implements Runnable{
    private static final String TAG="play.WavPlayTask";

    private MediaPlayer mediaPlayer;
    private String audioPath;	//the full path and the name of the audio file
    private AudioOutConfig audioOutConfig;

    public WavPlayTask(String audioPath){
        this(audioPath,null);
    }

    public WavPlayTask(String audioPath,AudioOutConfig audioOutConfig){
        this.audioPath =audioPath;
        this.audioOutConfig=audioOutConfig;
    }

    @Override
    public void run() {
        MediaPlayerFactory mediaPlayerFactory=new MediaPlayerFactory();
        mediaPlayerFactory.setAudioOutConfig(audioOutConfig);
        mediaPlayer=mediaPlayerFactory.createMediaPlayer();
        mediaPlayerFactory=null;

        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void stopPlaying(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void setParameters(String audioPath){
        this.audioPath =audioPath;
    }
}
