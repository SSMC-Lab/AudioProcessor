package fruitbasket.com.audioprocessor.process;

import android.util.Log;

import fruitbasket.com.audioprocessor.process.fft.STFT;

/**
 * 将一段声音信号解码成文本
 * Created by Study on 20/10/2016.
 */

class Decoder {
    private static final String TAG="modulate.Decoder";

    private STFT stft;


    public void Decoder(){
        Log.i(TAG,"Decoder()");
        stft=new STFT(STFT.FFT_LENGTH_1024);
    }

    /**
     * 将一段声音信号解码成单个字符。注意，此声音信号只能包含单个字符
     * @reurn
     */
    public char decodeChar(short[] audioData){
        Log.i(TAG,"decodeChar()");
        if(audioData==null){
            Log.e(TAG,"audioData==null");
            return '\u0000';
        }
        else {
            Log.i(TAG,"audioData!=null");
            ///原来的处理方法，现暂时弃用
            /*final int frequency = FrequencyDetector.getSingleFrequence(
                    FFT.fft(audioData, true),
                    AppCondition.DEFAULE_SIMPLE_RATE
            );
            Log.i(TAG,"frequency=="+frequency);
            return charOfFrequency(frequency);*/


            return '\u0000';
        }
    }

    /**
     * 将一段声音信号解码成一个字符串
     * @param audioData
     * @return
     */
    public String decodeString(short[] audioData){
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
