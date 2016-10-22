package fruitbasket.com.audioprocessor.modulate;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 本类用于检测声音信号的频率。
 * 目前，在安静的环境下，频率的识别精度在+/- 3以内
 */
public class FrequencyDetector {

	private static final FrequencyDetector instance=new FrequencyDetector();
	
	private static final short THRESHOLD=20000;
	
	private FrequencyDetector(){}
	
	public FrequencyDetector getInstance(){
		return instance;
	}
	
	/**
	 * 根据对音频数据进行快速傅里叶变换的结果，筛选该音频包含的最主要的一个频率
	 * @param fftData
	 * @param sampleRate
	 * @return
	 */
	public static int  getFrequence(short[] fftData,int sampleRate){
		
		int halfLenght=fftData.length/2;
		int index=0;
		
		ArrayList<Integer> indexs=new ArrayList<>(50);
		
		int elementNumber=0;//记录有多少个连续的元素值大于阈值
		int[] selectIndexs=new int[5];//记录连续大于阈值的元素
		
		for(int i=0;i<halfLenght;i++){
			if(fftData[i]>=THRESHOLD){//如果元素的值大于或等于阈值
				if(elementNumber<=0){
					elementNumber=0;
					selectIndexs[elementNumber]=i;
					elementNumber++;
				}
				else if(elementNumber<selectIndexs.length){//如果连续的元素个数未到达上限
					selectIndexs[elementNumber]=i;
					elementNumber++;
				}
				else{//如果连续的元素个数到达上限
					indexs.add(averageFrom(selectIndexs));
					elementNumber=0;
					i--;
				}
			}
			else{//如果元素的值小于阈值
				if(elementNumber>2){
					indexs.add(averageFrom(selectIndexs,0,elementNumber-1));
					elementNumber=0;
				}
				else if(elementNumber>0&&elementNumber<=2){
					//这里暂时不处理太少连续的元素
					/*int value=averageFrom(selectIndexs,0,elementNumber-1);
					int currentIndex=averageFrom(indexs);

					if(currentIndex==0
							||(currentIndex>0
							&&value<currentIndex+10
							&&value>currentIndex)){
						System.out.println("value<currentIndex+10 && value>currentIndex");
						indexs.add(value);
					}*/
					
					elementNumber=0;
				}
			}
		}

		if(indexs.size()>0){
			index=averageFrom(indexs);
		}

		int frequence=(int)(((double)sampleRate/fftData.length)*index);
		return frequence;
	}

	/**
	 *
	 * @param arrayList
	 * @param startIndex
	 * @param endIndex
     */
	private static void removeNoise(ArrayList<Integer> arrayList,int startIndex,int endIndex){
		
	}

	public static int averageFrom(ArrayList<Integer> arrayList){
		int size=arrayList.size();
		if(size>0){
			int sum=0;
			ListIterator<Integer> listIterator=arrayList.listIterator();
			while(listIterator.hasNext()){
				sum=sum+listIterator.next();
			}
			return (sum/arrayList.size());
		}
		else{
			return 0;
		}
	}

	private static int averageFrom(int array[]){
		int sum=0;
		for(int i=0;i<array.length;++i){
			sum+=array[i];
		}
		return (sum/array.length);
	}
	
	/**
	 * 计算数组中制定区间的元素的平均值
	 * @param array
	 * @param startIndex	开始下标
	 * @param endIndex		结束下标
	 * @return
	 */
	private static int averageFrom(int array[],int startIndex,int endIndex){
		int sum=0;
		if(startIndex>=0
				&&endIndex<array.length
				&&startIndex<=endIndex){
			for(int i=startIndex;i<=endIndex;++i){
				sum+=array[i];
			}
		}
		else{
			return 0;
		}
		return (sum/(endIndex-startIndex+1));
	}
}
