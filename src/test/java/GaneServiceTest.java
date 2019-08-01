import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.cristian.tictactoe.GameService;
import com.cristian.tictactoe.PathQualityQuery.PathQualityResponse;
import com.cristian.tictactoe.models.Board;
import com.cristian.tictactoe.models.Board.Cell;
import com.cristian.tictactoe.models.Chip;
import com.cristian.tictactoe.models.Coordinate;
import com.google.common.truth.Truth;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

public class GaneServiceTest {

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
	public void testComunication() {
		Board board = Board.empty()
						   .putChip(new Cell(Chip.X, new Coordinate(0, 0)))
						   .putChip(new Cell(Chip.O, new Coordinate(0, 1)))
						   .putChip(new Cell(Chip.X, new Coordinate(0, 2)))
						   .putChip(new Cell(Chip.O, new Coordinate(1, 0)))
						   .putChip(new Cell(Chip.X, new Coordinate(1, 1)))
						   .putChip(new Cell(Chip.O, new Coordinate(1, 2)));

		long requestId = 0;
		ActorRef gameService = actorSystem.actorOf(GameService.props());
		gameService.tell(new GameService.PathQualityRequest(requestId, board, Chip.X), testKit.getRef());

		PathQualityResponse pathQualityResponse = testKit.expectMsgClass(Duration.ofSeconds(20), PathQualityResponse.class);
		Truth.assertThat(pathQualityResponse.getRequestId()).isEqualTo(requestId);
	}

}
