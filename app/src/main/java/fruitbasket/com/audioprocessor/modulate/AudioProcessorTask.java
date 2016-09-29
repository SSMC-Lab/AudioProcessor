package fruitbasket.com.audioprocessor.modulate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.audioprocessor.AppCondition;


/**
 * 用于检测声音频率的任务
 * Created by Study on 18/09/2016.
 */
final public class AudioProcessorTask implements Runnable {
    private static final String TAG="AudioProcessorTask";

    private Handler handler;//赋予这个任务更新用户界面的能力
    private short[] audioData;

    public AudioProcessorTask(){}

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return handler;
    }

    public void setAudioData(short[] audioData){
        if(audioData!=null){
            //注意，这里使用的是浅复制。这意味着在本类的外部修改参数，会影响本类内部的数据
            this.audioData=audioData;
        }
        else{
            Log.w(TAG,"setAudioData(): audioData==null");
        }
    }

    public short[] getAudioData(){
        return audioData;
    }

    @Override
    public void run() {
        Log.i(TAG,"run()");
        if(audioData!=null) {
            int frequency = FrequencyDetector.getFrequence(
                    FFT.fft(audioData, true),
                    AppCondition.DEFAULE_SIMPLE_RATE
            );
            Log.i(TAG,"frequency="+frequency);
            if (handler != null) {
                Message message = new Message();
                message.what= ModulateCondition.AUDIO_PROCESSOR;

                Bundle bundle = new Bundle();
                bundle.putInt(ModulateCondition.KEY_FREQUENCY, frequency);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else{
                Log.w(TAG,"run(): handle==null");
            }
        }
        else{
            Log.w(TAG,"run(): audioData==null");
        }
    }

}
