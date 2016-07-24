package fruitbasket.com.audioprocessor.ui;

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
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.List;

import fruitbasket.com.audioprocessor.R;

/**
 * Created by Study on 21/06/2016.
 */
public class MainFragment extends Fragment
        implements  SwipeRefreshLayout.OnRefreshListener , View.OnClickListener{
    private Toolbar toolbar;
    private Button send;
    private Button file;
    private EditText edit_text;
    private RecyclerView mRecyclerView;
    private List<String> Content_List=new ArrayList<>();
    private CardViewAdapter myAdapter;
    private SwipeRefreshLayout swipeLayout;

    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.main_fragment,container,false);
        send = (Button) rootView.findViewById(R.id.main_send);
        send.setOnClickListener(this);

        file = (Button) rootView.findViewById(R.id.main_file_bottom);
        file.setOnClickListener(this);

        edit_text = (EditText) rootView.findViewById(R.id.main_text);

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

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.main_swipe_refresh);
        swipeLayout.setOnRefreshListener(MainFragment.this);
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
            case R.id.main_send : Toast.makeText(this.getContext(),"send",Toast.LENGTH_SHORT).show();Content_List.add("test");
                myAdapter.notifyDataSetChanged();break;
            case R.id.main_file_bottom :break;
        }
    }

    public void onRefresh() {
        //DO SOMETHING
        Toast.makeText(this.getContext(),"refresh",Toast.LENGTH_SHORT).show();
        swipeLayout.setRefreshing(false);
    }
}
