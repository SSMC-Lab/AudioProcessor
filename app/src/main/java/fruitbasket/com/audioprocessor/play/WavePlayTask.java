package fruitbasket.com.audioprocessor.play;

import android.util.Log;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * 播放声波的任务
 * Created by Study on 28/06/2016.
 */
public class WavePlayTask implements Runnable {
    private static final String TAG=WavePlayTask.class.toString();

    private AudioTrackWrapper audioTrackWrapper;
    private AudioOutConfig audioOutConfig;
    private WaveType waveType;
    private int waveRate;//波的频率
    private int sampleRate;//设备实际发出声音的频率

    public WavePlayTask(WaveType waveType,int waveRate){
        this(waveType,waveRate, AppCondition.DEFAULE_SIMPLE_RATE);
    }

    public WavePlayTask(WaveType waveType,int waveRate,int sampleRate){
        this(waveType,waveRate,sampleRate,null);
    }

    public WavePlayTask(WaveType waveType,int waveRate,int sampleRate ,AudioOutConfig audioOutConfig){
        this.waveType=waveType;
        this.waveRate=waveRate;
        this.sampleRate=sampleRate;
        this.audioOutConfig=audioOutConfig;
    }

    @Override
    public void run() {
        Log.i(TAG,"run()");
        audioTrackWrapper=new AudioTrackWrapper(audioOutConfig);
        audioTrackWrapper.startPlaying(waveType,waveRate,sampleRate);
    }

    public void stopPlaying(){
        Log.i(TAG,"stopPlaying()");
        if(audioTrackWrapper !=null){
            audioTrackWrapper.stopPlaying();
            audioTrackWrapper.releaseResource();
            audioTrackWrapper=null;
        }
    }
}
