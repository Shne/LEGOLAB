public class DoubleAlphaNormalizer {
	AlphaNormalizer alpha1;
	AlphaNormalizer alpha2;

	public DoubleAlphaNormalizer(final float a1, final float a2) {
		alpha1 = new AlphaNormalizer() {
			{
				alpha = a1;
			}
		};
		alpha2 = new AlphaNormalizer() {
			{
				alpha = a2;
			}
		};
	}
	
	public float get()
	{
		return alpha1.get()/alpha2.get();
	}
	
	public void set(float val)
	{
		alpha1.set(val);
		alpha2.set(val);
	}
}
