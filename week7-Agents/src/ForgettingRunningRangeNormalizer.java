public class ForgettingRunningRangeNormalizer extends RunningRangeNormalizer {
	public float ffactor;

	public float get() {
		return super.get();
	}

	public void set(float val) {
		float nhigh = (1.0f-ffactor)*high+ffactor*low;
		float nlow =(1.0f-ffactor)*low+ffactor*high;
		if(Float.isNaN(nhigh))
			nhigh = Float.NEGATIVE_INFINITY;
		if(Float.isNaN(nlow))
			nlow = Float.POSITIVE_INFINITY;
		high = nhigh;
		low = nlow;
		super.set(val);
	}
}
