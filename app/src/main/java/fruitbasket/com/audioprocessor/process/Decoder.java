package fruitbasket.com.audioprocessor.process;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import fruitbasket.com.audioprocessor.process.fft.STFT;

/**
 *  将声音信号解码成文本。其解码方法与Encoder的编码方法对应
 * Created by FruitBasket on 2016/11/16.
 */

final class Decoder {
    private static final String TAG="process.Decoder";
    private static final int INVALID_INDEX=-1;
    /*
    指定声音检测的最大次数
     */
    private static final int MAX_DECTECTION=70;

    private STFT stft;

    private int updateCounter=0;
    private ArrayBlockingQueue<short[]> audioDataBuffer;

    private ArrayList<Integer> temIndexs;//存放当前的声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
    private int[][] decodeIndexs;//存放最优的一些声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
                                                    //按照由好到坏排序。好的放在前面，坏的放在后面
    private String decodeString;//存放最优的声音信息识别结果，以字符表示。


    public Decoder(){
        stft=new STFT(STFT.FFT_LENGTH_8192);
        audioDataBuffer=new ArrayBlockingQueue<short[]>(MAX_DECTECTION);
        decodeIndexs=new int[5][];
        temIndexs=new ArrayList<>();
    }

    public boolean updateAudioData(short[] audioData) throws InterruptedException {
        if(updateCounter<MAX_DECTECTION){
            ++updateCounter;
            audioDataBuffer.put(audioData);
            return true;
        }
        else{
            updateCounter=0;
            return false;
        }

    }

    /**
     * 解码音频数据得到信息，信息以字符串表示
     * @return 表示信息的字符串
     */
    public String decodeString() throws InterruptedException {
        int detectionCounter=0;
        while(detectionCounter<MAX_DECTECTION){
            stft.feedData(audioDataBuffer.take());//使用阻塞的方式取出语音数据；从队列中取出的元素值一定不会是null
            stft.getSpectrumAmp();
            stft.calculatePeak();

            int index = indexOfFrequency((int) stft.maxAmpFreq);//表示声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
            if (index != INVALID_INDEX) {
                if (index == PCondition.END_INDEX) {
                    if (temIndexs.isEmpty() == false) {
                        ///去除多余的字符。这里使用的方法有待改进
                        int i;
                        ListIterator<Integer> listIterator = temIndexs.listIterator(temIndexs.size());
                        while (listIterator.hasPrevious()) {
                            i = listIterator.previousIndex();
                            if (listIterator.previous() == PCondition.START_INDEX) {
                                temIndexs.remove(i);
                                while (listIterator.hasPrevious()) {
                                    temIndexs.remove(listIterator.previousIndex());
                                }
                                break;
                            }
                        }
                        storeBestResult(toArray(temIndexs));
                        temIndexs.clear();
                    }
                } else if (index == PCondition.START_INDEX) {
                    if (temIndexs.isEmpty()) {
                        temIndexs.add(index);
                    } else {
                        ///去除多余的字符。这里使用的方法有待改进
                        int i;
                        ListIterator<Integer> listIterator = temIndexs.listIterator(temIndexs.size());
                        while (listIterator.hasPrevious()) {
                            i = listIterator.previousIndex();
                            if (listIterator.previous() == PCondition.START_INDEX) {
                                temIndexs.remove(i);
                                while (listIterator.hasPrevious()) {
                                    temIndexs.remove(listIterator.previousIndex());
                                }
                            }
                        }

                        storeBestResult(toArray(temIndexs));
                        temIndexs.clear();
                    }
                } else {
                    Log.i(TAG, "temIndexs.add(" + PCondition.CHAR_BOOK.charAt(index - 1) + ");");
                    temIndexs.add(index);
                }
            }
            ++detectionCounter;
        }
        //进行最后的信息合并
        storeBestResult(toArray(temIndexs));

        /*
        结束上述处理后的结果：
        1.decodeIndexs得到正确的结果，temIndex有可能为空也有可能不空
        2.decodeIndexs[0]==null，但是temIndex！=null
        3.temIndexs==null&&decodeIndexs[0]==null
         */
        //分情况处理结果
        if(temIndexs==null&&decodeIndexs==null){
            Log.w(TAG,"temIndexs==null && decodeIndexs==null : recognition fail");
            return null;
        }

        if (decodeIndexs[0] == null) {
            decodeIndexs [0]= toArray(temIndexs);
        }

        temIndexs.clear();
        //decodeString = stringOfIndexs(decodeIndexs[0]);
        decodeString=getDecodeString();
        Log.i(TAG, "decodeString=" + decodeString);
        return decodeString;
    }

    /**
     * 存放最优的声音识别结果
     * 这里认为，识别的结果越长，就越优
     * @param array
     */
    private void storeBestResult(int[] array){
        Log.i(TAG,"storeBestResult()");
        if(array==null){
            return;
        }
        int i=0;
        while(i<decodeIndexs.length){
            if(decodeIndexs[i]==null){
                decodeIndexs[i]=array;
                break;
            }
            else if(array.length>decodeIndexs[i].length){
                int j=decodeIndexs.length-1;
                while(j>i){
                    decodeIndexs[j]=decodeIndexs[j-1];
                    j--;
                }
                decodeIndexs[i]=array;
                break;
            }
            ++i;
        }
    }

    /**
     * 根据最优的一些声音设备结果，取得最优的识别结果，并以字符串表示。
     * 此函数有待改进
     */
    private String getDecodeString(){
        Log.i(TAG,"getDecodeString()");
        for(int i=0;i<decodeIndexs.length;++i){
            Log.i(TAG,stringOfIndexs(decodeIndexs[i]));
        }
        ArrayList<Integer> bestResult=new ArrayList<>();

        int[] ptArray=new int[decodeIndexs.length];
        int [] sameCounter=new int[decodeIndexs.length];
        int[] sameNumberCounter=new int[decodeIndexs.length];

        for(int i=0;i<ptArray.length;++i){
            ptArray[i]=0;
        }

        //
        int maxElement=0;
        int maxElementIndex=0;
        int[] elements=new int[decodeIndexs[0].length];
        int pt=0;
        int i,j,k;
        for(i=0;i<decodeIndexs.length;++i){
            if(decodeIndexs[i]!=null){
                pt=0;
                elements[pt]=decodeIndexs[i][0];
                for(j=1;j<decodeIndexs[i].length;++j){

                    for(k=0;k<=pt;++k){
                        if(decodeIndexs[i][j]==elements[k]) {
                            break;
                        }
                    }
                    if(k>pt){
                        elements[++pt]=decodeIndexs[i][j];
                    }

                }
                if(maxElement<pt+1){
                    maxElement=pt+1;
                    maxElementIndex=i;
                }
            }
        }
        Log.i(TAG,"maxElementIndex=="+maxElementIndex);


        int charIndex=0;
        int max,maxIndex;
        while(ptArray[0]<decodeIndexs[0].length){
            //Log.i(TAG,"ptArray[0]=="+ptArray[0]);
            charIndex=decodeIndexs[maxElementIndex][ptArray[maxElementIndex]];//这里假设第一个数组包含所有的元素
            for (i = 0; i < decodeIndexs.length; ++i) {

                for (j = 0; j < sameCounter.length; ++j) {
                    sameCounter[i] = 0;
                }

                if(decodeIndexs[i]!=null
                    &&ptArray[i] < decodeIndexs[i].length
                    &&decodeIndexs[i][ptArray[i]]==charIndex){
                    sameCounter[i]=1;
                    while (ptArray[i] < decodeIndexs[i].length - 1
                        &&decodeIndexs[i][ptArray[i]] == decodeIndexs[i][ptArray[i] + 1]) {
                        ptArray[i]++;
                        sameCounter[i]++;
                    }
                    ptArray[i]++;
                }
            }
            ///
            for(i=0;i<sameCounter.length;++i){
                Log.i(TAG,"sameCounter["+i+"]=="+sameCounter[i]);
            }

            for(i=0;i<sameNumberCounter.length;++i){
                sameNumberCounter[i]=0;
            }
            for(int index:sameCounter){
                sameNumberCounter[index]++;
            }
            ///
            for(i=0;i<sameNumberCounter.length;++i){
                Log.i(TAG,"sameNumberCounter["+i+"]=="+sameNumberCounter[i]);
            }

            max=sameNumberCounter[1];
            maxIndex=1;
            for(i=2;i<sameNumberCounter.length;++i){
                if(max<sameNumberCounter[i]){
                    max=sameNumberCounter[i];
                    maxIndex=i;
                }
            }
            Log.i(TAG,"maxIndex=="+maxIndex);

            for(i=0;i<maxIndex;++i){
                bestResult.add(charIndex);
            }

        }

        return stringOfIndexs(toArray(bestResult));
    }

    /**
     *将声音信息融合在一起。
     *声音信息中的元素以PCondition.WAVE_BOOK对应的元素位置表示
     * @param xArray 包含声音信息的数组
     * @param yArray 包含声音信息的数组
     * @return 返回一个数组，它包含了融合在一起的声音信息
     */
    private static int[] merge(int[] xArray,int[] yArray){
        Log.i(TAG,"storeBestResult() : before storeBestResult : "+stringOfIndexs(xArray)+" ; "+stringOfIndexs(yArray)+" ;");
        //特殊情况的处理
        //1.
        if(xArray==null){
            Log.i(TAG,"storeBestResult() : after storeBestResult : "+stringOfIndexs(yArray)+" ;");
            return yArray;
        }
        else if(yArray==null){
            Log.i(TAG,"storeBestResult() : after storeBestResult : "+stringOfIndexs(xArray)+" ;");
            return xArray;
        }
        else if(yArray.length<2){
            Log.i(TAG,"storeBestResult() : after storeBestResult : "+stringOfIndexs(xArray)+" ;");
            return xArray;
        }
        else if(xArray.length<2){
            Log.i(TAG,"storeBestResult() : after storeBestResult : "+stringOfIndexs(yArray)+" ;");
            return yArray;
        }
        //2.如果两个参数相同
        if(xArray.length==yArray.length){
            int i;
            for(i=0;i<xArray.length;++i){
                if(xArray[i]!=yArray[i]){
                    break;
                }
            }
            if(i>=xArray.length){
                int[] resultArray=new int[xArray.length];
                System.arraycopy(xArray, 0, resultArray, 0, resultArray.length);
                return resultArray;
            }
        }
        return xArray.length>yArray.length? xArray:yArray;

        /*ArrayList<Integer> resultArray=new ArrayList<>();
        int xPt,yPt;//xArray和yArray的指针
        int xSameCounter,ySameCounter;//记录数组中连续连续相同元素的个数
        int maxSameCounter;//记录xSameCounter和ySameCounter的较大者;
        boolean isHasSame=false;//记录xArray中的指定元素是否与yArray中的指定元素相同

        for(xPt=0;xPt<xArray.length;++xPt){
            isHasSame=false;
            for(yPt=0;yPt<yArray.length;++yPt){
                if(xArray[xPt]==yArray[yPt]){
                    xSameCounter=1;
                    ySameCounter=1;
                    while(xPt+xSameCounter<xArray.length
                            &&xArray[xPt]==xArray[xPt+xSameCounter]){
                        xSameCounter++;
                    }
                    while(yPt+ySameCounter<yArray.length
                            &&yArray[yPt]==yArray[yPt+ySameCounter]){
                        ySameCounter++;
                    }
                    maxSameCounter=Math.max(xSameCounter, ySameCounter);
                    while(maxSameCounter>1){
                        resultArray.add(xArray[xPt]);///这里会遇到特殊情况
                        --maxSameCounter;
                    }
                    xPt+=(xSameCounter-1);
                    yPt+=(ySameCounter-1);

                    if(xPt==0&&yPt==1){//如果在开头处有信息可以合并
                        resultArray.add(yArray[0]);
                        resultArray.add(xArray[xPt]);
                    }
                    else if(xPt==xArray.length-1
                            &&yPt==yArray.length-2){//如果在结束处有信息可以合并
                        resultArray.add(xArray[xPt]);
                        resultArray.add(yArray[yPt+1]);
                    }
                    else if(yPt+2<yArray.length
                            &&xPt<xArray.length-1
                            &&xArray[xPt+1]==yArray[yPt+2]
                            &&yArray[yPt+1]!=yArray[yPt+2]){//如果在中间处有信息可以合并
                        resultArray.add(xArray[xPt]);
                        resultArray.add(yArray[yPt+1]);
                    }
                    else{
                        if(isHasSame==false){
                            resultArray.add(xArray[xPt]);
                        }
                    }
                    isHasSame=true;
                }

                if(yPt>=yArray.length-1&&isHasSame==false){
                    resultArray.add(xArray[xPt]);
                }
            }
        }
        Log.i(TAG,"storeBestResult() : after storeBestResult : "+stringOfIndexs(toArray(resultArray))+" ;");
        return toArray(resultArray);*/
    }

    /**
     * 根据频率，返回在PCondition.WAVE_BOOK对应的元素位置
     * @param frequency 频率
     * @return  对应的下标
     */
    private static int indexOfFrequency(int frequency){
        final int bookLength= PCondition.WAVE_RATE_BOOK.length;
        final int errorRange=5;
        int standard;

        for(int i=0;i<bookLength;++i){
            standard= PCondition.WAVE_RATE_BOOK[i];
            if(frequency>=standard-errorRange
                    &&frequency<=standard+errorRange){
                return i;
            }
        }
        //如果frequency无效
        Log.i(TAG,"i>=bookLength : frequency is invalid");
        return INVALID_INDEX;
    }

    /**
     *  返回指定的字符串
     * @param indexs 声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
     * @return 指定的字符串
     */
    private static String stringOfIndexs(int[] indexs){
        if(indexs==null){
            return null;
        }
        StringBuilder tem=new StringBuilder(indexs.length);
        for(int index:indexs){
            if(index!=PCondition.START_INDEX&&index!=PCondition.END_INDEX){
                tem.append(PCondition.CHAR_BOOK.charAt(index-1));
            }
            else{
                Log.w(TAG,"index==PCondition.START_INDEX || index=PCondition.END_INDEX : add wrong index to the recognition result");
            }
        }
        return tem.toString();
    }

    /**
     * 进行ArrayList到int数组的转换。这里的转换方式有待改进
     * @param inArray
     * @return
     */
    private static int[] toArray(ArrayList inArray){
        int[] array=new int[inArray.size()];
        ListIterator<Integer> listIterator=inArray.listIterator();
        int i=0;
        while(listIterator.hasNext()){
            array[i++]=(Integer)listIterator.next();
        }
        return array;
    }
}
