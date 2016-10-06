package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;

import fruitbasket.com.audioprocessor.AppCondition;

/**
 * 用于录音的任务
 */
final public class RecordTask implements Runnable {
	
	AudioRecordWrapper audioRecordWrapper;

	private int channelIn;//用于指定音频来源的数量

	public RecordTask(){
		setChannelIn(AudioFormat.CHANNEL_IN_MONO);
	}

	public void setChannelIn(int channelIn){
		if(channelIn==AudioFormat.CHANNEL_IN_STEREO){
			this.channelIn=AudioFormat.CHANNEL_IN_STEREO;
		}
		else{
			/*这里直接限定channelIn的类型，即只能是AudioFormat.CHANNEL_IN_STEREO或
			AudioFormat.CHANNEL_IN_MONO。这样可能会去引起不能使用其他channelIn类型的问题
			*/
			this.channelIn=AudioFormat.CHANNEL_IN_MONO;
		}
	}

	public int getChannelIn(){
		return this.channelIn;
	}
	
	@Override
	public void run() {
		audioRecordWrapper =new AudioRecordWrapper();
		audioRecordWrapper.startRecording(AppCondition.DEFAULE_SIMPLE_RATE,channelIn,AudioFormat.ENCODING_PCM_16BIT);
	}

	public void stopRecording(){
		if(audioRecordWrapper !=null){
			audioRecordWrapper.stopRecoding();//当audioRecordWrapper停止播放后就会自动释放资源
			audioRecordWrapper =null;
		}
	}
}
