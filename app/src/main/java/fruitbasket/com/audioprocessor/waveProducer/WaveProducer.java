package fruitbasket.com.audioprocessor.waveProducer;

final public class WaveProducer {
	private static final String TAG=WaveProducer.class.toString();
	private static final WaveProducer waveProducer =new WaveProducer();

	private static final int DEDAULT_SAMPLE_COUNT =10000; //生成的波的样本数量
	private static final short WAVE_RANGE=Short.MAX_VALUE;


	private WaveProducer(){}
	
	public static WaveProducer getInstance(){
		return waveProducer;
	}

	/**
	 *  取得一段波
	 * @param waveType 波的类型
	 * @param waveRate 波的频率
	 * @param sampleRate 发声设备的实际发声频率
	 * @param sampleCount 生成的波的样本数量
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
	 *
	 * @param waveRate 波的频率
	 * @param sampleRate 发声设备的实际发声频率
	 * @param sampleCount 生成的波的样本数量
     * @return
     */
	public static short[] getSinWave(int waveRate,int sampleRate,int sampleCount){
		short[] wave=new short[sampleCount];
		double sampleCountInWave=sampleRate /(double)waveRate;//每一个Sin波中，包含的样本点数量
		for(int i=0;i<wave.length;++i){
			wave[i]=(short) (WAVE_RANGE*
					Math.sin(2.0 * Math.PI * i / (sampleCountInWave))
			);
		}
		return wave;
	}
}
