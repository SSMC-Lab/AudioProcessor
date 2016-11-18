package fruitbasket.com.audioprocessor.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

import fruitbasket.com.audioprocessor.process.fft.STFT;

/**
 * 用于检测声音信息的任务。
 * 它将识别声音包含的主要频率，来得出声音信息
 * Created by FruitBasket on 07/11/2016.
 */
final class RecognitionTask implements Runnable {
    private static final  String TAG=RecognitionTask.class.getName();

    private Decoder decoder;
    private Handler handler;//赋予这个任务更新用户界面的能力

    public RecognitionTask(Decoder decoder,Handler handler){
        this.decoder=decoder;
        this.handler=handler;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        Log.i(TAG, "run()");
        String decodeString= null;

        try {
            decodeString = decoder.decodeString();///
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Log.i(TAG,"begin to put the decodeString to the screen");
        if(handler!=null){
            Message message = new Message();
            message.what= PCondition.AUDIO_PROCESSOR;

            Bundle bundle = new Bundle();
            bundle.putString(PCondition.KEY_RECOGNIZE_STRING,decodeString);
            message.setData(bundle);
            handler.sendMessage(message);
        }
        else {
            Log.w(TAG, "handler==null");
        }
    }





    /*private static final int INVALID_INDEX=-1;
    *//*
    指定声音检测的最大次数
     *//*
    private static final int MAX_DECTECTION=15;

    private STFT stft;
    private short[] audioData;//存放音频数据

    private ArrayList<Integer> temIndexs;//存放当前的声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
    private int[] decodeIndexs;//存放最优的声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
    private String decodeString;//存放最优的声音信息识别结果，以字符表示。

    private boolean hasUpdateData;//指示是否更新了音频数据
    private Handler handler;//赋予这个任务更新用户界面的能力

    RecognitionTask(){
        stft=new STFT(STFT.FFT_LENGTH_1024);
        temIndexs=new ArrayList<>();
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return handler;
    }

    public void updateAudioData(short[] audioData){
        if(audioData!=null){
            //注意，这里使用的是浅复制。这意味着在本类的外部修改参数，会影响本类内部的数据
            this.audioData=audioData;
            hasUpdateData=true;
        }
        else{
            Log.w(TAG,"setAudioData(): audioData==null");
        }
    }

    @Override
    public void run() {
        Log.i(TAG,"run()");
        int index;//表示声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
        int detectionCounter=0;

        while(detectionCounter<MAX_DECTECTION){
            while(hasUpdateData==false){
                continue;///这里应该使用更先进的处理方法
            }
            hasUpdateData=false;

            stft.feedData(audioData);
            stft.getSpectrumAmp();
            stft.calculatePeak();

            index=indexOfFrequency((int)stft.maxAmpFreq);
            if(index!=INVALID_INDEX){
                if(index==PCondition.END_INDEX){
                    Log.i(TAG,"index==PCondition.END_INDEX");
                    if(temIndexs.isEmpty()==false){
                        Log.i(TAG,"temIndexs.isEmpty()==false");
                        ///去除多余的字符。这里使用的方法有待改进
                        Log.i(TAG,"before remove useless chars, temIndexs="+stringOfIndexs(toArray(temIndexs)));
                        int i;
                        ListIterator<Integer> listIterator=temIndexs.listIterator(temIndexs.size());
                        while(listIterator.hasPrevious()){
                            i=listIterator.previousIndex();
                            if(listIterator.previous()==PCondition.START_INDEX){
                                temIndexs.remove(i);///应该改用LinkedListArray
                                while(listIterator.hasPrevious()){
                                    temIndexs.remove(listIterator.previousIndex());
                                }
                                break;
                            }
                        }
                        Log.i(TAG,"after it, temIndexs="+stringOfIndexs(toArray(temIndexs)));

                        Log.i(TAG,"before merge information, decodeIndexs="+stringOfIndexs(decodeIndexs));
                        decodeIndexs=merge(decodeIndexs,toArray(temIndexs));
                        Log.i(TAG,"after it, decodeIndexs="+stringOfIndexs(decodeIndexs));
                        temIndexs.clear();
                    }
                }
                else if(index==PCondition.START_INDEX){
                    Log.i(TAG,"index==PCondition.START_INDEX");
                    if(temIndexs.isEmpty()){
                        Log.i(TAG,"temIndexs.isEmpty0==ture");
                        temIndexs.add(index);
                    }
                    else{
                        ///去除多余的字符。这里使用的方法有待改进
                        Log.i(TAG,"before remove useless chars, temIndexs="+stringOfIndexs(toArray(temIndexs)));
                        int i;
                        ListIterator<Integer> listIterator=temIndexs.listIterator(temIndexs.size());
                        while(listIterator.hasPrevious()){
                            i=listIterator.previousIndex();
                            if(listIterator.previous()==PCondition.START_INDEX){
                                temIndexs.remove(i);
                                while(listIterator.hasPrevious()){
                                    temIndexs.remove(listIterator.previousIndex());
                                }
                            }
                        }
                        Log.i(TAG,"after it, temIndexs="+stringOfIndexs(toArray(temIndexs)));

                        Log.i(TAG,"before merge information, decodeIndexs="+stringOfIndexs(decodeIndexs));
                        decodeIndexs=merge(decodeIndexs,toArray(temIndexs));
                        Log.i(TAG,"after it, decodeIndexs="+stringOfIndexs(decodeIndexs));
                        temIndexs.clear();
                    }
                }
                else{
                    Log.i(TAG,"temIndexs.add("+PCondition.CHAR_BOOK.charAt(index-1)+");");
                    temIndexs.add(index);
                }
            }
            ++detectionCounter;
        }

        if(decodeIndexs==null){
            decodeIndexs=toArray(temIndexs);
        }
        temIndexs.clear();

        Log.i(TAG,"begin to show the result of the recognition");
        if(decodeIndexs!=null&&decodeIndexs.length>0){
            decodeString=stringOfIndexs(decodeIndexs);
            Log.i(TAG,"decodeString="+decodeString);
            if(handler!=null){
                Message message = new Message();
                message.what= PCondition.AUDIO_PROCESSOR;

                Bundle bundle = new Bundle();
                bundle.putInt(PCondition.KEY_FREQUENCY,(int)stft.maxAmpFreq);
                bundle.putString(PCondition.KEY_RECOGNIZE_STRING,decodeString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            else{
                Log.w(TAG,"handler==null");
            }
        }
        else{
            Log.w(TAG,"decodeIndexs==null || decodeIndexs.length<=0 : decode error");
        }

    }

    *//**
     *将声音信息融合在一起。
     *声音信息中的元素以PCondition.WAVE_BOOK对应的元素位置表示
     *目前这个方法尚不完善，有待改进
     * @param xArray 包含声音信息的数组
     * @param yArray 包含声音信息的数组
     * @return 返回一个数组，它包含了融合在一起的声音信息
     *//*
    private static int[] merge(int[] xArray,int[] yArray){
        //特殊情况的处理
        //1.参数同时不合法
        if(xArray==null&&yArray==null){
            return null;
        }
        //2.在参数数组长度小于2时，无法进行信息的合并
        if(yArray!=null&&yArray.length<2){
            return xArray;
        }
        else if(yArray!=null&&xArray.length<2){
            return yArray;
        }
        //3.如果两个参数相同
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

        ArrayList<Integer> resultArray=new ArrayList<>();
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
        return toArray(resultArray);
    }

    *//**
     * 根据频率，返回在PCondition.WAVE_BOOK对应的元素位置
     * @param frequency 频率
     * @return  对应的下标
     *//*
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
        Log.w(TAG,"i>=bookLength : frequency is invalid");
        return INVALID_INDEX;
    }

    *//**
     *  返回指定的字符串
     * @param indexs 声音信息识别结果，以在PCondition.WAVE_RATE_BOOK对应的元素位置表示
     * @return 指定的字符串
     *//*
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
                Log.w(TAG,"index==PCondition.START_INDEX || index=PCondition.END_INDEX");
            }
        }
        return tem.toString();
    }

    *//**
     * 进行ArrayList到int数组的转换。这里的转换方式有待改进
     * @param inArray
     * @return
     *//*
    private static int[] toArray(ArrayList inArray){
        int[] array=new int[inArray.size()];
        ListIterator<Integer> listIterator=inArray.listIterator();
        int i=0;
        while(listIterator.hasNext()){
            array[i++]=(Integer)listIterator.next();
        }
        return array;
    }*/
}
