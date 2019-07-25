package com.cristian.tictactoe;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.cristian.tictactoe.GameService.PathQualityRequest;
import com.cristian.tictactoe.PathQualityQuery.PathQualityResponse;
import com.cristian.tictactoe.models.Board;
import com.cristian.tictactoe.models.Board.Cell;
import com.cristian.tictactoe.models.Chip;
import com.cristian.tictactoe.models.Coordinate;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.japi.Util.classTag;
import static akka.pattern.Patterns.ask;

public class Main {

	public static void main(String[] args) throws Exception {
		final ActorSystem system = ActorSystem.create("tictactoe");
		final ActorRef gameService = system.actorOf(GameService.props(), "game-service");

		Board board = Board.empty()
						   .putChip(new Cell(Chip.X, new Coordinate(1, 1)))
						   .putChip(new Cell(Chip.O, new Coordinate(0, 0)));

		FiniteDuration duration = Duration.create(10, TimeUnit.HOURS);
		PathQualityResponse result = Await.result(
				ask(gameService, new PathQualityRequest(0L, board, Chip.X), new Timeout(duration))
						.mapTo(classTag(PathQualityResponse.class)), duration);

		result.getPathList()
			  .forEach(path -> System.out.println(String.format("x: %d; y: %d; %6.2f%% lost; wins: %d; ties: %d; loses: %d; total paths: %d",
																path.getCoordinate().getX(), path.getCoordinate().getY(),
																path.getPathQuality().getLostPercent(),
																path.getPathQuality().getWinCount(),
																path.getPathQuality().getTieCount(),
																path.getPathQuality().getLostCount(),
																path.getPathQuality().getPathCount())));
		System.out.println("Time: " + result.getTimeInMills() + "ms");

		Await.ready(system.terminate(), Duration.Inf());
	}

}
