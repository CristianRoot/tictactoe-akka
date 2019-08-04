package com.cristian.tictactoe.models;

public class PathQuality {

	private final int pathCount;
	private final int winCount;
	private final int tieCount;
	private final int lostCount;
	private final double lostPercent;

	public PathQuality(int pathCount, int winCount, int tieCount, int lostCount) {
		this.pathCount = pathCount;
		this.winCount = winCount;
		this.tieCount = tieCount;
		this.lostCount = lostCount;
		this.lostPercent = (this.lostCount / (double) pathCount) * 100;
	}

	public PathQuality(GameResult gameResult) {
		this.pathCount = 1;
		this.winCount = gameResult.equals(GameResult.WIN) ? 1 : 0;
		this.tieCount = gameResult.equals(GameResult.TIE) ? 1 : 0;
		this.lostCount = gameResult.equals(GameResult.LOST) ? 1 : 0;
		this.lostPercent = (this.lostCount / (double) pathCount) * 100;
	}

	public int getPathCount() {
		return pathCount;
	}

	public double getLostPercent() {
		return lostPercent;
	}

	public int getWinCount() {
		return winCount;
	}

	public int getTieCount() {
		return tieCount;
	}

	public int getLostCount() {
		return lostCount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PathQuality that = (PathQuality) o;
		return pathCount == that.pathCount &&
		       winCount == that.winCount &&
		       tieCount == that.tieCount &&
		       lostCount == that.lostCount;
	}

}
