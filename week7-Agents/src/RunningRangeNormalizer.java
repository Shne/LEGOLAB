public class RunningRangeNormalizer extends RangeNormalizer {
	{
		high = Float.NEGATIVE_INFINITY;
		low = Float.POSITIVE_INFINITY;
	}
	
	@Override
	public void set(float val){
		if(val > high)
			high = val;
		if(val < low)
			low = val;
		super.set(val);
	}
	
	public float handle(float val)
	{
		set(val);
		return get();
	}
}
