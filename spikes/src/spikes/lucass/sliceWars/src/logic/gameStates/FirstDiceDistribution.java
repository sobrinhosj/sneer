package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public class FirstDiceDistribution implements GameState {

	private final Board _board;
	private final  int _diceToAdd;
	private DiceDistribution _distributeDiePhase;
	private Player _currentPlaying;

	public FirstDiceDistribution(final Player currentPlaying,final Board board) {
		_currentPlaying = currentPlaying;
		_board = board;
		_diceToAdd = board.getCellCount()/currentPlaying.getPlayersCount();
		_distributeDiePhase = new DiceDistribution(currentPlaying, board, _diceToAdd);
	}

	@Override
	public PlayOutcome play(final int x,final int y,final GameStateContext gameStateContext){
		PlayOutcome playOutcome = _distributeDiePhase.play(x, y, gameStateContext);
		if(gameStateContext.getPhase().equals(Phase.FIRST_DICE_DISTRIBUTION)){
			return playOutcome;
		}
		if(_currentPlaying.isLastPlayer()){
			gameStateContext.setState(new FirstAttacks(_currentPlaying.next(), _board));
			return new PlayOutcome(0);
		}
			
		gameStateContext.setState(this);
		Player nextPlayer = _currentPlaying.next();
		_currentPlaying = nextPlayer;
		_distributeDiePhase = new DiceDistribution(nextPlayer, _board, _diceToAdd);
		return new PlayOutcome(_diceToAdd);
	}
	
	@Override
	public String getPhaseName() {
		return "First round, add dice";
	}
	
	@Override
	public Player getWhoIsPlaying() {
		return _currentPlaying;
	}

	@Override
	public boolean canPass() {
		return false;
	}

	@Override
	public PlayOutcome pass(final GameStateContext gameStateContext){
		return null;
	}

	@Override
	public Phase getPhase(){
		return Phase.FIRST_DICE_DISTRIBUTION;
	}

	public int getDiceToAdd() {
		return _diceToAdd;
	}
}
