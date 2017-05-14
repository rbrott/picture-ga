package genetics.picture;

public enum Mutation {
	COLOR_CHANGE,
	VERTEX_CHANGE,
	SWAP;
	static Mutation random() {
		return Mutation.values()[(int) (3 * Math.random())];
	}
}