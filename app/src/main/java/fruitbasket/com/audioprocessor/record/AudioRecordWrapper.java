package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.DataIOHelper;

/**
 * 录制声音
 */
public class AudioRecordWrapper {
	private static final String TAG=AudioRecordWrapper.class.toString();

	private String audioFullName;
	private WavHeader wavHeader;
	private boolean isRecording=false;
	
	public AudioRecordWrapper(){
		wavHeader=new WavHeader();
	}
	
	/**
	 * 开始录制音频
	 * @return ture 录音完成，false 录音失败
	 */
	public boolean startRecording(){
		return startRecording(AppCondition.DEFAULE_SIMPLE_RATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
	}

	/**
	 *
	 * @param sampleRate 声音的采样频率
	 * @param channelIn 声道
	 * @param encoding specific the quantity of bit of each voice frame
     * @return ture 录音完成，false 录音失败
     */
	public boolean startRecording(final int sampleRate,int channelIn,int encoding){
		audioFullName = DataIOHelper.getRecordedFileName("pcm");
		File audioFile= new File(audioFullName);

		int bufferSize = AudioRecord.getMinBufferSize(
				sampleRate,
				channelIn,
				encoding);
		if(bufferSize==AudioRecord.ERROR_BAD_VALUE){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
			return false;
		}
		else if(bufferSize==AudioRecord.ERROR){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
			return false;
		}
		byte[] buffer = new byte[bufferSize];

		try {
			FileOutputStream output = new FileOutputStream(audioFile);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					sampleRate,
					channelIn,
					encoding,
					bufferSize);
			audioRecord.startRecording();

			isRecording = true;
			while (isRecording) {
				int readState = audioRecord.read(buffer, 0, bufferSize);
				if(readState==AudioRecord.ERROR_INVALID_OPERATION){
					Log.e(TAG,"readState==AudioRecord.ERROR_INVALID_OPERATION");
					return false;
				}
				else if(readState==AudioRecord.ERROR_BAD_VALUE){
					Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
					return false;
				}
				else{
					output.write(buffer);
				}
			}
			output.close();
			audioRecord.stop();
			audioRecord.release();

			FileInputStream inputStream=new FileInputStream(audioFile);///这里先将原始音频保存起来，在改装成wav文件，这不是一个号做法
			FileOutputStream outputStream=new FileOutputStream(audioFullName+".wav");

			int length=(int)inputStream.getChannel().size();
			wavHeader.setAdjustFileLength(length-8);
			wavHeader.setAudioDataLength(length-44);
			wavHeader.setBlockAlign(channelIn,encoding);
			wavHeader.setByteRate(channelIn,sampleRate,encoding);
			wavHeader.setChannelCount(channelIn);
			wavHeader.setEncodingBit(encoding);
			wavHeader.setSampleRate(sampleRate);
			wavHeader.setWaveFormatPcm(WavHeader.WAV_FORMAT_PCM);

			outputStream.write(wavHeader.getHeader());
			while (inputStream.read(buffer) != -1) {
				outputStream.write(buffer);
			}
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void stopRecoding(){
		isRecording = false;
	}
}
