package com.cristian.tictactoe;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.Status;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import com.cristian.tictactoe.exceptions.PathCalculationError;
import com.cristian.tictactoe.models.Board;
import com.cristian.tictactoe.models.Board.Cell;
import com.cristian.tictactoe.models.Chip;
import com.cristian.tictactoe.models.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PathQualityQuery extends AbstractLoggingActor {

    public static Props props(long requestId, ActorRef requester, Board board, Chip winningPlayer) {
        return Props.create(PathQualityQuery.class,
            () -> new PathQualityQuery(requestId, requester, board, winningPlayer));
    }

    public static class PathQualityResponse {

        private final long requestId;
        private final List<Path> pathList;
        private final long timeInMills;

        public PathQualityResponse(long requestId, List<Path> pathList, long timeInMills) {
            this.requestId = requestId;
            this.pathList = pathList;
            this.timeInMills = timeInMills;
        }

        public long getRequestId() {
            return requestId;
        }

        public List<Path> getPathList() {
            return pathList;
        }

        public long getTimeInMills() {
            return timeInMills;
        }
    }

    private final long requestId;
    private final ActorRef requester;
    private final Board board;
    private final Chip winningPlayer;
    private final List<Path> pathList;
    private long pendingWorkers;
    private long initialTime;

    public PathQualityQuery(long requestId, ActorRef requester, Board board, Chip winningPlayer) {
        this.requestId = requestId;
        this.requester = requester;
        this.board = board;
        this.winningPlayer = winningPlayer;
        this.pendingWorkers = 0;
        this.pathList = new ArrayList<>();
        this.initialTime = System.currentTimeMillis();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(false,
            DeciderBuilder.match(PathCalculationError.class, pathCalculationError -> {
                log().error("Unexpected failure calculating the path ({}), restarting worker...",
                    pathCalculationError.getPath().getCoordinate());
                return SupervisorStrategy.restart();
            }).matchAny(error -> {
                requester.tell(new Status.Failure(error), self());
                return SupervisorStrategy.escalate();
            }).build());
    }

    @Override
    public void preStart() {
        this.pendingWorkers =
            board.getContent()
                .stream()
                .filter(Cell::isEmpty)
                .map(Cell::getCoordinate)
                .peek(coordinate ->
                    getContext().actorOf(PathQualityQueryWorker.props(board, coordinate, winningPlayer, winningPlayer)))
                .count();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Path.class, path -> {
                pathList.add(path);
                pendingWorkers--;

                if (pendingWorkers == 0) {
                    pathList.sort(Comparator.comparingDouble(a -> a.getPathQuality().getLostCount()));
                    requester.tell(new PathQualityResponse(requestId, pathList,
                        System.currentTimeMillis() - initialTime), getSelf());
                    getContext().stop(getSelf());
                }
            })
            .build();
    }

}
