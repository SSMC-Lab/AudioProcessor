package fruitbasket.com.audioprocessor.process;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fruitbasket.com.audioprocessor.AppCondition;

/**
 * 用于识别音频信息
 */
final class AudioRecognition {
    private static final String TAG="AudioRecognition";

    private Handler handler;//赋予这个类更新用户界面的能力
    private boolean isRecording =false;//指示是否正在录音
    private Thread recognitionThread;///
    private Decoder decoder;

    public AudioRecognition(){
        decoder=new Decoder();
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return handler;
    }

    /**
     * 检测声音的频率
     * @return
     */
    public void startRecognition(){
        int bufferSize = AudioRecord.getMinBufferSize(
                AppCondition.DEFAULE_SIMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if(bufferSize==AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
            return ;
        }
        else if(bufferSize==AudioRecord.ERROR){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
            return ;
        }

        Log.i(TAG,"bufferSize="+bufferSize);
        bufferSize=adjustLength(bufferSize,AppCondition.DEFAULE_SIMPLE_RATE);
        Log.i(TAG,"after adjusted, bufferSize="+bufferSize);
        short[] buffer=new short[bufferSize];//用于存放录得的音频数据

        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                AppCondition.DEFAULE_SIMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        audioRecord.startRecording();
        recognitionThread=new Thread(new RecognitionTask(decoder,handler));
        recognitionThread.start();
        isRecording =true;
        while(isRecording ==true){
            int readResult = audioRecord.read(buffer, 0, bufferSize);
            if(readResult==AudioRecord.ERROR_INVALID_OPERATION){
                Log.e(TAG,"readState==AudioRecord.ERROR_INVALID_OPERATION");
                return ;
            }
            else if(readResult==AudioRecord.ERROR_BAD_VALUE){
                Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
                return ;
            }
            else{
                Log.d(TAG,"buffer.length="+buffer.length);
                //对录取得的数据进行处理
                if(decoder.updateAudioData(buffer)==false){
                    stop();
                }
            }
        }
        //在结束以上循环后就释放资源
        audioRecord.stop();
        audioRecord.release();

    }

    public void stop(){
        Log.i(TAG,"stop()");
        isRecording = false;
        recognitionThread.interrupt();
    }

    /**
     * 对audioDataLength进行调整
     * @param audioDataLength 大于或等于0的整数
     * @return
     */
    private static int adjustLength(int audioDataLength,int sampleRate){
        if(audioDataLength<=0){
            return 0;
        }
        else{
            int length=1;

            //第一次调整。使length一个大于audioDataLength，又是2的次幂
            int factor=2;
            while(audioDataLength>length){
                length*=factor;
            }

            //第二次调整
            while(length<(sampleRate/(1000/Encoder.DEFAULT_DURATION))){
                length*=2;
            }
              return length;
        }
    }
}
