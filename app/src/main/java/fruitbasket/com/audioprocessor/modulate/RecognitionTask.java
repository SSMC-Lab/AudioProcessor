package fruitbasket.com.audioprocessor.modulate;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Study on 19/09/2016.
 */
final public class RecognitionTask implements Runnable {
    private static final String TAG="RecognitionTask";

    private Handler handler;
    private AudioRecognition audioRecognition;

    public RecognitionTask(){}

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return handler;
    }

    public boolean prepare(){
        if(handler!=null){
            audioRecognition=new AudioRecognition();
            audioRecognition.setHandler(handler);
            return true;
        }
        else{
            Log.w(TAG,"prepare() false: handler==null");
            return false;
        }
    }

    @Override
    public void run() {
        if(audioRecognition!=null){
            audioRecognition.startRecognition();
        }
        else{
            Log.w(TAG,"run(): audioRecognition==null");
        }
    }

    public void stop(){
        if(audioRecognition!=null){
            audioRecognition.stop();
            audioRecognition=null;
        }
    }
}
