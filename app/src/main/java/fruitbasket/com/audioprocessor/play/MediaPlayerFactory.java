package fruitbasket.com.audioprocessor.play;

import android.media.MediaPlayer;

/**
 * 用于生产特定的MediaPlayer
 * Created by Study on 24/06/2016.
 */
public class MediaPlayerFactory {
    private static final String TAG=MediaPlayerFactory.class.toString();

    private AudioOutConfig audioOutConfig;
    private MediaPlayer mediaPlayer;

    public MediaPlayerFactory(){
        this(null);
    }

    public MediaPlayerFactory(AudioOutConfig audioOutConfig){
        mediaPlayer=new MediaPlayer();
        setAudioOutConfig(audioOutConfig);
    }

    public MediaPlayerFactory(int channelOut){
        mediaPlayer=new MediaPlayer();
        setChannelOut(channelOut);
    }

    public void setAudioOutConfig(AudioOutConfig audioOutConfig){
        this.audioOutConfig=audioOutConfig;
        adjustMediaPlayer();
    }

    public void setChannelOut(int channelOut){
        if(audioOutConfig==null){
            this.audioOutConfig=new AudioOutConfig(channelOut);
        }
        else{
            audioOutConfig.setChannelOut(channelOut);
        }
        adjustMediaPlayer();
    }

    private void adjustMediaPlayer(){
        if(audioOutConfig!=null
                &&mediaPlayer!=null){
            switch(audioOutConfig.getChannelOut()){
                case AudioOutConfig.CHANNEL_OUT_LEFT:
                    mediaPlayer.setVolume(0.5f,0.0f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_RIGHT:
                    mediaPlayer.setVolume(0.0f,0.5f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_BOTH:
                    mediaPlayer.setVolume(0.5f,0.5f);
                    break;
            }
        }
    }

    public MediaPlayer createMediaPlayer(){
        return mediaPlayer;
    }
}
