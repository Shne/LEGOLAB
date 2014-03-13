public class AlphaNormalizer {
	public float alpha;
	private float _val = Float.NaN;

	public float get() {
		return _val;
	}

	public void set(float val) {
		if (Float.isNaN(_val)) {
			_val = val;
		} else {
			_val = _val * (1.0f - alpha) + alpha * val;
		}
	}
	
	public float handle(float val)
	{
		set(val);
		return get();
	}

}
