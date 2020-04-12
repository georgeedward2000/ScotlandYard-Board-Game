package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.*;

public class TheCraneAi implements Ai {
	static ScoreFunction sc = new ScoreFunction();
	static Move bestNextMove = null;
	final static int NO_ANTICIPATED_MOVES = 4;
	@Nonnull
	@Override
	public String name() {
		return "TheCraneAi";
	}

	@Nonnull
	@Override
	public Move pickMove(
			@Nonnull Board board,
			@Nonnull AtomicBoolean terminate) {
		//System.out.println("NEBUN      " + board.getMrXTravelLog().size());
		//extracting mrX initial location using the current board
		var moves = board.getAvailableMoves().asList();
		int initialLocationMRX = moves.iterator().next().source();

		//Move bestNextMove = moves.get(new Random().nextInt(moves.size()));
		//extracting mrX and a list of detectives -> instances of class PlayerInfo
		PlayerInfo mrX = null;
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

		List<PlayerInfo> init = new ArrayList<>(); //a list with all the players
		init.add(mrX);
		init.addAll(detectives);

		OurNewBoard newBoard = new OurNewBoard(init, board.getSetup()); //a board used for anticipating possible next moves

		int bestMoveScore = minimaxAlphaBeta(0, true, newBoard, mrX, detectives, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
			//System.out.println("CEL MAI TARE DIN PARCARE : " + bestMoveScore);
		return bestNextMove;
	}


	static int minimaxAlphaBeta(int depth, boolean maximize, OurNewBoard ourBoard, PlayerInfo mrX, List<PlayerInfo> detectives, Board board, int alpha, int beta) {
		int currentScore = 0;

		if (depth == NO_ANTICIPATED_MOVES) {
			return sc.scorer(board.getSetup().graph, ourBoard.players, mrX.getLocation());
		}

		if (maximize) {
			int v = Integer.MIN_VALUE;
			for (Move nextMove : ourBoard.getAvailableMoves()) {
				PlayerInfo newMrX = new PlayerInfo(mrX.giveTicketBoard(), mrX.getPiece(), mrX.getLocation());

				int destination = extractDestination(nextMove);

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
					if(depth == 0) {
						bestNextMove = nextMove;
						//System.out.println("bestmove sc"+v);
					}
				}

				if (beta <= alpha) break;
			}

			return alpha;
		}
		else {
			int v = Integer.MAX_VALUE;

			for (Move nextMove : ourBoard.getAvailableMoves()) {
				PlayerInfo newMrX = new PlayerInfo(mrX.giveTicketBoard(), mrX.getPiece(), mrX.getLocation());

				int destination = extractDestination(nextMove);

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

	public static int extractDestination(Move nextMove) {

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

