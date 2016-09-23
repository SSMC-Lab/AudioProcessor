package fruitbasket.com.audioprocessor.record;

/**
 * 用于录音的任务
 */
final public class RecordTask implements Runnable {
	
	AudioRecordWrapper audioRecordWrapper;
	
	@Override
	public void run() {
		audioRecordWrapper =new AudioRecordWrapper();
		audioRecordWrapper.startRecording();
	}

	public void stopRecording(){
		if(audioRecordWrapper !=null){
			audioRecordWrapper.stopRecoding();//当audioRecordWrapper停止播放后就会自动释放资源
			audioRecordWrapper =null;
		}
	}
}
