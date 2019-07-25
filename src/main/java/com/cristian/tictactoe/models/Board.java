package com.cristian.tictactoe.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {

	public static class Cell {

		private Chip chip;
		private Coordinate coordinate;

		public Cell(Chip chip, Coordinate coordinate) {
			this.chip = chip;
			this.coordinate = coordinate;
		}

		public boolean isEmpty() {
			return chip == null;
		}

		public Optional<Chip> getChip() {
			return Optional.ofNullable(chip);
		}

		public Coordinate getCoordinate() {
			return coordinate;
		}

	}

	private static final int ROWS = 3;
	private static final int COLUMNS = 3;
	private final List<Cell> content;

	private Board() {
		this.content = new ArrayList<>();
	}

	public static Board empty() {
		Board board = new Board();

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				board.content.add(new Cell(null, new Coordinate(j, i)));
			}
		}

		return board;
	}

	public static Board copyOf(Board sourceBoard) {
		Board newBoard = new Board();
		newBoard.content.addAll(sourceBoard.content);
		return newBoard;
	}

	public boolean isFull() {
		return content.size() == ROWS * COLUMNS && content.stream().noneMatch(Cell::isEmpty);
	}

	public Board putChip(Cell newCell) {
		Board newBoard = Board.copyOf(this);
		newBoard.content.removeIf(cell -> cell.coordinate.equals(newCell.coordinate));
		newBoard.content.add(newCell);
		return newBoard;
	}

	public List<Cell> getContent() {
		return content;
	}
}
