package fruitbasket.com.audioprocessor.process;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.waveProducer.WaveProducer;

/**
 * 将文本编码成一段声音信号
 * 编码方法：将不同的字符分配不同的声音频率。具体是，先将不同的字符分配不同的编码，然后将不同的编码分配不同的声波频率
 */
class Encoder {
    private static final String TAG="modulate.Encoder";

    /*
    给单个字符编码后，作为音频数据用作播放声音时，播放的时长。以毫秒为单位
     */
    public static final int DEFAULT_DURATION=100;

    private String text;
    private ArrayList<Integer> codes =new ArrayList<>();//存放字符在Condition.CHAR_BOOK中的编号，用于表示一段文本的编码。

    Encoder(String text){
        setText(text);
    }

    public void setText(String text){
        this.text=text;
    }

    /**
     * 获取文本对应的原始音频数据，这是pcm数据
     * @return
     */
    public short[][] getAudioData(){
        if(convertTextToCodes(text)){
            return convertCodesToWaveRate();
        }
        else{
            Log.e(TAG,"convertTextToCodes(text)==false");
            return null;
        }
    }

    /**
     * 将文本转成一段编码序列。这里采用编码字典的方法，即，对于一个字符，其编码就是，这个字符在编码表中的编码。
     * @param text 文本
     * @return true 如果转换成功
     */
    private boolean convertTextToCodes(String text){
        if (!TextUtils.isEmpty(text)) {
            codes.clear();
            final int textLength=text.length();
            int index;

            for(int i=0;i<textLength;++i){
                index= PCondition.CHAR_BOOK.indexOf(text.charAt(i));
                if(index>-1){
                    codes.add(index);
                }
                else{
                    Log.w(TAG,"index<=-1 : invalid char '"+text.charAt(i)+"'");
                    return false;
                }
            }
        }
        else{
            Log.w(TAG,"TextUtils.isEmpty(text)==true : text is empty");
            return false;
        }
        return true;
    }

    /**
     *  将编码序列转换成对应的声波频率
     * @return
     */
    private short[][] convertCodesToWaveRate(){
        Log.i(TAG,"convertCodesToWaveRate()");
        final int time=1000;

        short[][] data=new short[codes.size()+2][];

        data[0]=WaveProducer.getSinWave(
                PCondition.WAVE_RATE_BOOK[PCondition.START_INDEX],
                AppCondition.DEFAULE_SIMPLE_RATE,
                AppCondition.DEFAULE_SIMPLE_RATE/(time/DEFAULT_DURATION)
        );

        ListIterator<Integer> listIterator=codes.listIterator();
        int i=1;
        while(listIterator.hasNext()&&i<data.length-1){
            data[i++]=WaveProducer.getSinWave(
                    PCondition.WAVE_RATE_BOOK[listIterator.next()+1],
                    AppCondition.DEFAULE_SIMPLE_RATE,
                    AppCondition.DEFAULE_SIMPLE_RATE/(time/DEFAULT_DURATION)
            );
        }

        data[data.length-1]=WaveProducer.getSinWave(
                PCondition.WAVE_RATE_BOOK[PCondition.END_INDEX],
                AppCondition.DEFAULE_SIMPLE_RATE,
                AppCondition.DEFAULE_SIMPLE_RATE/(time/DEFAULT_DURATION)
        );
        return data;
    }
}
