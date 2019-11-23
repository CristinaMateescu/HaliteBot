// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MyBot {
    public static void main(final String[] args) {
        final long rngSeed;
        if (args.length > 1) {
            rngSeed = Integer.parseInt(args[1]);
        } else {
            rngSeed = System.nanoTime();
        }
        final Random rng = new Random(rngSeed);

        Game game = new Game();
        
        game.ready("MyJavaBot");

        Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + rngSeed + ".");

        HashMap<Integer, Direction> getPrev = new HashMap<>();
        HashMap<Integer, Integer> status = new HashMap<>();
        /* status = 1 -- miner */
        /* status = 2 -- homer */
        
	Extra object = new Extra();
        //int min_cell_halite = Constants.MAX_HALITE / 100;
	int min_cell_halite = 35;
	
	int map_halite = 0;
	for ( int y = 0; y< game.gameMap.height; y++ )
		for ( int x = 0; x < game.gameMap.width; x++)
			map_halite+=game.gameMap.cells[y][x].halite;

	if ( map_halite / (game.gameMap.height*game.gameMap.width) < 120 )
		min_cell_halite = Constants.MAX_HALITE / 100;

        int max_halite = 850;
        
        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;
	    Player enemy = null;

	    for ( Player player : game.players )
		if ( player.id.id != me.id.id )
			enemy = player;

	    /* Casa blocata de inamic */
	    Position shipyard = me.shipyard.position;
	    if ( gameMap.at( shipyard ).isOccupied () &&
	 	 !me.ships.containsKey( gameMap.at (shipyard).ship.id ) )
		gameMap.at (shipyard).ship = null;
	    /*end*/

            final ArrayList<Command> commandQueue = new ArrayList<>();

            for (final Ship ship : me.ships.values()) {


		if(!getPrev.containsKey(ship.id.id)) 
				getPrev.put(ship.id.id, null);  

                if (!status.containsKey(ship.id.id))
                                status.put(ship.id.id,1);
                
            	/* If ship is full or cell has min value - change status */
                if ( ship.halite >= max_halite ){
                    status.remove(ship.id.id);
		    status.put(ship.id.id,2);
                }

                
                /* If status 2 -- go home */
                if ( status.get(ship.id.id) == 2 ){
                    
                    Direction home = object.navigatorHome(ship, gameMap, me.shipyard.position);
                    commandQueue.add(ship.move(home));
                    
                    /* Update status and prev when home is reached */
                    if (ship.position.directionalOffset(home).equals(me.shipyard.position)){
			
                        status.remove(ship.id.id);
			status.put(ship.id.id,1);
                        getPrev.remove(ship.id.id);
                        getPrev.put(ship.id.id, null);}
                }
                /* If status 1 -- mine or stay still */
                else{
                    
                    
                    if (gameMap.at(ship).halite >= min_cell_halite && !ship.position.equals(me.shipyard.position)){
                        commandQueue.add(ship.stayStill());
			}
                        
                    else {
                        
			
                        final Direction direction = object.navigator(ship, 
                                                    getPrev.get(ship.id.id), gameMap,min_cell_halite,enemy.shipyard,me);
			

                        /* mark position as occupied */
                        gameMap.at(ship.position.directionalOffset(direction)).markUnsafe(ship);
                        commandQueue.add(ship.move(direction));
                        
                        /* Do not update previous direction for stay still */
                        if ( direction != Direction.STILL ){
			    
                            getPrev.remove(ship.id.id);
                            getPrev.put(ship.id.id, direction);
			   }
                    }
                
                /* end if status 1 */
                }
             
            /*end for each ship*/      
            }

	    if ( game.turnNumber >= 250 )
			 max_halite = 850;
	
	    if ( game.turnNumber >=300 )
			max_halite = 700;

	    if ( game.turnNumber >= 370 )
			max_halite = 550;
	    
            if (game.turnNumber >= 400 )
			max_halite = 350;

	    /*if ( Constants.MAX_TURNS - game.turnNumber <= 20 )
			max_halite = 250;*/
		
            if (
                game.turnNumber <= 210 &&
                me.halite >= Constants.SHIP_COST &&
                !gameMap.at(me.shipyard).isOccupied())
            {
                commandQueue.add(me.shipyard.spawn());
            }

            game.endTurn(commandQueue);
        }
    }
}
