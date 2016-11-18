package fruitbasket.com.audioprocessor.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.lang.ref.WeakReference;

import fruitbasket.com.audioprocessor.AudioService;
import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.R;
import fruitbasket.com.audioprocessor.process.PCondition;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * Created by FruitBasket on 21/06/2016.
 */
public class TestFragment extends Fragment {
    private static final String TAG="ui.TestFragment";

    private RadioGroup channelOut;
    private ToggleButton waveProducer;
    private SeekBar seekbarWaveRate;
    private TextView textVeiwWaveRate;
    private ToggleButton sendText;
    private ToggleButton record;
    private RadioGroup channelIn;
    private ToggleButton frequenceDectector;
    private TextView frequence;
    private TextView recognizeTextView;
    private ToggleButton playPcm;
    private EditText pcmAudioPath;

    private ToggleButton recordWav;
    private RadioGroup wavchannelIn;
    private ToggleButton playWav;
    private EditText wavAudioPath;

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

        handler=new MyHandler(this);
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
        MyCheckedChangeListener mccListener=new MyCheckedChangeListener();
        ToggleClickListener tcListener=new ToggleClickListener();

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
        waveProducer.setOnClickListener(tcListener);

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
        sendText.setOnClickListener(tcListener);

        record=(ToggleButton)view.findViewById(R.id.record);
        record.setOnClickListener(tcListener);

        channelIn=(RadioGroup)view.findViewById(R.id.channel_in);
        channelIn.setOnCheckedChangeListener(mccListener);

        frequenceDectector=(ToggleButton)view.findViewById(R.id.frequence_dectector);
        frequenceDectector.setOnClickListener(tcListener);
        frequence =(TextView)view.findViewById(R.id.frequence);
        recognizeTextView =(TextView)view.findViewById(R.id.recognize);

        playPcm=(ToggleButton)view.findViewById(R.id.play_pcm);
        playPcm.setOnClickListener(tcListener);
        pcmAudioPath=(EditText)view.findViewById(R.id.pcm_audio_path);

        wavchannelIn=(RadioGroup)view.findViewById(R.id.wav_channel_in);
        wavchannelIn.setOnCheckedChangeListener(mccListener);

        recordWav=(ToggleButton)view.findViewById(R.id.record_wav);
        recordWav.setOnClickListener(tcListener);

        playWav=(ToggleButton)view.findViewById(R.id.play_wav);
        playWav.setOnClickListener(tcListener);

        wavAudioPath=(EditText)view.findViewById(R.id.wav_audio_path);
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

    private void startPlayPcm(){
        Log.i(TAG,"startPlayPcm()");
        if(audioService!=null){
            String string=pcmAudioPath.getText().toString().trim();
            if(!TextUtils.isEmpty(string)){
                audioService.startPlayPcm(AppCondition.APP_FILE_DIR+ File.separator+string);
            }
            else{
                Log.w(TAG,"TextUtils.isEmpty(pcmAudioPath.getText().toString().trim())==true");
            }
        }
    }

    private void stopPlayPcm(){
        Log.i(TAG,"stopPlayPcm()");
        if(audioService!=null){
            audioService.stopPlayPcm();
        }
    }

    private void startRecordWav(){
        Log.i(TAG,"startRecordWav()");
        if(audioService!=null){
            audioService.startRecordWav();
        }
    }

    private void stopRecordWav(){
        Log.i(TAG,"stopRecordWav()");
        if(audioService!=null){
            audioService.stopRecordWav();
        }
    }

    private void startPlayWav(){
        Log.i(TAG,"startPlayWav()");
        if(audioService!=null){
            String string=wavAudioPath.getText().toString().trim();
            if(!TextUtils.isEmpty(string)){
                audioService.startPlayWav(AppCondition.APP_FILE_DIR+ File.separator+string);
            }
            else{
                Log.w(TAG,"TextUtils.isEmpty(wavAudioPath.getText().toString().trim())==true");
            }
        }
    }

    private void stopPlayWav(){
        Log.i(TAG,"stopPlayWav()");
        if(audioService!=null){
            audioService.stopPlayWav();
        }
    }

    private static class MyHandler extends Handler {

        private final WeakReference<TestFragment> mFragment;

        public MyHandler(TestFragment fregment) {
            mFragment = new WeakReference<TestFragment>(fregment);
        }

        @Override
        public void handleMessage(Message message){
            Log.i(TAG,"MyHandler.handlerMessage()");
            TestFragment fragment= mFragment.get();
            if(message.what== PCondition.AUDIO_PROCESSOR){
                Log.i(TAG,"message.what==PCondition.AUDIO_PROCESSOR");

                Bundle bundle=message.getData();

                String recognizeString=bundle.getString(PCondition.KEY_RECOGNIZE_STRING);
                Log.i(TAG,"recognizeString="+recognizeString);
                fragment.recognizeTextView.setText("reconize : "+recognizeString);

                int frequency=bundle.getInt(PCondition.KEY_FREQUENCY);
                Log.i(TAG,"frequency="+frequency);
                fragment.frequence.setText(fragment.getResources().getString(R.string.detect_frequency,frequency));

                /*char recognizeChar=bundle.getChar(PCondition.KEY_RECOGNIZE_CHAR);
                Log.i(TAG,"recognizeChar="+recognizeChar);
                recognizeTextView.setText("reconize : "+recognizeChar);*/
            }
        }
    }

    private class MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                case R.id.channel_in_mono:
                    audioService.setChannelIn(AudioFormat.CHANNEL_IN_MONO);
                    break;
                case R.id.channel_in_stereo:
                    audioService.setChannelIn(AudioFormat.CHANNEL_IN_STEREO);
                    break;
                case R.id.wav_channel_in_mono:
                    audioService.setChannelIn(AudioFormat.CHANNEL_IN_MONO);
                    break;
                case R.id.wav_channel_in_stereo:
                    audioService.setChannelIn(AudioFormat.CHANNEL_IN_STEREO);
                    break;
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
                        stopPlayPcm();
                        stopPlayWav();
                        sendText.setChecked(false);
                        playPcm.setChecked(false);
                        playWav.setChecked(false);

                        startPlayingWave();
                    }
                    else{
                        stopPlayingWave();
                    }
                    break;

                case R.id.send_text:
                    if(((ToggleButton)view).isChecked()){

                        stopPlayingWave();
                        stopPlayPcm();
                        stopPlayWav();
                        waveProducer.setChecked(false);
                        playPcm.setChecked(false);
                        playWav.setChecked(false);

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
                        stopRecordWav();
                        recordWav.setChecked(false);

                        startRecord();
                    }
                    else{
                        stopRecord();
                    }
                    break;

                case R.id.frequence_dectector:
                    if(((ToggleButton)view).isChecked()){
                        stopRecord();
                        stopRecordWav();
                        record.setChecked(false);
                        recordWav.setChecked(false);

                        startFrequenceDetect();
                    }
                    else{
                        stopFrequenceDetect();
                    }
                    break;

                case R.id.play_pcm:
                    if(((ToggleButton)view).isChecked()){
                        stopPlayingWave();
                        stopSendingText();
                        stopPlayWav();
                        waveProducer.setChecked(false);
                        sendText.setChecked(false);
                        playWav.setChecked(false);

                        startPlayPcm();
                    }
                    else{
                        stopPlayPcm();
                    }
                    break;

                case R.id.record_wav:
                    if(((ToggleButton)view).isChecked()){
                        stopFrequenceDetect();
                        frequenceDectector.setChecked(false);
                        stopRecord();
                        record.setChecked(false);

                        startRecordWav();
                    }
                    else{
                        stopRecordWav();
                    }
                    break;

                case R.id.play_wav:
                    if(((ToggleButton)view).isChecked()){
                        stopPlayingWave();
                        stopSendingText();
                        stopPlayPcm();
                        waveProducer.setChecked(false);
                        sendText.setChecked(false);
                        playPcm.setChecked(false);

                        startPlayWav();
                    }
                    else{
                        stopPlayWav();
                    }
                    break;
            }
        }
    }
}
