package com.cristian.tictactoe;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import com.cristian.tictactoe.models.Board;
import com.cristian.tictactoe.models.Chip;

public class GameService extends AbstractLoggingActor {

	public static Props props() {
		return Props.create(GameService.class, GameService::new);
	}

	public static class PathQualityRequest {
		private final long requestId;
		private final Board board;
		private final Chip winningPlayer;

		public PathQualityRequest(long requestId, Board board, Chip winningPlayer) {
			this.requestId = requestId;
			this.board = board;
			this.winningPlayer = winningPlayer;
		}

		public long getRequestId() {
			return requestId;
		}

		public Board getBoard() {
			return board;
		}

		public Chip getWinningPlayer() {
			return winningPlayer;
		}
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(PathQualityRequest.class, pathQualityRequest -> {
					Props pathQualityQueryProps =
							PathQualityQuery.props(pathQualityRequest.requestId,
												   getSender(),
												   pathQualityRequest.getBoard(),
												   pathQualityRequest.getWinningPlayer());

					getContext().actorOf(pathQualityQueryProps, "path-quality-query-" + pathQualityRequest.getRequestId());
				})
				.build();
	}

}
