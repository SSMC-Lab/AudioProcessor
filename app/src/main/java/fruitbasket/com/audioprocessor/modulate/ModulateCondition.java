package fruitbasket.com.audioprocessor.modulate;

final public class ModulateCondition {
    private static final ModulateCondition condition=new ModulateCondition();

    /*
    用于表示编码开始的标记
     */
    static final int START=0;
    /*
    用于表示编码结束的标记
     */
    static final int END=6;
    /*
	支持声音编码的字符表。目前的这个字符表仍在测试中
	 */
    static final String CHAR_BOOK="12345";
    /*
    与字符表对应的声波频率编码表，其中第一个和最后一个元素分别和开始标记和结束标记对应
     */
    static final short[] WAVE_RATE_BOOK=new short[]{1422,1575, 1764, 2004, 2321, 2940,4410};
    /*
   Handler消息号码。表示要进行音频处理的消息
    */
    public static final int AUDIO_PROCESSOR=0x1;
    /*
    Bundler关键字。表示检测到的频率
     */
    public static final String KEY_FREQUENCY="key_frequency";
    /*
    Bundler关键字。表示检测到的字符
     */
    public static final String KEY_RECOGNIZE_CHAR ="key_recognize_char";

    private ModulateCondition(){}

    public ModulateCondition getInstance(){
        return condition;
    }
}
