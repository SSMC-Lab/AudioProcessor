package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.DataIOHelper;

/**
 * 用于录制wav格式的音频
 * Created by Study on 20/10/2016.
 */

public class WavRecordTask implements Runnable {
    private static final String TAG="record.WavRecordTask";

    private int channelIn;//用于指定音频来源的数量
    private boolean isRecording=false;

    public WavRecordTask(){
        this(AudioFormat.CHANNEL_IN_MONO);
    }

    public WavRecordTask(int channelIn){
        Log.i(TAG,"WavRecordTask()");
        setChannelIn(channelIn);
    }

    public void setChannelIn(int channelIn){
        if(channelIn== AudioFormat.CHANNEL_IN_STEREO){
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
        Log.i(TAG,"run()");

        final int sampleRate=AppCondition.DEFAULE_SIMPLE_RATE;
        final int encoding=AudioFormat.ENCODING_PCM_16BIT;

        String audioFullName = DataIOHelper.getRecordedFileName("pcm");
        File audioFile= new File(audioFullName);

        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelIn,
                AudioFormat.ENCODING_PCM_16BIT);
        if(bufferSize==AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
            return;
        }
        else if(bufferSize==AudioRecord.ERROR){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
            return;
        }
        byte[] buffer=new byte[bufferSize];

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
                    return;
                }
                else if(readResult==AudioRecord.ERROR_BAD_VALUE){
                    Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
                    return;
                }
                else{
                    for(int i=0;i<readResult;i++){
                        output.writeByte(buffer[i]);
                    }
                }
            }
            //结束以上循环后就停止播放并释放资源
            audioRecord.stop();
            output.flush();
            output.close();
            audioRecord.release();
            audioRecord=null;

            Log.i(TAG,"make wav file");
            //制作wav文件
            ///这里先将原始音频保存起来，在改装成wav文件，这不是一个好做法
			FileInputStream fis= new FileInputStream(audioFile);
			BufferedInputStream inputStream=new BufferedInputStream(fis);

			BufferedOutputStream outputStream=new BufferedOutputStream(
					new FileOutputStream(audioFullName+".wav")
			);
			byte[] readBuffer=new byte[1024];

			int length=(int)fis.getChannel().size();

            Log.i(TAG,"create a wav file header");
            WavHeader wavHeader=new WavHeader();
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
			outputStream.close();
            audioFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        isRecording=false;
    }
}
