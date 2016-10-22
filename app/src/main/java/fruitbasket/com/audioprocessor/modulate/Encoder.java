package fruitbasket.com.audioprocessor.modulate;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import fruitbasket.com.audioprocessor.AppCondition;
import fruitbasket.com.audioprocessor.waveProducer.WaveProducer;

/**
 * 将文本编码成一段声音信号
 * 编码方法：将不同的字符分配不同的声音频率。具体是，先将不同的字符分配不同的编码，然后将不同的编码分配不同的声波频率
 */
public class Encoder {
    private static final String TAG="modulate.Encoder";

    /*
    给单个字符编码后，作为音频数据用作播放声音时，播放的时长。以毫秒为单位
     */
    private static final int DEFAULT_DURATION=100;

    private String text;
    private ArrayList<Integer> codes =new ArrayList<>();//存放字符在Condition.CHAR_BOOK中的编号，用于表示一段文本的编码。

    public Encoder(String text){
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
        boolean state=true;
        if (!TextUtils.isEmpty(text)) {
            codes.clear();
            codes.add(ModulateCondition.START);//插入编码的开始标记
            int textLength=text.length();

            for(int i=0;i<textLength;++i){
                int index= ModulateCondition.CHAR_BOOK.indexOf(text.charAt(i));
                if(index>-1){
                    codes.add(index+1);//index+1是因为声波频率编码表中第0个元素已经被用作开头标记
                    Log.i(TAG,"convertTextToCodes(): index+1=="+(index+1));
                }
                else{ //如果text含有不存在CHAR_BOOK中的字符
                    state=false;
                    break;
                }
            }

            if(state){
                codes.add(ModulateCondition.END);//插入编码的结束标记
            }
        }
        Log.i(TAG,"convertTextToCodes(): codes.size()=="+codes.size());
        return state;
    }

    /**
     *  将编码序列转换成对应的声波频率
     * @return
     */
    private short[][] convertCodesToWaveRate(){
        short[][] data=new short[codes.size()][];
        for(int index:codes){
            data[index]=WaveProducer.getSinWave(
                    ModulateCondition.WAVE_RATE_BOOK[index],
                    AppCondition.DEFAULE_SIMPLE_RATE,
                    AppCondition.DEFAULE_SIMPLE_RATE/(1000/DEFAULT_DURATION)
            );
        }
        return data;
    }
}
