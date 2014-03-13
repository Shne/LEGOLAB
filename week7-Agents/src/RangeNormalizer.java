public class RangeNormalizer {
	public float low = Float.NaN;
	public float high = Float.NaN;
	private float _val = Float.NaN;

	public float get() {
		return _val;
	}

	public void set(float val) {
		_val = (val - low)/(high-low)*100f;
	}

}
