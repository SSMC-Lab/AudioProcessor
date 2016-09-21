package fruitbasket.com.audioprocessor.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fruitbasket.com.audioprocessor.R;

/**
 * 利用声波发送文本，并接受该声波并解码为文本显示
 * Created by wbin on 2016/7/5.
 */
public class SendReceiveFragment extends Fragment {
    private EditText sendEt;    //发送输入文本框
    private Button sendBtn;     //发送按钮
    private TextView receiveTv; //接收文本框
    private Thread listenerThread;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                receiveTv.setText(msg.getData().getString("msg"));
            } else {
                receiveTv.setText("error");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_receive_fragment, container, false);
        init(view);
        //listenerThread = new ReceiveTextTask(handler);          //开启接收音频
        //listenerThread.start();
        return view;
    }

    /**
     * 初始化界面
     *
     * @param view view
     */
    private void init(View view) {
        sendEt = (EditText) view.findViewById(R.id.send_text);
        receiveTv = (TextView) view.findViewById(R.id.receive_text);

        sendBtn = (Button) view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = sendEt.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(getActivity(), "发送文本不能为空", Toast.LENGTH_SHORT).show();
                }
                //listenerThread = new ReceiveTextTask(handler);
                //listenerThread.start();

                SendTextTask sendTask = new SendTextTask(s);
                sendTask.execute();
            }
        });
    }
}
