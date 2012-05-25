package sneer.bricks.snapps.games.go.impl.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import sneer.bricks.snapps.games.go.impl.network.Player;

public class GoFrame extends JFrame implements BoardListener,Player{
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	private GoBoardPanel _goBoardPanel;
	private ActionsPanel actionsPanel;
	private GoScorePanel scorePanel;
	private Player _adversary;
	
	public static void main(String[] args) {
		TimerFactory timerFactory = new TimerFactory() {
			@Override
			public WeakContract wakeUpEvery(final int interval, final Runnable scroller) {
				new Thread(){
					@Override
					public void run() {
						while(true){
							try {
								scroller.run();
								Thread.sleep(interval);
							} catch (InterruptedException e) {
								throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
							}
						}
					};
				}.start();
				return null;
			}
		};
		GoFrame blackFrame = new GoFrame(StoneColor.BLACK, 0, timerFactory);
		GoFrame whiteFrame = new GoFrame(StoneColor.WHITE, 0, timerFactory);
		whiteFrame.setAdversary(blackFrame);
		blackFrame.setAdversary(whiteFrame);
	}
	
	public GoFrame(StoneColor side, int horizontalPosition, final TimerFactory timerFactory) {
		_side = side;
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Go - " + _side.name());	  
	    setResizable(false);
	    addComponentPanel(timerFactory); 
	    setVisible(true);
	    int bord=getInsets().left+getInsets().right;
	    setBounds(horizontalPosition*(500+bord)+100, 100, 500+bord, 575);
		//this is for when the game is running on a single window
		//setLocationRelativeTo(null);
	}
	
	private void addComponentPanel(final TimerFactory timerFactory) {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		_goBoardPanel = new GoBoardPanel(this,timerFactory, _side);
		_goBoardPanel.setBoardListener(this);
		contentPane.add(_goBoardPanel, BorderLayout.CENTER);
		
		JPanel goEastPanel = new JPanel();
		
		goEastPanel.setLayout(new FlowLayout());
		scorePanel = new GoScorePanel(_goBoardPanel.scoreBlack(), _goBoardPanel.scoreWhite());
		goEastPanel.add(scorePanel);
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(30,0));
		
		goEastPanel.add(space); 
		
		Closure pass = new Closure() { @Override public void run() {
			doMovePass();
		}};
		Closure resign = new Closure() { @Override public void run() {
			doMoveResign();
		}}; 
		
		actionsPanel = new ActionsPanel(pass,resign, _side);
		
		goEastPanel.add(actionsPanel);
				
		contentPane.add(goEastPanel, BorderLayout.SOUTH);
	}

	@Override
	public void updateScore(int blackScore, int whiteScore) {
		scorePanel.updateScore(blackScore, whiteScore);
	}

	@Override
	public void nextToPlay(StoneColor nextToPlay) {
		actionsPanel.nextToPlay(nextToPlay);
	}

	@Override
	public void setAdversary(Player adversary) {
		_adversary = adversary;
	}
	//Game Plays traffic below
	
	public void doMovePass() {
		Move move = new Move(false, true, 0, 0, false);
		_adversary.play(move);
	}
	
	public void doMoveResign() {
		Move move = new Move(true, false, 0, 0, false);
		_adversary.play(move);
	}
	
	public void doMoveAddStone(int x, int y) {
		Move move = new Move(false, false, x,y, false);
		_adversary.play(move);
	}

	public void doMoveMarkStone(int x, int y) {
		Move move = new Move(false, false, x,y, true);
		_adversary.play(move);
	}

	@Override
	public void play(Move move) {
		if (move.isResign) {
			_goBoardPanel.receiveMoveResign();
			return;
		}
		if (move.isPass){
			_goBoardPanel.receiveMovePassTurn();
			return;
		}
		
		if (move.isMark){
			_goBoardPanel.receiveMoveMarkStone(move.xCoordinate, move.yCoordinate);
			return;
		}
		
		_goBoardPanel.receiveMoveAddStone(move.xCoordinate, move.yCoordinate);			
	}
	
}
