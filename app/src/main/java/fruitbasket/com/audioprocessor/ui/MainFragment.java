package fruitbasket.com.audioprocessor.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import fruitbasket.com.audioprocessor.R;

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
    private List<String> Content_List=new ArrayList<>();
    private CardViewAdapter myAdapter;
    private RelativeLayout main_word;
    private Button main_voice;
    private LinearLayout pop_up;
    private boolean type_voice=false;
    private boolean pop_up_now=false;

    public MainFragment(){}

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
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_send : SendMsg();break;
            case R.id.main_type_change :Type_change();break;
            case R.id.main_voice:solve_pop();break;
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
            main_voice.setText("暂停播放");
            pop_up.setVisibility(View.VISIBLE);
            pop_up_now = true;
        } else {
            main_voice.setText("播放音频");
            pop_up.setVisibility(View.GONE);
            pop_up_now = false;
        }
    }
}
