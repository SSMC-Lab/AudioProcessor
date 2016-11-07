package fruitbasket.com.audioprocessor.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.audioprocessor.process.fft.STFT;


/**
 * 用于检测声音频率的任务
 * Created by Study on 18/09/2016.
 */
final class AudioRecognitionTask implements Runnable {
        private static final String TAG="AudioRecognitionTask";

    private Handler handler;//赋予这个任务更新用户界面的能力
    private short[] audioData;
    private STFT stft;

    private int frequency;///just for test

    public AudioRecognitionTask(){
        stft=new STFT(STFT.FFT_LENGTH_1024);
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
            Log.i(TAG,"audioData!=null");
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

            char recognizeChar=decodeChar();
            Log.i(TAG,"recognizeChar=="+recognizeChar);

            if (handler != null) {
                Message message = new Message();
                message.what= PCondition.AUDIO_PROCESSOR;

                Bundle bundle = new Bundle();
                bundle.putInt(PCondition.KEY_FREQUENCY, frequency);
                bundle.putChar(PCondition.KEY_RECOGNIZE_CHAR,recognizeChar);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else{
                Log.w(TAG,"run(): handle==null");
            }
        }
        else{
            Log.e(TAG,"run(): audioData==null");
        }
    }

    /**
     * 将一段声音信号解码成单个字符。注意，此声音信号只能包含单个字符
     * @reurn 解码得到的字符
     */
    private char decodeChar() {
        Log.i(TAG, "decodeChar()");
        if (audioData == null) {
            Log.e(TAG, "audioData==null");
            return '\u0000';
        } else {
            Log.i(TAG, "audioData!=null");
            ///原来的处理方法，现暂时弃用
            /*final int frequency = FrequencyDetector.getSingleFrequence(
                    FFT.fft(audioData, true),
                    AppCondition.DEFAULE_SIMPLE_RATE
            );
            Log.i(TAG,"frequency=="+frequency);
            return charOfFrequency(frequency);*/

            stft.feedData(audioData);
            stft.getSpectrumAmp();
            stft.calculatePeak();

            frequency=(int)stft.maxAmpFreq;
            Log.i(TAG,"stft.maxAmpFreq=="+stft.maxAmpFreq);

            return charOfFrequency((int)stft.maxAmpFreq);
        }
    }

    /**
     * 将一段声音信号解码成一个字符串
     * @return 解码得到的字符串
     */
    private String decodeString(){
        if(audioData==null){
            Log.e(TAG,"audioData==null");
            return null;
        }
        else{
            ///
            return null;
        }
    }

    /**
     * 根据频率，返回对应的字符
     * @param frequency
     * @return
     */
    private static char charOfFrequency(int frequency){

        final int bookLength= PCondition.WAVE_RATE_BOOK.length-2;//记录WAVE_RATE_BOOK包含声波频率的实际个数
        final int errorRange=5;//定义一个误差范围
        int standard;
        int index;
        int i;
        //之所以这样控制循环范围，是因为不能使用WAVE_RATE_BOOK的开始和结束的元素
        for(i=1;i<=bookLength;i++){
            standard= PCondition.WAVE_RATE_BOOK[i];

            if(frequency>=standard-errorRange
                    &&frequency<=standard+errorRange){
                index=i-1;
                return PCondition.CHAR_BOOK.charAt(index);
            }
        }

        //如果frequency无效
        Log.w(TAG,"i>=bookLength : frequency is invalid");
        return '\u0000';
    }
}
