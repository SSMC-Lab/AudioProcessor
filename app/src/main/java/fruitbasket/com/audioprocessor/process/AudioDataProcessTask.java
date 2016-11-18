package fruitbasket.com.audioprocessor.process;

import android.os.Handler;
import android.util.Log;

/**
 * 启动声音信息处理功能
 * Created by Study on 19/09/2016.
 */
final public class AudioDataProcessTask implements Runnable {
    private static final String TAG="AudioDataProcessTask";

    private Handler handler;
    private AudioRecognition audioRecognition;

    public AudioDataProcessTask(){}

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return handler;
    }

    /**
     * 开始执行本任务前，必须执行本方法，以准备相关的资源
     * @return
     */
    public boolean prepare(){
        if(handler!=null){
            audioRecognition=new AudioRecognition();
            audioRecognition.setHandler(handler);///有空要解决handler传递的问题
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
            audioRecognition.stop();//当audioRecognition停止后就会自动释放资源
            audioRecognition=null;
        }
    }
}
