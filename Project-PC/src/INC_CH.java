import java.io.*;
import java.util.*;


public class INC_CH {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static List<NumberedPoint> convexHull_INC_CH(List<NumberedPoint> readPoints) {
		Collections.sort(readPoints);
		//ArrayList<NumberedPoint> LUpper = new ArrayList<NumberedPoint>();
		/*LUpper.add(readPoints.get(0));
		LUpper.add(readPoints.get(1));
		for(int i = 2; i<readPoints.size();i++){
			LUpper.add(readPoints.get(i));
			while(LUpper.size()>2&&!rightTurn(LUpper.get(LUpper.size()-3),LUpper.get(LUpper.size()-2),LUpper.get(LUpper.size()-1))){
				LUpper.remove(LUpper.size()-2);
			}
		}
		ArrayList<NumberedPoint> LLower = new ArrayList<NumberedPoint>();
		LLower.add(readPoints.get(readPoints.size()-1));
		LLower.add(readPoints.get(readPoints.size()-2));
		for(int i = readPoints.size()-3; i>=0;i--){
			LLower.add(readPoints.get(i));
			while(LLower.size()>2&&!rightTurn(LLower.get(LLower.size()-3),LLower.get(LLower.size()-2),LLower.get(LLower.size()-1))){
				LLower.remove(LLower.size()-2);
			}
		}*/
		List<NumberedPoint> LUpper = Hull(readPoints);
		Collections.reverse(readPoints);
		List<NumberedPoint> LLower = Hull(readPoints);
		LLower.remove(0);
		LLower.remove(LLower.size()-1);
		LUpper.addAll(LLower);
		return LUpper;
	}
	
	private static  List<NumberedPoint> Hull(List<NumberedPoint> input){
		ArrayList<NumberedPoint> res = new ArrayList<NumberedPoint>();
		res.add(input.get(0));
		res.add(input.get(1));
		for(int i = 2; i<input.size();i++){
			res.add(input.get(i));
			while(res.size()>2&&!rightTurn(res.get(res.size()-3),res.get(res.size()-2),res.get(res.size()-1))){
				res.remove(res.size()-2);
			}
		}
		return res;
	}
	
	private static boolean rightTurn(NumberedPoint p1, NumberedPoint p2, NumberedPoint p3) {
		return (p2.y-p1.y)/(p2.x-p1.x)>(p3.y-p2.y)/(p3.x-p2.x);
	}

}
