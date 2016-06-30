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

import fruitbasket.com.audioprocessor.Condition;
import fruitbasket.com.audioprocessor.waveProducer.WaveProducer;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

public class AudioTrackWrapper {
	private static final String TAG=AudioTrackWrapper.class.toString();

    private AudioOutConfig audioOutConfig;
	private AudioTrack audioTrack;

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
     * 播放一个pcm音频文件，这个音频文件使用16位的编码
     * @param audioPath the full path and the name of the audio file
     */
    public void startPlaying(String audioPath){
        startPlaying(audioPath, Condition.SIMPLE_RATE_CD);
    }

	/**
	 *
	 * @param audioPath
	 * @param sampleRate 设备实际的发声频率
	 */
	public void startPlaying(String audioPath,int sampleRate){

		//读取音频文件。
        // 这里将整份音频文件读进内存后，再播放
		File file = new File(audioPath);
		int audioLength = (int)(file.length()/2);
		short[] audio = new short[audioLength];
		try {
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);
			int i = 0;
			while (dis.available() > 0) {
				audio[i] = dis.readShort();
				i++;
			}
			dis.close();

			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
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
			audioTrack.write(audio, 0, audioLength);
			releaseResource();
		} catch (Throwable t) {
			Log.e(TAG,"Playback Failed");
		}
	}

    /**
     *播放声波
     * @param wave
     */
    public void startPlaying(short[] wave){
        startPlaying(wave,Condition.SIMPLE_RATE_CD);
    }

    /**
     *这个函数播放声音会出问题///
     * @param wave
     * @param sampleRate 设备播放声音的频率
     */
    public void startPlaying(short[] wave,int sampleRate){
        Log.i(TAG,"startPlaying(wave,sampleRate)");

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                wave.length*2,
                AudioTrack.MODE_STATIC);

        if(audioOutConfig!=null) {
            switch (audioOutConfig.getChannelOut()) {
                case AudioOutConfig.CHANNEL_OUT_LEFT:
                    audioTrack.setStereoVolume(0.5f, 0.0f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_RIGHT:
                    audioTrack.setStereoVolume(0.0f, 0.5f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_BOTH:
                    audioTrack.setStereoVolume(0.5f, 0.5f);
                    break;
            }
        }

        audioTrack.write(wave,0,wave.length);
        audioTrack.setLoopPoints(0,wave.length,-1);
        audioTrack.play();
    }

    public void startPlaying(WaveType waveType,int waveRate){
        this.startPlaying(waveType,waveRate,Condition.SIMPLE_RATE_CD);
    }

    public void startPlaying(final WaveType waveType, final int waveRate, final int sampleRate){
        final int bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack=new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        if(audioOutConfig!=null) {
            switch (audioOutConfig.getChannelOut()) {
                case AudioOutConfig.CHANNEL_OUT_LEFT:
                    audioTrack.setStereoVolume(0.5f, 0.0f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_RIGHT:
                    audioTrack.setStereoVolume(0.0f, 0.5f);
                    break;
                case AudioOutConfig.CHANNEL_OUT_BOTH:
                    audioTrack.setStereoVolume(0.5f, 0.5f);
                    break;
            }
        }
        audioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {
                short[] wave=WaveProducer.getWave(waveType,waveRate,sampleRate,bufferSize);

                while(audioTrack!=null&&audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
                    audioTrack.write(wave,0,wave.length);
                }
            }
        }).start();
    }

	public void stopPlaying(){
		if(audioTrack!=null){
			audioTrack.stop();
		}
	}
	
	public void releaseResource(){
		if(audioTrack !=null){
			audioTrack.release();
			audioTrack =null;
		}
	}
}

