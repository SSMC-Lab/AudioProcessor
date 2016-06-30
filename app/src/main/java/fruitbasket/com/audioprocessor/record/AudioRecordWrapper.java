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
	 * @return
	 */
	public boolean startRecording(){
		return startRecording(Condition.SIMPLE_RATE_CD,AudioFormat.ENCODING_PCM_16BIT);
	}
	
	/**
	 * 
	 * @param sampleRate : the number of  samples per second
	 * @param encoding: specific the quantity of bit of each voice frame
	 * @return
	 */
	public boolean startRecording(final int sampleRate,int encoding){
		boolean state = false;
		int bufferSize=determineMinimumBufferSize(sampleRate,encoding);
		try {
			state=doRecording(sampleRate,encoding,bufferSize,bufferSize,DEFAULT_BUFFER_INCREASE_FACTOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	/**
	 * get the minimum audio buffer size, according to the sample rate and encording
	 * @param sampleRate
	 * @param encoding
	 * @return
	 */
	private int determineMinimumBufferSize(final int sampleRate,int encoding){
		return AudioRecord.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_IN_MONO,encoding);
	}
	
	/**
	 * calculate audio buffer size such that it holds numSamplesInBuffer and is bigger than the minimum size, according to sample rate,encoding and numSamplesInBuffer
	 * @param sampleRate : the number of  samples per second
	 * @param encoding
	 * @param numSamplesInBuffer : the quantity of samples should be saved in the audio buffer
	 * @return
	 */
	private int determineCalculateBufferSize(final int sampleRate,int encoding,int numSamplesInBuffer){
		int minBufferSize=determineMinimumBufferSize(sampleRate,encoding);
		int bufferSize;
		if(encoding==AudioFormat.ENCODING_PCM_16BIT){
			bufferSize=numSamplesInBuffer*2;
		}
		else{
			bufferSize=numSamplesInBuffer;
		}
		///
		if(bufferSize<minBufferSize){
			bufferSize=minBufferSize;
		}
		return bufferSize;
	}
	
	/**
	 * 
	 * @param sampleRate
	 * @param encoding
	 * @param recordingBufferSize
	 * @param readBufferSize
	 * @param bufferIncreaseFactor
	 * @return
	 * @throws IOException 
	 */
	private boolean doRecording(final int sampleRate,int encoding,int recordingBufferSize,int readBufferSize,int bufferIncreaseFactor) throws IOException{
		audioFileName= DataIOHelper.getRecordedFileName("pcm");///
		File audioFile=new File(audioFileName);
		audioFile.createNewFile();

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

		 while(recorder.getRecordingState() ==AudioRecord.RECORDSTATE_RECORDING){
			 //bufferReasult is a state code or the length of readBuffer
			 final int bufferResult= recorder.read(readBuffer, 0,readBufferSize);
			 if(bufferResult==AudioRecord.ERROR_INVALID_OPERATION){
				 Log.e(TAG,"bufferResult==AudioRecord.ERROR_INVALID_OPERATION");
			 }
			 else if(bufferResult==AudioRecord.ERROR_BAD_VALUE){
				 Log.e(TAG,"bufferResult==AudioRecord.ERROR_BAD_VALUE");
			 }
			 else{
				 //save the data
				 int i;
				 for(i=0;i<bufferResult;++i){
					 output.writeShort(readBuffer[i]);
				 }
			 }
		 }
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
			recorder.release();
			recorder =null;
		}
	}
}
