package com.cristian.tictactoe.models;

import java.util.List;

public class Path {
	private Coordinate coordinate;
	private PathQuality pathQuality;

	public Path(Coordinate coordinate, PathQuality pathQuality) {
		this.coordinate = coordinate;
		this.pathQuality = pathQuality;
	}

	public Path(Coordinate coordinate, List<PathQuality> pathQualityList) {
		this.coordinate = coordinate;
		this.pathQuality = new PathQuality(pathQualityList.stream().mapToInt(PathQuality::getPathCount).sum(),
										   pathQualityList.stream().mapToInt(PathQuality::getWinCount).sum(),
										   pathQualityList.stream().mapToInt(PathQuality::getTieCount).sum(),
										   pathQualityList.stream().mapToInt(PathQuality::getLostCount).sum());
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public PathQuality getPathQuality() {
		return pathQuality;
	}
}
