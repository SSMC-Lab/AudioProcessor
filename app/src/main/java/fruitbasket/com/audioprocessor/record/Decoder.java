package fruitbasket.com.audioprocessor.record;

import android.os.Handler;

import java.util.Arrays;
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

    /**
     * 处理数据
     *
     * @param audioData
     */
    private void handlerByteBuffer(short[] audioData) {
        if (signalAvailable(audioData)) {
            int[] nPeaks = processSound(audioData);
            int[] bits = parseBits(nPeaks);
            String str = "";
            for (int i = 0; i < bits.length; i++) {
                if (bits[i] == 2) {
                    str = str + "1";
                } else if (bits[i] == 1) {
                    str = str + "0";
                }
            }
            handleData(str);
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

        int nPoints = 14;
        int nPart = audioData.length / nPoints;
        int nPeak = 0;
        int startIndex = 0;
        int index = 0;
        while (index < nPart) {
            nPeak += countPeaks(audioData, startIndex, startIndex + nPoints);
            startIndex += nPoints;
            index++;
            if (nPeak > 50) return true;    ///>=64？ >=192
        }
        return false;
    }

    /**
     * 将音频的short[]转成代表峰数的int[]
     *
     * @param audioData short[]
     * @return 代表峰数的int[]
     */
    private int[] processSound(short[] audioData) {
        int nPoints = 14;
        int nParts = audioData.length / nPoints;
        int[] nPeaks = new int[nParts];
        int startIndex = 0;
        int peakCount = 0;
        int i = 0;
        do {
            int endIndex = startIndex + nPoints;
            nPeaks[i] = countPeaks(audioData, startIndex, endIndex);
            peakCount += nPeaks[i];
            i++;
            startIndex = endIndex;
        } while (i < nParts);
        if (peakCount < 50) {
            nPeaks = new int[0];
        }
        return nPeaks;
    }

    /**
     * 返回audioData数组数据里的峰数
     *
     * @param audioData  short数组
     * @param startIndex 截取short数组的某段开头
     * @param endIndex   截取short数组的某段结尾
     * @return audioData数组数据里的峰数
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
                ///maxCount=0;  是否该清空？
            }
            index++;
        } while (index < endIndex);
        return signChangeCount;
    }

    /**
     * from the number of peaks array decode into an array of bits (2=bit-1, 1=bit-0, 0=no bit)
     *
     * @param peaks short[]
     * @return
     */
    private int[] parseBits(int[] peaks) {
        int nBits = peaks.length / 8;
        int[] bits = new int[nBits];
        Arrays.fill(bits, 0);   //默认为0
        int i = findNextNonZero(peaks, 0);
        if (i + 8 >= peaks.length) {
            return bits;
        }
        do {
            int nPeaks = 0;
            for (int j = 0; j < 8; j++) {
                nPeaks += peaks[i + j];
            }
            int position = i / 8;
            bits[position] = 0;

            if (nPeaks >= 12) {     ///8?
                bits[position] = 1;
            }
            if (nPeaks >= 30) {     ///16?
                bits[position] = 2;
            }

            i = i + 8;

        } while (8 + i < peaks.length);
        return bits;
    }

    /**
     * 返回int[]里从startIndex开始的非零位置
     *
     * @param peaks      代表峰数的int[]
     * @param startIndex 开始位置
     * @return int[]里从startIndex开始的非零位置
     */
    private int findNextNonZero(int[] peaks, int startIndex) {
        int index = startIndex;
        int value = 0;
        while (value == 0 && index < peaks.length) {
            value = peaks[index++];
        }
        return index;

        /*int index = startIndex;
        int value = 1;
        do {
            value = peaks[index];
            index++;
        } while (value==0 && index<peaks.length-1);
        return index-1;*/
    }

    private void handleData(String s) {

    }
}
