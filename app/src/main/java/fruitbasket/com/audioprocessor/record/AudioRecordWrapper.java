package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import fruitbasket.com.audioprocessor.Condition;
import fruitbasket.com.audioprocessor.DataIOHelper;

/**
 * 录制声音
 */
public class AudioRecordWrapper {
	private static final String TAG=AudioRecordWrapper.class.toString();
	private static final int DEFAULT_BUFFER_INCREASE_FACTOR=3;
	
	private AudioRecord recorder;
	private String audioFileName;
	private WavHeader wavHeader;
	
	public AudioRecordWrapper(){
		wavHeader=new WavHeader();
	}
	
	/**
	 * 开始录制音频
	 * @return ture 录音完成，false 录音失败
	 */
	public boolean startRecording(){
		return startRecording(Condition.SIMPLE_RATE_CD,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
	}
	
	/**
	 * 
	 * @param sampleRate : the number of  samples per second
	 * @param encoding: specific the quantity of bit of each voice frame
	 * @return ture 录音完成，false 录音失败
	 */
	public boolean startRecording(final int sampleRate,int channelIn,int encoding){
		boolean state = false;
		int bufferSize=AudioRecord.getMinBufferSize(sampleRate,channelIn,encoding);
		try {
			state=doRecording(sampleRate,encoding,bufferSize,bufferSize,DEFAULT_BUFFER_INCREASE_FACTOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * 
	 * @param sampleRate 采样频率
	 * @param encoding 编码格式
	 *                    See {@link AudioFormat#ENCODING_PCM_8BIT}, {@link AudioFormat#ENCODING_PCM_16BIT},
	 * @param recordingBufferSize the total size (in bytes) of the buffer where audio data is written to during the recording
	 * @param readBufferSize 读取数据的缓冲区的大小（字节数）
	 * @param bufferIncreaseFactor 用于增加缓冲区recordingBufferSize的因子
	 * @return ture 录音完成，false 录音失败
	 * @throws IOException 
	 */
	private boolean doRecording(final int sampleRate,int encoding,int recordingBufferSize,int readBufferSize,int bufferIncreaseFactor) throws IOException{
		audioFileName= DataIOHelper.getRecordedFileName("pcm");
		File audioFile=new File(audioFileName);
		//audioFile.createNewFile();

        DataOutputStream output = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(audioFile)
				)
		);
		
		if(recordingBufferSize==AudioRecord.ERROR_BAD_VALUE){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
			return false;
		}
		else if(recordingBufferSize==AudioRecord.ERROR){
			Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
			return false;
		}
		//give it extra space to prevent overflow
		int increasedRecordingBufferSize=recordingBufferSize * bufferIncreaseFactor;
		recorder =
				new AudioRecord(AudioSource.MIC,
						sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						encoding,
						increasedRecordingBufferSize);

		 final short[] readBuffer=new short[readBufferSize];

		 recorder.startRecording();

		//readState is a state code or the length of readBuffer
		int readState;
		 while(recorder.getRecordingState() ==AudioRecord.RECORDSTATE_RECORDING){
			 readState= recorder.read(readBuffer, 0,readBufferSize);
			 if(readState==AudioRecord.ERROR_INVALID_OPERATION){
				 Log.e(TAG,"readState==AudioRecord.ERROR_INVALID_OPERATION");
			 }
			 else if(readState==AudioRecord.ERROR_BAD_VALUE){
				 Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
			 }
			 else{
				 //save the data
				 int i;
				 for(i=0;i<readState;++i){
					 output.writeShort(readBuffer[i]);
				 }
			 }
		 }
		recorder.release();
		recorder=null;

		output.flush();
		 output.close();
		//插入wav文件头
		///这里应使用wav存储格式
		 /*RandomAccessFile accessFile=new RandomAccessFile(audioFile,"rw");
		//这里的设置要和audioRecord 的设置对应
		wavHeader.setAdjustFileLength((int)accessFile.length()+44-8);
		wavHeader.setAudioDataLength((int)accessFile.length());
		wavHeader.setBlockAlign(AudioFormat.CHANNEL_IN_MONO,encoding);
		wavHeader.setByteRate(AudioFormat.CHANNEL_IN_MONO,sampleRate,encoding);
		wavHeader.setChannelCount(AudioFormat.CHANNEL_IN_MONO);
		wavHeader.setEncodingBit(encoding);
		wavHeader.setSampleRate(sampleRate);
		wavHeader.setWaveFormatPcm(WavHeader.WAV_FORMAT_PCM);
		 accessFile.seek(0);
		accessFile.write(wavHeader.getHeader());
		accessFile.close();*/
		return true;
	}

	public void stopRecoding(){
		if(recorder !=null){
			recorder.stop();
		}
	}
}
