package fruitbasket.com.audioprocessor.play;

/**
 * Created by Study on 27/06/2016.
 * 用于描述音频输出（播放音频）的目标位置
 */
 public class AudioOutConfig {

    public static final int CHANNEL_OUT_LEFT=0x1;//只输出到耳机的左边
    public static final int CHANNEL_OUT_RIGHT=0x2;//只输出到耳机的右边
    public static final int CHANNEL_OUT_BOTH=0x3;//输出到耳机的两边

    private int channelOut;

    public AudioOutConfig(){
        this.channelOut =CHANNEL_OUT_BOTH;
    }

    public AudioOutConfig(int channelOut){
        setChannelOut(channelOut);
    }

    public int getChannelOut() {
        return channelOut;
    }

    public void setChannelOut(int channelOut) {
        //确保this.channelOut的值合法
        switch(channelOut){
            case CHANNEL_OUT_LEFT:
            case CHANNEL_OUT_RIGHT:
            case CHANNEL_OUT_BOTH:
                this.channelOut = channelOut;
                break;
            default:
        }
    }
}
