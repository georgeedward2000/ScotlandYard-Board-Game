package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.*;

public class MyAi implements Ai {
	public static ScoreFunction sc = new ScoreFunction();
	public static Move bestMoveuleanu = null;
	public static int currentScore = 0;

	@Nonnull
	@Override
	public String name() {
		return "theCrane";
	}

	@Nonnull
	@Override
	public Move pickMove(
			@Nonnull Board board,
			@Nonnull AtomicBoolean terminate) {

		PlayerInfo mrX = null;
		var moves = board.getAvailableMoves().asList();
		int initialLocationMRX = moves.iterator().next().source();

		List<PlayerInfo> detectives = new ArrayList<>();
		for (Piece player : board.getPlayers()) {

			if (player.isMrX()) {
				mrX = new PlayerInfo(board.getPlayerTickets(player).get(), player, initialLocationMRX);
				continue;
			}
			switch (player.webColour()) {
				case ("#f00"):
					detectives.add(new PlayerInfo(board.getPlayerTickets(player).get(), player, board.getDetectiveLocation(Piece.Detective.RED).get()));
					break;
				case ("#0f0"):
					detectives.add(new PlayerInfo(board.getPlayerTickets(player).get(), player, board.getDetectiveLocation(Piece.Detective.GREEN).get()));
					break;
				case ("#00f"):
					detectives.add(new PlayerInfo(board.getPlayerTickets(player).get(), player, board.getDetectiveLocation(Piece.Detective.BLUE).get()));
					break;
				case ("#fff"):
					detectives.add(new PlayerInfo(board.getPlayerTickets(player).get(), player, board.getDetectiveLocation(Piece.Detective.WHITE).get()));
					break;
				case ("#ff0"):
					detectives.add(new PlayerInfo(board.getPlayerTickets(player).get(), player, board.getDetectiveLocation(Piece.Detective.YELLOW).get()));
					break;
			}
		}

		List<PlayerInfo> init = new ArrayList<>();
		init.add(mrX);
		init.addAll(detectives);
		OurNewBoard newBoard = new OurNewBoard(init, board.getSetup());
		Move bestMove = null;
		int bestMoveScore = minimaxAlphaBeta(0, true, newBoard, mrX, detectives, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
		//ScoreFunction scoreFunction = new ScoreFunction();
		//scoreFunction.scorer();
		// returns a random move, replace with your own implementation
		System.out.println("CEL MAI TARE DIN PARCARE : " + bestMoveScore);
		return bestMoveuleanu;
		//return moves.get(new Random().nextInt(moves.size()));
	}


	static int minimaxAlphaBeta(int depth, boolean maximize, OurNewBoard ourBoard, PlayerInfo mrX, List<PlayerInfo> detectives, Board board, int alpha, int beta) {

		if (depth == 3) {
			int scorulina = sc.scorer(board.getSetup().graph, ourBoard.players, mrX.getLocation());
			System.out.println(scorulina);
			return scorulina;
		}

		if (maximize) {
			int v = Integer.MIN_VALUE;
			for (Move nextMove : ourBoard.getAvailableMoves()) {
				PlayerInfo newMrX = new PlayerInfo(mrX.giveTicketBoard(), mrX.getPiece(), mrX.getLocation());

				int destination = saTraiascaMata(nextMove);

				newMrX.changeLocation(destination);
				newMrX.modifyTickets(nextMove.tickets(), -1);

				List<PlayerInfo> init = new ArrayList<>();
				init.add(newMrX);
				init.addAll(detectives);

				OurNewBoard newBoard = new OurNewBoard(init, board.getSetup());

				currentScore = minimaxAlphaBeta(depth + 1, false, newBoard, newMrX, detectives, board, alpha, beta);
				v = Integer.max(v, currentScore);
				if (v > alpha) {
					alpha = v;
					if(depth == 0) bestMoveuleanu = nextMove;
				}
				if (beta <= alpha) break;
			}

			return v;
		}
		else {
			int v = Integer.MAX_VALUE;
			for (Move nextMove : ourBoard.getAvailableMoves()) {
				PlayerInfo newMrX = new PlayerInfo(mrX.giveTicketBoard(), mrX.getPiece(), mrX.getLocation());

				int destination = saTraiascaMata(nextMove);

				newMrX.changeLocation(destination);
				newMrX.modifyTickets(nextMove.tickets(), -1);

				List<PlayerInfo> init = new ArrayList<>();
				init.add(newMrX);
				init.addAll(detectives);

				OurNewBoard newBoard = new OurNewBoard(init, board.getSetup());
				currentScore = minimaxAlphaBeta(depth + 1, true, newBoard, newMrX, detectives, board, alpha, beta);
				v = Integer.min(v, currentScore);
				if (v < beta) {
					beta = v;
				}
				if (beta <= alpha) break;
			}
			return v;
		}

	}

	static int saTraiascaMata(Move nextMove) {

		List<Integer> Destination = nextMove.visit(new Move.Visitor<List<Integer>>() {
			@Override public List<Integer> visit(Move.SingleMove singleMove) {
				List<Integer> Destination = new ArrayList<>();
				Destination.add(singleMove.destination);
				return Destination;
			}
			@Override public List<Integer> visit(Move.DoubleMove doubleMove) {
				List <Integer> Destination = new ArrayList<>();
				Destination.add(doubleMove.destination1);
				Destination.add(doubleMove.destination2);
				return Destination;
			}
		} );

		int destination = Destination.iterator().next(); //extract the first element from the list
		int destination2 = -1; // we provide an invalid location in case we have only 1 location in the list

		if (Destination.size() > 1) { //DoubleMove is valid only for mrX
			//extract the last/second element in the list
			int lastElement = -1; //an initial invalid location

			for (int last : Destination)
				lastElement = last;

			destination2 = lastElement;
		}

		int finalDestination = -1; // not the movie
		if (Destination.size() > 1) finalDestination = destination2;
		else finalDestination = destination;

		return finalDestination;

	}

}

