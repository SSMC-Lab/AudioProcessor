package fruitbasket.com.audioprocessor.play;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import fruitbasket.com.audioprocessor.record.ReceiveTextTask;

public class SendTextTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = SendTextTask.class.toString();
    private String s;   //要发送的文本

    public SendTextTask() {
        s = "nothing";
    }

    public SendTextTask(String s) {
        this.s = s;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int length = s.length();
        int dis = 4;       //每次间隔4
        int end = length > dis ? dis : length;
        for (int start = 0; start < length; start += dis) {
            byte[] packet = convertData(start, end);     //将其中一段转成byte数组
            sendData(packet);
            end = (end + dis) > length ? length : (end + dis);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            Thread.sleep(100);
            ReceiveTextTask.isRecording = false;
            Log.e(TAG, "play finished!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/*
    @Override
    public void run() {
        int length = s.length();
        int dis = 4;       //每次间隔4
        int end = length > dis ? dis : length;
        for (int start = 0; start < length; start += dis) {
            byte[] packet = convertData(start, end);     //将其中一段转成byte数组
            sendData(packet);
            end = (end + dis) > length ? length : (end + dis);
        }
    }
*/

    /**
     * 将文本s从start到end的一段转成由01组成的byte数组
     *
     * @param start 截取文本s其中一段的start
     * @param end   截取文本s其中一段的end
     * @return 由01组成的byte数组
     */
    private byte[] convertData(int start, int end) {
        String msg = s.substring(start, end);       //截取选取的一段
        byte[] data = msg.getBytes();

        byte[] payload = new byte[2 + data.length];     //前两个字节作为头信息
        payload[0] = (byte) 0xaf;           //第一个字节为标志
        payload[1] = (byte) data.length;    //第二个字节为该段文本长度
        for (int i = 0; i < data.length; i++) {
            payload[i + 2] = data[i];
        }

        byte[] packet = new byte[payload.length * 8];   //将payload转成由01组成的byte数组
        int cnt = 0;
        for (int i = 0; i < payload.length; i++) {
            for (int j = 7; j >= 0; j--) {
                packet[cnt++] = (byte) ((payload[i] & (1 << j)) == 0 ? 0 : 1);
            }
        }
        return packet;
    }

    /**
     * 根据packet数组播放音频
     *
     * @param packet 由01组成的byte数组
     */
    private void sendData(byte[] packet) {
        for (int i = 0; i < packet.length; i++) {
            Log.e(TAG, "packet." + i + "= " + packet[i]);
        }
        int lowFrequency = 3150;
        int highFrequency = 6300;
        int sbp = 4;
        double[] low = carrierWave(lowFrequency, sbp);      //低频率的波，代表0
        double[] high = carrierWave(highFrequency, sbp);    //高频率的波，代表1

        double[] msg = new double[packet.length * low.length];  //将01数组转成波
        int cnt = 0;
        for (int i = 0; i < packet.length; i++) {
            if (packet[i] == 1) {
                for (int j = 0; j < high.length; j++) {
                    msg[cnt++] = high[j];
                }
            } else {
                for (int j = 0; j < low.length; j++) {
                    msg[cnt++] = low[j];
                }
            }
        }

        ///double转short short数组长度应为double4倍才能不丢失精度？
        short[] sound = new short[msg.length];      //需转成short数组方能播放，由于msg数组值都在0到1之间，乘上相关系数放大
        for (int i = 0; i < msg.length; i++) {
            sound[i] = (short) (msg[i] * Short.MAX_VALUE);
           // Log.e(TAG, i + " " + sound[i]);
        }

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                44100, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, sound.length,
                AudioTrack.MODE_STATIC);
        audioTrack.setNotificationMarkerPosition(sound.length);
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack audioTrack) {
                Log.e(TAG, "play finished!");
                ReceiveTextTask.isRecording = false;
            }

            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {

            }
        });
        audioTrack.write(sound, 0, sound.length);
        audioTrack.play();
    }


    /**
     * 一定频率的正弦波
     *
     * @param frequency 特定频率
     * @param sbp
     * @return
     */
    private double[] carrierWave(int frequency, int sbp) {
        int AUDIO_SAMPLE_FREQ = 44100;
        int samplesOne = 28 * sbp;  //slowest freq=1.575 kHz -> 14peak = 28 samples per
        double[] wave = new double[samplesOne];
        for (int i = 0; i < samplesOne; ++i) {
            wave[i] = Math.sin(2 * Math.PI * i / (AUDIO_SAMPLE_FREQ / frequency));
        }
        return wave;
    }
}
