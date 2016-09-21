package fruitbasket.com.audioprocessor.record;

/**
 * 用于录音的任务
 */
public class RecordTask implements Runnable {
	
	AudioRecordWrapper audioRecordWrapper;
	
	@Override
	public void run() {
		audioRecordWrapper =new AudioRecordWrapper();
		audioRecordWrapper.startRecording();
	}

	public void stopRecording(){
		if(audioRecordWrapper !=null){
			audioRecordWrapper.stopRecoding();
			audioRecordWrapper =null;
		}
	}
}
