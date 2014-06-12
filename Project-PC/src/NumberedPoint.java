
public class NumberedPoint implements Comparable<NumberedPoint> {
	public NumberedPoint(double x, double y, int index) {
		super();
		this.x = x;
		this.y = y;
		this.index = index;
	}
	public double x,y;
	public int index;
	
	public boolean equals(Object aThat){
		  if ( this == aThat ) return true;
		  if ( !(aThat instanceof  NumberedPoint) ) return false;
		  NumberedPoint that = (NumberedPoint)aThat;
		  return
		    that.x == x &&
		    that.y == y &&
		    that.index == index;
		}

	public int compareTo(NumberedPoint o) {
		if(x>o.x) return 1;
		if(x<o.x) return -1;
		if(y>o.y) return 1;
		if(y<o.y) return -1;
		return index-o.index;
	}
	
	public String toString(){
		return "#"+index+":"+"("+x+","+y+")";
	}
	
	
}
