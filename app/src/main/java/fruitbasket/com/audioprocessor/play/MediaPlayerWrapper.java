package fruitbasket.com.audioprocessor.play;

import android.media.MediaPlayer;

/**
 * Created by Study on 24/06/2016.
 */
public class MediaPlayerWrapper {
    private static final String TAG=MediaPlayerWrapper.class.toString();

    private AudioOutConfig audioOutConfig;
    private MediaPlayer mediaPlayer;

    public MediaPlayerWrapper(){
        this(null);
    }

    public MediaPlayerWrapper(AudioOutConfig audioOutConfig){
        mediaPlayer=new MediaPlayer();
        setAudioOutConfig(audioOutConfig);
    }

    public MediaPlayerWrapper(int channelOut){
        mediaPlayer=new MediaPlayer();
        setChannelOut(channelOut);
    }


    public void setAudioOutConfig(AudioOutConfig audioOutConfig){
        this.audioOutConfig=audioOutConfig;
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

    public void setChannelOut(int channelOut){
        if(audioOutConfig==null){
            this.audioOutConfig=new AudioOutConfig(channelOut);
        }
        else{
            audioOutConfig.setChannelOut(channelOut);
        }

        if(mediaPlayer!=null){
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

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }
}
