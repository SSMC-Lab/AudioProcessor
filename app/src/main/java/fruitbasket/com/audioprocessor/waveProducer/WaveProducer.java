package fruitbasket.com.audioprocessor.waveProducer;

final public class WaveProducer {
	private static final String TAG=WaveProducer.class.toString();
	private static final WaveProducer WAVE_PRODUCER =new WaveProducer();

	private static final int SAMPLE_COUNT=10000; //生成的波的样本数量
	private static final int WAVE_COUNT=1000;//the wave count
	private static final short WAVE_RANGE=Short.MAX_VALUE;


	private WaveProducer(){}
	
	public WaveProducer getInstance(){
		return WAVE_PRODUCER;
	}

	/**
	 * 取得一段波
	 * @param waveType
	 * @param waveRate
	 * @param sampleRate
     * @return
     */
	public static short[] getWave(WaveType waveType,int waveRate,int sampleRate){
		return getWave(waveType,waveRate,sampleRate,SAMPLE_COUNT);
	}

	/**
	 *
	 * @param waveType 波的类型
	 * @param waveRate 波的频率
	 * @param sampleRate 发声设备的实际发声频率
	 * @param sampleCount
     * @return
     */
	public static short[] getWave(WaveType waveType,int waveRate,int sampleRate,int sampleCount){
		switch(waveType){
			case SIN:
				return getSinWave(waveRate,sampleRate,sampleCount);
			default:
		}
		return null;
	}

	/**
	 * 取得sin波
	 * @param waveRate
	 * @param sampleRate
     * @return
     */
	public static short[] getSinWave(int waveRate,int sampleRate){
		return getSinWave(waveRate,sampleRate,SAMPLE_COUNT);
	}

	/**
	 *
	 * @param waveRate 波的频率
	 * @param sampleRate 发声设备的实际发声频率
	 * @param sampleCount 生成的波的样本数量
     * @return
     */
	public static short[] getSinWave(int waveRate,int sampleRate,int sampleCount){
		short[] wave=new short[sampleCount];
		double sampleCountInWave=sampleRate /(double)waveRate;
		for(int i=0;i<wave.length;++i){
			wave[i]=(short) (WAVE_RANGE*
					Math.sin(2.0 * Math.PI * i / (sampleCountInWave))
			);
		}
		return wave;
	}
}
