package com.cristian.tictactoe;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import com.cristian.tictactoe.models.*;
import com.cristian.tictactoe.models.Board.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathQualityQueryWorker extends AbstractLoggingActor {

	public static Props props(Board board, Coordinate myPosition, Chip myChip, Chip winningPlayer) {
		return Props.create(PathQualityQueryWorker.class, () -> new PathQualityQueryWorker(board, myPosition, myChip, winningPlayer));
	}

	private final Board board;
	private final Coordinate myPosition;
	private final Chip myChip;
	private final Chip winningPlayer;
	private final List<Path> pathList;
	private long pendingWorkers;

	public PathQualityQueryWorker(Board board, Coordinate myPosition, Chip myChip, Chip winningPlayer) {
		this.board = board.putChip(new Cell(myChip, myPosition));
		this.myPosition = myPosition;
		this.myChip = myChip;
		this.winningPlayer = winningPlayer;
		this.pathList = new ArrayList<>();
		this.pendingWorkers = 0;
	}

	@Override
	public void preStart() {
		if (imWinning()) {
			PathQuality pathQuality = new PathQuality(myChip.equals(winningPlayer) ? GameResult.WIN : GameResult.LOST);
			getContext().getParent().tell(new Path(myPosition, pathQuality), getSelf());
			getContext().stop(getSelf());
		} else {
			if (board.isFull()) {
				getContext().getParent().tell(new Path(myPosition, new PathQuality(GameResult.TIE)), getSelf());
				getContext().stop(getSelf());
			} else {
				this.pendingWorkers =
						board.getContent()
							 .stream()
							 .filter(Cell::isEmpty)
							 .map(Cell::getCoordinate)
							 .peek(coordinate -> getContext().actorOf(PathQualityQueryWorker.props(board, coordinate, myChip.reverse(), winningPlayer)))
							 .count();
			}
		}
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Path.class, pathQuality -> {
					pendingWorkers--;
					pathList.add(pathQuality);

					if (pendingWorkers == 0) {
						getContext().getParent().tell(new Path(myPosition, pathList.stream().map(Path::getPathQuality).collect(Collectors.toList())), getSelf());
						getContext().stop(getSelf());
					}
				}).build();
	}

	private boolean imWinning() {
		long chipsInARow = this.board.getContent()
									 .stream()
									 .filter(cell -> !cell.isEmpty())
									 .filter(cell -> cell.getChip().get().equals(myChip))
									 .filter(cell -> cell.getCoordinate().getX() == myPosition.getX())
									 .count();

		long chipsInAColumn = this.board.getContent()
										.stream()
										.filter(cell -> !cell.isEmpty())
										.filter(cell -> cell.getChip().get().equals(myChip))
										.filter(cell -> cell.getCoordinate().getY() == myPosition.getY())
										.count();

		long chipsInADiagonal = this.board.getContent()
										  .stream()
										  .filter(cell -> !cell.isEmpty())
										  .filter(cell -> cell.getChip().get().equals(myChip))
										  .filter(cell -> {
											  Coordinate cellCoordinate = cell.getCoordinate();
											  int xDiff = cellCoordinate.getX() - myPosition.getX();
											  int yDiff = cellCoordinate.getY() - myPosition.getY();
											  return Math.abs(xDiff) == Math.abs(yDiff);
										  })
										  .count();

		return chipsInARow == 3 || chipsInAColumn == 3 || chipsInADiagonal == 3;
	}

}
