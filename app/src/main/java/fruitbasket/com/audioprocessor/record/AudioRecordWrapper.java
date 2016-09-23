package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;

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
		short[] buffer=new short[bufferSize];

		try {
			DataOutputStream output=new DataOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(audioFile)
					)
			);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					sampleRate,
					channelIn,
					encoding,
					bufferSize);
			audioRecord.startRecording();

			isRecording = true;
			while (isRecording) {
				int readResult = audioRecord.read(buffer, 0, bufferSize);
				if(readResult==AudioRecord.ERROR_INVALID_OPERATION){
					Log.e(TAG,"readState==AudioRecord.ERROR_INVALID_OPERATION");
					return false;
				}
				else if(readResult==AudioRecord.ERROR_BAD_VALUE){
					Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
					return false;
				}
				else{
					for(int i=0;i<readResult;i++){
						output.writeShort(buffer[i]);
					}
				}
			}
			//结束以上循环后就停止播放并释放资源
			audioRecord.stop();
			output.close();
			audioRecord.release();

			//制作wav文件
			///这里录得的wav文件存在问题
			///这里先将原始音频保存起来，在改装成wav文件，这不是一个好做法
			/*FileInputStream fis= new FileInputStream(audioFile);
			BufferedInputStream inputStream=new BufferedInputStream(fis);
			BufferedOutputStream outputStream=new BufferedOutputStream(
					new FileOutputStream(audioFullName+".wav")
			);
			byte[] readBuffer=new byte[1024];

			int length=(int)fis.getChannel().size();
			wavHeader.setAdjustFileLength(length-8);
			wavHeader.setAudioDataLength(length-44);
			wavHeader.setBlockAlign(channelIn,encoding);
			wavHeader.setByteRate(channelIn,sampleRate,encoding);
			wavHeader.setChannelCount(channelIn);
			wavHeader.setEncodingBit(encoding);
			wavHeader.setSampleRate(sampleRate);
			wavHeader.setWaveFormatPcm(WavHeader.WAV_FORMAT_PCM);

			outputStream.write(wavHeader.getHeader());
			while (inputStream.read(readBuffer) != -1) {
				outputStream.write(readBuffer);
			}
			inputStream.close();
			outputStream.close();*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void stopRecoding(){
		isRecording = false;
	}
}
