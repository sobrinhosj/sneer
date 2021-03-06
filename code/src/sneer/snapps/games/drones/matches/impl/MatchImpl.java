package sneer.snapps.games.drones.matches.impl;

import static basis.environments.Environments.my;
import sneer.snapps.games.drones.matches.Match;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Units;

class MatchImpl implements Match {

	private final Unit unit1 = my(Units.class).create(  0, Unit.Direction.RIGHT, "Player 1");
	private final Unit unit2 = my(Units.class).create(700, Unit.Direction.LEFT,  "Player 2");

	@Override
	public void step() {
		if (!unit1.collidesWith(unit2))
			moveUnits();
		
		if (unit1.collidesWith(unit2))
			battle();
	}

	private void battle() {
		unit1.attack(unit2);
		unit2.attack(unit1);
	}

	private void moveUnits() {
		unit1.move();
		unit2.move();
	}

	@Override
	public Unit unit1() {
		return unit1;
	}

	@Override
	public Unit unit2() {
		return unit2;
	}

	@Override
	public boolean isOver() {
		return !unit1.isAlive() || !unit2.isAlive(); 
	}

	@Override
	public String result() {
		if (unit1.isAlive() && unit2.isAlive()) throw new IllegalStateException();
		
		if (!unit1.isAlive() && !unit2.isAlive())
			return "Draw!";
		Unit winner = unit1.isAlive() ? unit1 : unit2;
		return winner + " wins!";
	}

}
