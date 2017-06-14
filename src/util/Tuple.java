package util;

public class Tuple<L,R> {
	public L left;
	public R right;
	
	public Tuple(L l, R r) {
		left = l;
		right = r;
	}
	
	public Tuple() {
		this(null, null);
	}
}
