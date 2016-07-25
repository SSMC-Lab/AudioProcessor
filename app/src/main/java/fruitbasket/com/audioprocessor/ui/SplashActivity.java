package fruitbasket.com.audioprocessor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import fruitbasket.com.audioprocessor.R;

/**
 * Created by dell on 2016/7/8.
 */
public class SplashActivity extends Activity{

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000: goHome();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Message msg = new Message();
        msg.what=1000;
        mHandler.sendMessageDelayed(msg,3000);
    }

    void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}
