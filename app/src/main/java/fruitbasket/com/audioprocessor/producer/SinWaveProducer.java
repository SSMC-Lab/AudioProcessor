package fruitbasket.com.audioprocessor.producer;

final public class SinWaveProducer {
	private static final SinWaveProducer sinWaveProducer=new SinWaveProducer();
	
	//the height of sin wave
	public static final int WAVE_RANGE = Short.MAX_VALUE;
	
	private SinWaveProducer(){}
	
	public SinWaveProducer getInstance(){
		return sinWaveProducer;
	}
	
	/**
	 * produces a series of  sin waves
	 * sinWave.length>=samplesSize
	 * @param sinWave sin wave
	 * @param sampleSizeEachWave : the samples size in each sin wave
	 * @return
	 */
	public static short[] sinWave(short[] sinWave,int sampleSizeEachWave){
		for(int i=0;i<sinWave.length;++i){
			sinWave[i]=(short) (
					Math.sin(
							2*Math.PI
							*((i % sampleSizeEachWave) * 1.00/sampleSizeEachWave))
					*WAVE_RANGE);
		}
		return sinWave;
	}
	
}
