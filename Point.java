package me.alexng.untitled.generate;

class Point {
	// X and y are the real 'world' points, index x and y are index positions.
	private int x, y, indexX, indexY;

	public Point(int x, int y, int indexX, int indexY) {
		this.x = x;
		this.y = y;
		this.indexX = indexX;
		this.indexY = indexY;
	}

	public Point left() {
		return move(-1, 0);
	}

	public Point right() {
		return move(1, 0);
	}

	public Point up() {
		return move(0, -1);
	}

	public Point down() {
		return move(0, 1);
	}

	private Point move(int dx, int dy) {
		return new Point(x + dx, y + dy, indexX + dx, indexY + dy);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getIndexX() {
		return indexX;
	}

	public int getIndexY() {
		return indexY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		return x == point.x;
	}

	@Override
	public int hashCode() {
		// TODO: Not sure if indexX is correct here
		// https://en.wikipedia.org/wiki/Pairing_function#
		return (int) (0.5 * (indexX + indexY) * (indexX + indexY + 1) + indexY);
	}
}
