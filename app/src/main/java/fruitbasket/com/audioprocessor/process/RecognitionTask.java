package fruitbasket.com.audioprocessor.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

import fruitbasket.com.audioprocessor.process.fft.STFT;

/**
 * 用于检测声音信息的任务。
 * 它将识别声音包含的主要频率，来得出声音信息
 * Created by FruitBasket on 07/11/2016.
 */
final class RecognitionTask implements Runnable {
    private static final  String TAG=RecognitionTask.class.getName();

    private Decoder decoder;
    private Handler handler;//赋予这个任务更新用户界面的能力

    public RecognitionTask(Decoder decoder,Handler handler){
        this.decoder=decoder;
        this.handler=handler;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        Log.i(TAG, "run()");
        String decodeString= null;

        try {
            decodeString = decoder.decodeString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Log.i(TAG,"begin to put the decodeString to the screen");
        if(handler!=null){
            Log.i(TAG,"handler!=null : handler is ok");
            Message message = new Message();
            message.what= PCondition.AUDIO_PROCESSOR;

            Bundle bundle = new Bundle();
            bundle.putString(PCondition.KEY_RECOGNIZE_STRING,decodeString);
            message.setData(bundle);
            handler.sendMessage(message);
        }
        else {
            Log.w(TAG, "handler==null");
        }
    }
}
