package fruitbasket.com.audioprocessor.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import fruitbasket.com.audioprocessor.AudioService;
import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.R;
import fruitbasket.com.audioprocessor.modulate.ModulateCondition;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * Created by Study on 21/06/2016.
 */
public class TestFragment extends Fragment {
    private static final String TAG="ui.TestFragment";

    private RadioGroup channelOut;
    private ToggleButton waveProducer;
    private SeekBar seekbarWaveRate;
    private TextView textVeiwWaveRate;
    private ToggleButton sendText;
    private ToggleButton record;
    private ToggleButton frequenceDectector;
    private TextView frequenceTextView;
    private ToggleButton playPcm;
    private EditText pcmAudioPath;

    private int waveRate;
    private Handler handler;

    private Intent intentToRecord;
    private AudioService audioService;
    private ServiceConnection serviceConnection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG,"ServiceConnection.onServiceConnection()");
            audioService =((AudioService.RecordServiceBinder)binder).getService();
            audioService.setHandler(handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,"ServiceConnection.onServiceDisConnection()");
            audioService =null;
        }

    };

    public TestFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler=new MyHandler();
        intentToRecord=new Intent(getActivity(),AudioService.class);
        if(audioService ==null) {
            getActivity().bindService(intentToRecord, serviceConnection, Context.BIND_AUTO_CREATE);
            //do not execute startService()
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_fragment,container,false);
        initializeViews(rootView);
        return rootView;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy()");
        if(audioService !=null){
            getActivity().unbindService(serviceConnection);
            getActivity().stopService(intentToRecord);//must stop the Service
            audioService =null;
        }
        super.onDestroy();
    }

    private void initializeViews(View view){
        ToggleClickListener listener=new ToggleClickListener();

        channelOut=(RadioGroup)view.findViewById(R.id.channel_out);
        channelOut.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.channel_out_left:
                        audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_LEFT);
                        break;
                    case R.id.channel_out_right:
                        audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_RIGHT);
                        break;
                    default:
                        audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_BOTH);
                }
            }
        });

        waveProducer =(ToggleButton)view.findViewById(R.id.wave_producer);
        waveProducer.setOnClickListener(listener);

        textVeiwWaveRate =(TextView)view.findViewById(R.id.text_view_waverate);

        seekbarWaveRate =(SeekBar)view.findViewById(R.id.seekbar_waverate);
        seekbarWaveRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveRate =progress*1000;
                textVeiwWaveRate.setText(getResources().getString(R.string.frequency,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        waveRate = seekbarWaveRate.getProgress()*1000;
        textVeiwWaveRate.setText(getResources().getString(R.string.frequency,seekbarWaveRate.getProgress()));

        sendText=(ToggleButton)view.findViewById(R.id.send_text);
        sendText.setOnClickListener(listener);

        record=(ToggleButton)view.findViewById(R.id.record);
        record.setOnClickListener(listener);

        frequenceDectector=(ToggleButton)view.findViewById(R.id.frequence_dectector);
        frequenceDectector.setOnClickListener(listener);
        frequenceTextView=(TextView)view.findViewById(R.id.frequence);

        //playPcm=(ToggleButton)view.findViewById(R.id.play_pcm);
        //playPcm.setOnClickListener(listener);

    }


    private void startPlayingWave(){
        if(audioService!=null){
            audioService.startPlayingWave(WaveType.SIN, waveRate, AppCondition.DEFAULE_SIMPLE_RATE);
        }
    }

    private void stopPlayingWave(){
        if(audioService!=null){
            audioService.stopPlayingWave();
        }
    }

    private void startSendingText(){
        Log.i(TAG,"startSendingText()");
        if(audioService!=null){
            audioService.startSendingText();
        }
    }

    private void stopSendingText(){
        Log.i(TAG,"stopSendingText()");
        if(audioService!=null){
            audioService.stopSendingText();
        }
    }

    private void startRecord(){
        Log.i(TAG,"startRecord()");
        if(audioService!=null){
            audioService.startRecord();
        }
    }

    private void stopRecord(){
        Log.i(TAG,"stopRecord()");
        if(audioService!=null){
            audioService.stopRecord();
        }
    }

    private void startFrequenceDetect(){
        Log.i(TAG,"startFrequenceDetect()");
        if(audioService!=null){
            audioService.startRecognition();
        }
    }

    private void stopFrequenceDetect(){
        Log.i(TAG,"stopFrequenceDetect()");
        if(audioService!=null){
            audioService.stopRecognition();
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message message){
            Log.i(TAG,"MyHandler.handlerMessage()");
            if(message.what== ModulateCondition.AUDIO_PROCESSOR){
                Log.i(TAG,"message.what==ModulateCondition.AUDIO_PROCESSOR");
                int frequency=message.getData().getInt(ModulateCondition.KEY_FREQUENCY);
                Log.i(TAG,"frequency="+frequency);
                frequenceTextView.setText(getResources().getString(R.string.detect_frequency,frequency));
            }
        }
    }

    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.wave_producer:
                    if(((ToggleButton)view).isChecked()){

                        stopSendingText();
                        sendText.setChecked(false);

                        startPlayingWave();
                    }
                    else{
                        stopPlayingWave();
                    }
                    break;

                case R.id.send_text:
                    if(((ToggleButton)view).isChecked()){

                        stopPlayingWave();
                        waveProducer.setChecked(false);

                        startSendingText();
                    }
                    else{
                        stopSendingText();
                    }
                    break;

                case R.id.record:
                    if(((ToggleButton)view).isChecked()){
                        stopFrequenceDetect();
                        frequenceDectector.setChecked(false);

                        startRecord();
                    }
                    else{
                        stopRecord();
                    }
                    break;

                case R.id.frequence_dectector:
                    if(((ToggleButton)view).isChecked()){
                        stopRecord();
                        record.setChecked(false);

                        startFrequenceDetect();
                    }
                    else{
                        stopFrequenceDetect();
                    }
                    break;
            }
        }
    }
}
