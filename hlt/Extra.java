package hlt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author Cristina-Ramona
 */
public class Extra {

	public Direction navigator(final Ship ship,final Direction prevDir, final GameMap gameMap,int min
				   , Shipyard enemy, Player me) {

		
		ArrayList<Direction> directii = new ArrayList<>();

		Position p = gameMap.normalize(ship.position.directionalOffset( Direction.EAST));
		if ( !gameMap.at(ship.position.directionalOffset( Direction.EAST)).isOccupied()
		     && gameMap.cells[p.y][p.x].enemy_safe(gameMap,me))
			directii.add(Direction.EAST);

		p = gameMap.normalize(ship.position.directionalOffset( Direction.NORTH));
		if ( !gameMap.at(ship.position.directionalOffset( Direction.NORTH)).isOccupied()
		      && gameMap.cells[p.y][p.x].enemy_safe(gameMap,me) )
			directii.add(Direction.NORTH);

		p = gameMap.normalize(ship.position.directionalOffset( Direction.SOUTH));
		if ( !gameMap.at(ship.position.directionalOffset( Direction.SOUTH)).isOccupied()
		     && gameMap.cells[p.y][p.x].enemy_safe(gameMap,me))
			directii.add(Direction.SOUTH);
		
		p = gameMap.normalize(ship.position.directionalOffset( Direction.WEST));
		if ( !gameMap.at(ship.position.directionalOffset( Direction.WEST)).isOccupied()
		      && gameMap.cells[p.y][p.x].enemy_safe(gameMap,me))
			directii.add(Direction.WEST);

		
		

		Collections.sort(directii, new Comparator <Direction> (){
			@Override
			public int compare(Direction o1, Direction o2) {
				return gameMap.at(ship.position.directionalOffset(o2)).halite - 
						gameMap.at(ship.position.directionalOffset(o1)).halite;
			}

		});

		if ( ship.position.equals(enemy.position))
			return Direction.STILL;

		if ( directii.isEmpty() ) 
			return Direction.STILL;
			
		if (prevDir!=null && !gameMap.at(ship.position.directionalOffset(prevDir)).isOccupied() 
		    && gameMap.at(ship.position.directionalOffset(directii.get(0))).halite < min){
			
			return prevDir;
		  }
		
		else {
		     if ( ship.direction!=null && !gameMap.at(ship.position.directionalOffset(ship.direction)).isOccupied()  
			 && ship.position.equals(me.shipyard.position) ) 
			return ship.direction;

			return directii.get(0);}
	}
        
        
        public Direction navigatorHome(final Ship ship, final GameMap gameMap,  final Position destination) {

		
		ArrayList<Direction> directii = gameMap.getUnsafeMoves(ship.position, destination);		

		Collections.sort(directii, new Comparator <Direction> (){
			@Override
			public int compare(Direction o1, Direction o2) {
				return gameMap.at(ship.position.directionalOffset(o1)).halite - 
						gameMap.at(ship.position.directionalOffset(o2)).halite;
			}

		});

                for (final Direction direction : directii) {
                    final Position targetPos = ship.position.directionalOffset(direction);
                    if (!gameMap.at(targetPos).isOccupied() ) {
                        gameMap.at(targetPos).markUnsafe(ship);
                        return direction;
                    }
                }

                return Direction.STILL;
	}
        
        
}
