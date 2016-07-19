package fruitbasket.com.audioprocessor.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

/**
 * 以音频的方式接收一段文本
 * Created by wbin on 2016/7/8.
 */
public class ReceiveTextTask extends Thread {
    private static final String TAG = ReceiveTextTask.class.toString();
    private int frequency = 44100;    //采样频率
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;   //单声道
    //private int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;   //双声道
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;  //音频编码
    public static boolean isRecording = false;    //是否正在录制
    private Handler handler;        //用于更新界面的UI
    private Decoder decoder;

    public ReceiveTextTask(Handler handler) {
        this.handler = handler;
        decoder = new Decoder(handler);       //解码收听到的数字音频
        decoder.start();
    }

    @Override
    public void run() {
        Log.e(TAG, "start!");

        //取得最小缓存区
        int bufferSize = AudioRecord.getMinBufferSize(frequency,    //采样频率
                channelConfiguration,   //声道配置
                audioEncoding);         //音频编码
        short[] buffer = new short[bufferSize];
        //byte[] buffer = new byte[bufferSize];

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  //音频源
                frequency,              //采样频率
                channelConfiguration,   //声道配置
                audioEncoding,          //音频编码
                bufferSize);            //缓存区大小
        audioRecord.startRecording();   //开始录制

        isRecording = true;
        while (isRecording) {
            //使用read方法将原始音频数据添加到录制缓冲区中
            int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
            decoder.addByteBuffer(buffer);
            Log.e(TAG, "recording!");
            /*for (int i = 0; i < bufferReadResult; i++) {
                Log.e(TAG, "buffer." + i + "= " + buffer[i]);
            }*/
        }
    }
}
