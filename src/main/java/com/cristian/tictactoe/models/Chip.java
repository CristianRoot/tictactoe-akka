package com.cristian.tictactoe.models;

public enum Chip {
	O, X;

	public Chip reverse() {
		if (this.equals(O))
			return X;
		else
			return O;
	}
}
