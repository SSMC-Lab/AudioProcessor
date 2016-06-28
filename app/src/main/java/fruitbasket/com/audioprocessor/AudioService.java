package fruitbasket.com.audioprocessor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.play.CommonPlayTask;
import fruitbasket.com.audioprocessor.play.PCMPlayTask;
import fruitbasket.com.audioprocessor.record.RecordTask;

/**
 * 执行录制音频和播放音频操作的服务
 */
public class AudioService extends Service {
	private static final String TAG="RecordService";
	
	private Thread recordThread;
	private RecordTask recordTask;
	
	private Thread pcmPlayThread;
	private PCMPlayTask PCMPlayTask;

	private Thread soundFilePlayThread;
	private CommonPlayTask commonPlayTask;

	private AudioOutConfig audioOutConfig;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new RecordServiceBinder();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"onCreate()");
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG,"onDestroy()");

		//在服务结束时，必须结束所有任务
		stopPlaying();
		stopPlayingAudioFile();
		stopRecording();
		super.onDestroy();
	}

	/**
	 * 开始录制音频
	 */
	public void startRecording(){
		recordTask=new RecordTask();
		recordThread=new Thread(recordTask);
		recordThread.start();
	}

	/**
	 * 停止录制音频
	 */
	public void stopRecording(){
		if(recordTask!=null){
			recordTask.stopRecording();
		}
	}

	/**
	 * 开始播放录制的音频
	 * @param recordingFilePath 指定一个录制的音频，音频的格式必须为pcm
	 * @param sampleRate
     */
	public void startPlaying(String recordingFilePath,int sampleRate){
		PCMPlayTask =new PCMPlayTask(recordingFilePath,sampleRate,audioOutConfig);
		pcmPlayThread =new Thread(PCMPlayTask);
		pcmPlayThread.start();
	}
	
	public void stopPlaying(){
		if(PCMPlayTask !=null){
			PCMPlayTask.stopPlaying();
			PCMPlayTask =null;
		}
	}

	/**
	 * 开始播放一个音频文件
	 */
	public void startPlayingAudioFile(){
		commonPlayTask =new CommonPlayTask(Condition.SOUND_FILE_PATH,audioOutConfig);
		soundFilePlayThread=new Thread(commonPlayTask);
		soundFilePlayThread.start();
	}

	/**
	 * @param audioPath 指定音频文件的路径
     */
	public void startPlayingAudioFile(String audioPath){
		commonPlayTask =new CommonPlayTask(audioPath,audioOutConfig);
		soundFilePlayThread=new Thread(commonPlayTask);
		soundFilePlayThread.start();
	}

	/**
	 * 停止播放一个音频文件
	 */
	public void stopPlayingAudioFile(){
		if(commonPlayTask !=null){
			commonPlayTask.stopPlaying();
			commonPlayTask =null;
		}
	}

	public void setAudioOutConfig(AudioOutConfig audioOutConfig){
		this.audioOutConfig=audioOutConfig;
	}

	public void setChannelOut(int channelOut){
		if(audioOutConfig==null){
			this.audioOutConfig=new AudioOutConfig(channelOut);
		}
		else{
			audioOutConfig.setChannelOut(channelOut);
		}
	}




	public class RecordServiceBinder extends Binder{
		public AudioService getService(){
			return AudioService.this;
		}
	}
}
