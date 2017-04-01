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

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.waveProducer.WaveProducer;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * 用于播放原始音频（pcm）
 * ///会导致一种bug：当调用releaseResource()后，startPlaying()中的现成仍在运行，导致异常
 */
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

    public void startPlaying(String audioPath){
        startPlaying(audioPath, AppCondition.DEFAULE_SIMPLE_RATE);
    }

	/**
	 *	播放一个pcm音频文件，这个音频文件使用16位的编码
	 * @param audioPath the full path and the name of the audio file
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

			adjustVolume();
			audioTrack.play();
			audioTrack.write(audio, 0, audioLength);
			releaseResource();
		} catch (Throwable t) {
			Log.e(TAG,"Playback Failed");
		}
	}

    public void startPlaying(final WaveType waveType,final int waveRate){
        startPlaying(waveType,waveRate, AppCondition.DEFAULE_SIMPLE_RATE);
    }

	/**
	 * 播放一段声波
	 * @param waveType 声波的类型
	 * @param waveRate 声波的频率
	 * @param sampleRate 设备实际的发声频率
     */
    public void startPlaying(final WaveType waveType, final int waveRate, final int sampleRate){
        final int bufferSize = 2*AudioTrack.getMinBufferSize(///
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
		Log.i(TAG,"bufferSize=="+bufferSize);

        audioTrack=new AudioTrack(
                AudioManager.STREAM_MUSIC,
				sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

		adjustVolume();
        audioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {

				/*double sampleCountInWave=sampleRate /(double)waveRate;//每一个Sin波中，包含的样本点数量
				Log.i(TAG,"sampleCountInWave=="+sampleCountInWave);
				int adjustBufferSize=bufferSize;
				Log.i(TAG,"adjustBufferSize/sampleCountInWave=="+adjustBufferSize/sampleCountInWave);
				Log.i(TAG,"(adjustBufferSize-1)/sampleCountInWave=="+(adjustBufferSize-1)/sampleCountInWave);
				Log.i(TAG,"Math.floor(adjustBufferSize/sampleCountInWave)=="+Math.floor(adjustBufferSize/sampleCountInWave));
				Log.i(TAG,"Math.floor((adjustBufferSize-1)/sampleCountInWave)=="+Math.floor((adjustBufferSize-1)/sampleCountInWave));
				while(
						Math.floor(adjustBufferSize/sampleCountInWave)==
								Math.floor((adjustBufferSize-1)/sampleCountInWave)
						){
					adjustBufferSize--;
					Log.i(TAG,"adjustBufferSize--");
				}
				Log.i(TAG,"adjustBufferSize/sampleCountInWave=="+adjustBufferSize/sampleCountInWave);
				Log.i(TAG,"(adjustBufferSize-1)/sampleCountInWave=="+(adjustBufferSize-1)/sampleCountInWave);
				Log.i(TAG,"Math.floor(adjustBufferSize/sampleCountInWave)=="+Math.floor(adjustBufferSize/sampleCountInWave));
				Log.i(TAG,"Math.floor((adjustBufferSize-1)/sampleCountInWave)=="+Math.floor((adjustBufferSize-1)/sampleCountInWave));
				Log.i(TAG,"adjustBufferSize=="+adjustBufferSize);
                short[] wave=WaveProducer.getWave(waveType,waveRate,sampleRate,adjustBufferSize);*/


				double sampleCountInWave=sampleRate /(double)waveRate;//每一个Sin波中，包含的样本点数量
				short[] wave=new short[bufferSize];
				int index=0;

                while(audioTrack!=null&&audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){

					///因为解决不了周期性噪声的问题，因此这里采用边计算边播放的傻办法
					for(int i=0;i<wave.length;++i,++index){
						wave[i]=(short) (Short.MAX_VALUE*
								Math.sin(2.0 * Math.PI * index / sampleCountInWave)
						);
					}

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

	/**
	 * 执行本方法，以释放资源
	 */
	public void releaseResource(){
		if(audioTrack !=null){
			audioTrack.release();
			audioTrack =null;
		}
	}

	private void adjustVolume(){
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
	}
}

