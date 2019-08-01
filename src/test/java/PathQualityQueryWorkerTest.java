import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.cristian.tictactoe.PathQualityQueryWorker;
import com.cristian.tictactoe.models.*;
import com.cristian.tictactoe.models.Board.Cell;
import com.google.common.truth.Truth;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

public class PathQualityQueryWorkerTest {

	private static ActorSystem actorSystem;
	private TestKit testKit;

	@BeforeClass
	public static void setUpClass() {
		actorSystem = ActorSystem.create();
	}

	@AfterClass
	public static void teardownClass() {
		TestKit.shutdownActorSystem(actorSystem, true);
		actorSystem = null;
	}

	@Before
	public void setUp() {
		testKit = new TestKit(actorSystem);
	}

	@Test
	public void testNormalEnvironment() {
		Board board = Board.empty()
						   .putChip(new Cell(Chip.X, new Coordinate(0, 0)))
						   .putChip(new Cell(Chip.O, new Coordinate(0, 1)))
						   .putChip(new Cell(Chip.X, new Coordinate(0, 2)))
						   .putChip(new Cell(Chip.O, new Coordinate(1, 0)))
						   .putChip(new Cell(Chip.X, new Coordinate(1, 1)))
						   .putChip(new Cell(Chip.O, new Coordinate(1, 2)));

		Coordinate workerPosition = new Coordinate(2, 1);
		Props props = PathQualityQueryWorker.props(board, workerPosition, Chip.X, Chip.X);
		ActorRef worker = testKit.childActorOf(props);
		testKit.watch(worker);

		Path pathResponse = testKit.expectMsgClass(Path.class);
		testKit.expectTerminated(worker);

		Truth.assertThat(pathResponse.getCoordinate()).isEqualTo(workerPosition);
		Truth.assertThat(pathResponse.getPathQuality()).isEqualTo(new PathQuality(2, 2, 0, 0));
	}

	@Test
	public void testEmptyBoard() {
		Board board = Board.empty();

		Coordinate workerPosition = new Coordinate(1, 1);
		Props props = PathQualityQueryWorker.props(board, workerPosition, Chip.X, Chip.X);
		ActorRef worker = testKit.childActorOf(props);
		testKit.watch(worker);

		Path pathResponse = testKit.expectMsgClass(Duration.ofSeconds(20), Path.class);
		testKit.expectTerminated(worker);

		Truth.assertThat(pathResponse.getCoordinate()).isEqualTo(workerPosition);
		Truth.assertThat(pathResponse.getPathQuality()).isEqualTo(new PathQuality(25472, 14256, 4608, 6608));
	}

}
