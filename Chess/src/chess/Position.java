package chess;

/**
 * Class simply represeting a coordinate on the Chessboard Grid.
 * It contains a row and col index.
 * @author Dev Patel and Eric Chan
 *
 */
public class Position {
	/**
	 * Row index
	 */
	public int r;
	/**
	 * Col index
	 */
	public int c;

	/**
	 * create position with specific row and col combination
	 * @param r
	 * @param c
	 */
	public Position(int r, int c) {
		this.r = r;
		this.c = c;
	}

	/**
	 * Overriding hashcode so that we can search it in the lists/sets.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + r;
		return result;
	}

	/**
	 * overriding equals object gives us control on finding the same object
	 * in the list.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (c != other.c)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + r + ", " + c + ")";
	}
}