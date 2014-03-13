public class MinpowerNormalizer {
	public float low = Float.NaN;
	public float high = Float.NaN;
	private float _val = Float.NaN;

	public float get() {
		return _val;
	}

	public void set(float val) {
		if (val > 1.0f)
			val = 1.0f;
		if (val < 0.0f)
			val = 0.0f;
		_val = low + (high - low) * val;
	}

}
