package fruitbasket.com.audioprocessor.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.process.fft.FFT;


/**
 * 用于检测声音频率的任务
 * Created by Study on 18/09/2016.
 */
final class AudioRecognitionTask implements Runnable {
    private static final String TAG="AudioRecognitionTask";

    private Handler handler;//赋予这个任务更新用户界面的能力
    private short[] audioData;
    private Decoder decoder;

    public AudioRecognitionTask(){
        decoder=new Decoder();
    }

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

            ///已转移到Decoder中处理
            /*final int frequency = FrequencyDetector.getSingleFrequence(
                    FFT.fft(audioData, true),
                    AppCondition.DEFAULE_SIMPLE_RATE
            );
            Log.i(TAG,"frequency="+frequency);

            char recognizeChar='\u0000';
            //final int bookLength=PCondition.WAVE_RATE_BOOK.length-2;//记录WAVE_RATE_BOOK包含声波频率的实际个数
            final int errorRange=5;//定义一个误差范围
            int standard;
            int i;

            //for(i=1;i<=bookLength;i++){//之所以这样控制循环范围，是因为不能使用WAVE_RATE_BOOK开始和结束的元素
            for(i=0; i< PCondition.WAVE_RATE_BOOK.length; i++){
                standard= PCondition.WAVE_RATE_BOOK[i];

                if(frequency>=standard-errorRange
                        &&frequency<=standard+errorRange){
                    if(i== PCondition.START){

                    }
                    else if(i== PCondition.END){

                    }
                    else{
                        recognizeChar= PCondition.CHAR_BOOK.charAt(i-1);
                    }
                }
                else{
                    //Log.i(TAG,"frequency detecting error");
                }
            }*/

            /*char recognizeChar='\u0000';
            recognizeChar=decoder.decodeChar(audioData);*/

            if (handler != null) {
                Message message = new Message();
                message.what= PCondition.AUDIO_PROCESSOR;

                Bundle bundle = new Bundle();
                //bundle.putInt(PCondition.KEY_FREQUENCY, frequency);
                //bundle.putChar(PCondition.KEY_RECOGNIZE_CHAR,recognizeChar);
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
