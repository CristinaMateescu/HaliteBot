package hlt;

public class MapCell {
    public final Position position;
    public int halite;
    public Ship ship;
    public Entity structure;

    public MapCell(final Position position, final int halite) {
        this.position = position;
        this.halite = halite;
    }

    public boolean isEmpty() {
        return ship == null && structure == null;
    }

    public boolean isOccupied() {
        return ship != null;
    }

    public boolean hasStructure() {
        return structure != null;
    }

    public void markUnsafe(final Ship ship) {
        this.ship = ship;
    }

    public boolean enemy_safe(GameMap gameMap, Player me){
        
        int x = position.x;
        int y = position.y;
        
        Position newP = gameMap.normalize(new Position(x+1,y));
        if (gameMap.cells[newP.y][newP.x].isOccupied() &&
            !me.ships.containsKey(gameMap.cells[newP.y][newP.x].ship.id) )
            return false;
        
        newP = gameMap.normalize(new Position(x-1,y));
        if (gameMap.cells[newP.y][newP.x].isOccupied() &&
            !me.ships.containsKey(gameMap.cells[newP.y][newP.x].ship.id) )
            return false;
        
        newP = gameMap.normalize(new Position(x,y+1));
        if (gameMap.cells[newP.y][newP.x].isOccupied() &&
            !me.ships.containsKey(gameMap.cells[newP.y][newP.x].ship.id) )
            return false;
        
        newP = gameMap.normalize(new Position(x,y-1));
        if (gameMap.cells[newP.y][newP.x].isOccupied() &&
            !me.ships.containsKey(gameMap.cells[newP.y][newP.x].ship.id) )
            return false;
       
        
        return true;
    }
}
