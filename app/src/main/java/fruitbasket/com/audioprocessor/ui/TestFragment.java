package fruitbasket.com.audioprocessor.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import fruitbasket.com.audioprocessor.AudioService;
import fruitbasket.com.audioprocessor.Condition;
import fruitbasket.com.audioprocessor.R;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.record.AudioRecordWrapper;

/**
 * Created by Study on 21/06/2016.
 */
public class TestFragment extends Fragment {

    private static final String TAG="TestFragment";

    private EditText recordingFilePath;

    private ToggleButton recorder;
    private ToggleButton player;
    private ToggleButton playAndRecord;
    private ToggleButton playRecordingFile;
    private RadioGroup channelOut;

    private AudioManager audioManager;

    private Intent intentToRecord;
    private AudioService audioService;
    private ServiceConnection serviceConnection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG,"ServiceConnection.onServiceConnection()");
            audioService =((AudioService.RecordServiceBinder)binder).getService();
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

        intentToRecord=new Intent(getActivity(),AudioService.class);
        if(audioService ==null){
            getActivity().bindService(intentToRecord,serviceConnection, Context.BIND_AUTO_CREATE);
            //do not execute startService()
        }

        audioManager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
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
        recorder =(ToggleButton)view.findViewById(R.id.recorder);
        recorder.setOnClickListener(listener);

        player =(ToggleButton)view.findViewById(R.id.player);
        player.setOnClickListener(listener);

        playAndRecord=(ToggleButton)view.findViewById(R.id.play_and_record);
        playAndRecord.setOnClickListener(listener);

        playRecordingFile =(ToggleButton)view.findViewById(R.id.play_recording_file);
        playRecordingFile.setOnClickListener(listener);

        recordingFilePath=(EditText)view.findViewById(R.id.recording_file_path);

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
                    case R.id.channel_out_both:
                        audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_BOTH);
                        break;
                }
            }
        });
    }

    /**
     * 开始录制音频
     */
    private void startRecording(){
        Log.d(TAG, "startRecording()");
        if(audioService !=null){
            audioService.startRecording();
        }
    }

    /**
     * 停止录制音频
     */
    private void stopRecording(){
        Log.d(TAG,"stopRecording()");
        if(audioService !=null){
            audioService.stopRecording();
        }
    }

    /**
     * 播放音频文件
     */
    private void startPlayingAudioFile(){
        Log.d(TAG,"startPlayingAudioFile()");
        if(audioService !=null){
            audioService.startPlayingAudioFile();
        }
    }

    /**
     * 停止播放音频文件
     */
    private void stopPlayingAudioFile(){
        Log.d(TAG,"stopPlayingAudioFile");
        if(audioService !=null){
            audioService.stopPlayingAudioFile();
        }
    }

    /**
     * 开始播放录音文件（.pcm）文件
     */
    private void startPlayingRecordingFile(){
        Log.d(TAG,"startPlayingRecordingFile()");
        if(audioService !=null){
            String path= Condition.APP_FILE_DIR+"/"+recordingFilePath.getText().toString().trim();
            audioService.startPlaying(path, AudioRecordWrapper.RECORDER_SAMPLERATE_CD);
        }
    }

    /**
     * 停止播放录音（.pcm）文件
     */
    private void stopPlayingRecordingFile(){
        Log.d(TAG,"stopPlayingRecordingFile()");
        if(audioService !=null){
            audioService.stopPlaying();
        }
    }




    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.recorder:
                    if(((ToggleButton) view).isChecked()==true){
                        startRecording();
                    }
                    else{
                        stopRecording();
                    }
                    break;

                case R.id.player:
                    if(((ToggleButton) view).isChecked()==true){
                        startPlayingAudioFile();
                    }
                    else{
                        stopPlayingAudioFile();
                    }
                    break;

                case R.id.play_and_record:
                    if(((ToggleButton)view).isChecked()==true){
                        startPlayingAudioFile();
                        startRecording();
                    }
                    else{
                        stopRecording();
                        stopPlayingAudioFile();
                    }
                    break;

                case R.id.play_recording_file:
                    if(((ToggleButton)view).isChecked()==true){
                        startPlayingRecordingFile();
                    }
                    else{
                        stopPlayingRecordingFile();
                    }
                    break;
            }
        }
    }
}
