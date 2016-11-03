package fruitbasket.com.audioprocessor.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.AudioService;
import fruitbasket.com.audioprocessor.R;
import fruitbasket.com.audioprocessor.process.PCondition;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * Created by Study on 21/06/2016.
 */
public class MainFragment extends Fragment
        implements  View.OnClickListener{
    private Toolbar toolbar;
    private Button send;
    private ImageView type_change;
    private EditText edit_text;
    private RecyclerView mRecyclerView;
    private CardViewAdapter myAdapter;
    private RelativeLayout main_word;
    private Button main_voice;
    private LinearLayout pop_up;
    private boolean type_voice=false;
    private boolean pop_up_now=false;
    private List<String> Content_List=new ArrayList<>();

    private Handler handler;
    private Intent intentToRecord;
    private AudioService audioService;
    private int waveRate;
    private ServiceConnection serviceConnection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            audioService =((AudioService.RecordServiceBinder)binder).getService();
            audioService.setHandler(handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            audioService =null;
        }

    };

    public MainFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler=new MyHandler();
        intentToRecord=new Intent(getActivity(),AudioService.class);
        if(audioService ==null) {
            getActivity().bindService(intentToRecord, serviceConnection, Context.BIND_AUTO_CREATE);
            //do not execute startService()
        }
       // setState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.main_fragment,container,false);
        send = (Button) rootView.findViewById(R.id.main_send);
        send.setOnClickListener(this);

        type_change = (ImageView) rootView.findViewById(R.id.main_type_change);
        type_change.setOnClickListener(this);

        edit_text = (EditText) rootView.findViewById(R.id.main_text);

        main_word = (RelativeLayout) rootView.findViewById(R.id.main_word);

        pop_up = (LinearLayout) rootView.findViewById(R.id.main_popup);

        main_voice = (Button) rootView.findViewById(R.id.main_voice);
        main_voice.setOnClickListener(this);

        toolbar = (Toolbar) rootView.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        myAdapter = new CardViewAdapter(Content_List);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(myAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_setting) {
            Intent intent = new Intent(this.getContext(),Setting.class);
            startActivityForResult(intent,1);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        if(audioService !=null){
            getActivity().unbindService(serviceConnection);
            getActivity().stopService(intentToRecord);//must stop the Service
            audioService =null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_send : setState();SendMsg();break;
            case R.id.main_type_change :Type_change();break;
            case R.id.main_voice:setState();solve_pop();break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String content = prefs.getString(Setting.Clear,"0");
        Log.d("Liar","22");
        if(Integer.valueOf(content) == 1) {
            Log.d("Liar","33");
            if(Content_List.size() > 0) {
                Content_List.clear();
                myAdapter.notifyDataSetChanged();
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Setting.Clear,"0");
            editor.commit();
        }
    }

    private void Type_change() {
        if(type_voice==false) {
            main_word.setVisibility(View.GONE);
            main_voice.setVisibility(View.VISIBLE);
            type_voice = true;
            type_change.setImageResource(R.drawable.ic_word);
        } else {
            main_word.setVisibility(View.VISIBLE);
            main_voice.setVisibility(View.GONE);
            type_voice = false;
            type_change.setImageResource(R.drawable.ic_voice);
        }
    }

    private void SendMsg() {
            String get_str=edit_text.getText().toString();
            if(get_str.length()>0) {
                Content_List.add(get_str);
                myAdapter.notifyDataSetChanged();
                edit_text.setText("");
                InputMethodManager imm = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
           }
    }

    private void solve_pop() {
        if(pop_up_now == false) {
            startPlayingWave();
            main_voice.setText(getString(R.string.Stop_Playing));
            pop_up.setVisibility(View.VISIBLE);
            pop_up_now = true;
        } else {
            stopPlayingWave();

            main_voice.setText(getString(R.string.Playing));
            pop_up.setVisibility(View.GONE);
            pop_up_now = false;
        }
    }

    private void setState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String option = prefs.getString(Setting.VOICE_CHOICE, "2");
        int choice = Integer.valueOf(option);
        switch (choice) {
            case 0:
                audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_LEFT);
                break;
            case 1:
                audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_RIGHT);
                break;
            case 2:
                audioService.setChannelOut(AudioOutConfig.CHANNEL_OUT_BOTH);
                break;
        };
        waveRate = prefs.getInt(Setting.HZ, 0)*1000;
        Log.d("Liar"," "+waveRate);
        Log.d("Liar",option);
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


    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message message){
            if(message.what== PCondition.AUDIO_PROCESSOR){

            }
        }
    }
}
