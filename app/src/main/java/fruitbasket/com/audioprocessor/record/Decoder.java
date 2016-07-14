package fruitbasket.com.audioprocessor.record;

import android.os.Handler;

import java.util.Vector;

/**
 * 对收到的数字音频进行解码，成字符串
 * Created by wbin on 2016/7/13.
 */
public class Decoder extends Thread {
    private static final String TAG = Decoder.class.toString();

    private Vector<short[]> byteBuffer = new Vector<>();
    private Handler handler;    //用于向主线程更新界面，显示收到的字符串

    public Decoder(Handler handler) {
        this.handler = handler;
    }

    /**
     * 将新监听到的数字音频添加到byteBuffer
     *
     * @param sound AudioRecord收到的short数组
     */
    public synchronized void addByteBuffer(short[] sound) {
        if (sound.length > 0) {
            byteBuffer.add(sound);
        }
    }

    /**
     * 得到byteBuffer数组
     *
     * @return byteBuffer数组
     */
    public synchronized short[] getByteBuffer() {
        if (byteBuffer.size() > 0) {
            return this.byteBuffer.remove(0);
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            handlerByteBuffer(getByteBuffer());
            if (Thread.interrupted()) {
                break;
            }
        }
    }

    private void handlerByteBuffer(short[] audioData) {
        if(signalAvailable(audioData)){
            
        }
    }

    /**
     * 判断short[]数据是否可取，主要根据数据的峰数作为判断
     *
     * @param audioData short数组
     * @return 根据峰数的数量返回该数据是否可取
     */
    private boolean signalAvailable(short[] audioData) {
        if (audioData.length == 0) {
            return false;
        }

        int nPoints = 28;
        int nPart = audioData.length / nPoints;
        int nPeak = 0;
        int startIndex = 0;
        int index = 0;
        while (index < nPart) {
            nPeak += countPeaks(audioData, startIndex, startIndex + nPoints);
            startIndex += nPoints;
            index++;
            if (nPeak > 50) return true;
        }
        return false;
    }

    /**
     * 返回audioData数组数据里的峰数
     *
     * @param audioData  short数组
     * @param startIndex 截取short数组的某段开头
     * @param endIndex   截取short数组的某段结尾
     * @return
     */
    private int countPeaks(short[] audioData, int startIndex, int endIndex) {
        int signChangeCount = 0;
        int sign = 0;
        int maxCount = 0;
        int index = startIndex;
        do {
            short value = audioData[index];
            if (Math.abs(value) > 12000) maxCount++;
            if (sign == 0 && maxCount > 0) sign = value / Math.abs(value);
            boolean signChange = false;
            if (sign > 0 && value < 0) signChange = true;
            if (sign < 0 && value > 0) signChange = true;
            if (signChange && maxCount > 2) {
                signChangeCount++;
                sign = -sign;
            }
            index++;
        } while (index < endIndex);
        return signChangeCount;
    }
}
