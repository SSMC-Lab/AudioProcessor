package fruitbasket.com.audioprocessor.play;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioTrackWrapper {
	private static final String TAG=AudioTrackWrapper.class.toString();

    private AudioOutConfig audioOutConfig;
	private AudioTrack audioTrack;
	private boolean isContinuePlaying =false;

	public AudioTrackWrapper(){}

	public AudioTrackWrapper(AudioOutConfig audioOutConfig){
		setAudioOutConfig(audioOutConfig);
	}

	public AudioTrackWrapper(int channelOut){
		setChannelOut(channelOut);
	}

	public void setAudioOutConfig(AudioOutConfig audioOutConfig){
		this.audioOutConfig =audioOutConfig;
	}

	public void setChannelOut(int channelOut){
		if(audioOutConfig==null){
			this.audioOutConfig=new AudioOutConfig(channelOut);
		}
		else{
			audioOutConfig.setChannelOut(channelOut);
		}
	}
	
	/**
	 * plays a existing music , which must be a PCM music data
	 * @param audioPath : the full path and the name of the music data
	 * @param sampleRate : the sample rate of the music data
	 */
	public void startPlaying(String audioPath,int sampleRate){

		File file = new File(audioPath);
		int audioLength = (int)(file.length()/2);
		short[] music = new short[audioLength];

		try {
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);
			int i = 0;
			while (dis.available() > 0) {
				music[i] = dis.readShort();
				i++;
			}
			dis.close();
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRate,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					audioLength,
					AudioTrack.MODE_STREAM);

			if(audioOutConfig!=null){
				switch(audioOutConfig.getChannelOut()){
					case AudioOutConfig.CHANNEL_OUT_LEFT:
						audioTrack.setStereoVolume(0.5f,0.0f);
						break;
					case AudioOutConfig.CHANNEL_OUT_RIGHT:
						audioTrack.setStereoVolume(0.0f,0.5f);
						break;
					case AudioOutConfig.CHANNEL_OUT_BOTH:
						audioTrack.setStereoVolume(0.5f,0.5f);
						break;
				}
			}

			audioTrack.play();
			audioTrack.write(music, 0, audioLength);
			releaseResource();
		} catch (Throwable t) {
			Log.e(TAG,"Playback Failed");
		}
	}

	public void stopPlaying(){
		isContinuePlaying =false;
	}
	
	private void releaseResource(){
		if(audioTrack !=null){
			audioTrack.stop();
			audioTrack.release();
			audioTrack =null;
		}
	}
}

