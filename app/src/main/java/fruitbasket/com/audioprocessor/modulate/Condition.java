package fruitbasket.com.audioprocessor.modulate;

public class Condition {
    private static final Condition condition=new Condition();

    /*
    用于表示编码开始的标记
     */
    public static final int START=0;
    /*
    用于表示编码结束的标记
     */
    public static final int END=6;
    /*
	支持声音编码的字符表
	 */
    public static final String CHAR_BOOK="12345";
    /*
    与字符表对应的声波频率编码表，其中第一个和最后一个元素分别和开始标记和结束标记对应
     */
    public static final short[] WAVE_RATE_BOOK=new short[]{1422,1575, 1764, 2004, 2321, 2940,4410};

    private Condition(){}

    public Condition getInstance(){
        return condition;
    }
}
