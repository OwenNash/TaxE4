package gameLogic.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import gameLogic.dijkstra.Dijkstra;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private List<Station> stations;
    private List<Connection> connections;
    private Random random = new Random();
    private Dijkstra dijkstra;

    public Map() {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();

        initialise();
        dijkstra = new Dijkstra(this);
    }

    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));

        parseStations(jsonVal);
        parseConnections(jsonVal);
    }

    private void parseConnections(JsonValue jsonVal) {
        for(JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";

            for(JsonValue val = connection.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }

            addConnection(station1, station2);
        }
    }

    private void parseStations(JsonValue jsonVal) {
        for(JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
            String name = "";
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            for(JsonValue val = station.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if(val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if(val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            if (isJunction) {
                addJunction(name, new Position(x,y));
            } else {
                addStation(name, new Position(x, y));
            }
        }
    }

    public boolean doesConnectionExist(String stationName, String anotherStationName) {
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            if (s1.equals(stationName) && s2.equals(anotherStationName)
                || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return true;
            }
        }

        return false;
    }

    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }

    public Station addStation(String name, Position location) {
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }
    
    public CollisionStation addJunction(String name, Position location) {
    	CollisionStation newJunction = new CollisionStation(name, location);
    	stations.add(newJunction);
    	return newJunction;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Connection addConnection(Station station1, Station station2) {
        Connection newConnection = new Connection(station1, station2);
        connections.add(newConnection);
        return newConnection;
    }

    //Add Connection by Names
    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }

    //Get connections from station
    public List<Connection> getConnectionsFromStation(Station station) {
        List<Connection> results = new ArrayList<Connection>();
        for(Connection connection : connections) {
            if(connection.getStation1() == station || connection.getStation2() == station) {
                results.add(connection);
            }
        }
        return results;
    }

    public Station getStationByName(String name) {
        int i = 0;
        while(i < stations.size()) {
            if(stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else{
                i++;
            }
        }
        return null;
    }

    public Station getStationFromPosition(IPositionable position) {
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }

        throw new RuntimeException("Station does not exist for that position");
    }

    public List<Station> createRoute(List<IPositionable> positions) {
        List<Station> route = new ArrayList<Station>();

        for (IPositionable position : positions) {
            route.add(getStationFromPosition(position));
        }

        return route;
    }

    public void decrementBlockedConnections() {
        for (Connection connection : connections) {
            connection.decrementBlocked();
        }
    }

    public Connection getRandomConnection(){
        int index = random.nextInt(connections.size());
        return connections.get(index);
    }

    public void blockRandomConnection(){
        int rand = random.nextInt(2);
        if (rand > 0) { //50% chance of connection being blocked

            Connection toBlock = getRandomConnection();
            toBlock.setBlocked(4);
            System.out.println("Connection blocked: " + toBlock.getStation1().getName() + " to " + toBlock.getStation2().getName());

            //connections.get(0).setBlocked(1); //UNCOMMENT FOR TEST OF 50% CHANCE TO BLOCK PARIS-MADRID CONNECTION
        }

    }

    public boolean isConnectionBlocked(Station station1, Station station2) {
        for (Connection connection : connections){
            if(connection.getStation1() == station1)
                if(connection.getStation2() == station2)
                    return connection.isBlocked();
            if(connection.getStation1() == station2)
                if(connection.getStation2() == station1)
                    return connection.isBlocked();
        }
        //Reaching here means a connection has been added to the route where a connection doesn't exist
        return true;
    }

    public ArrayList<Connection> getBlockedConnections(){
        ArrayList<Connection> blockedConnections = new ArrayList<Connection>();
        for (Connection connection : this.getConnections()){
            if (connection.isBlocked()){
                blockedConnections.add(connection);
            }
        }
        return blockedConnections;
    }

    public float getDistance (Station s1, Station s2) {
        return Vector2.dst(s1.getLocation().getX(), s1.getLocation().getY(), s2.getLocation().getX(), s2.getLocation().getY());
    }
    public double getShortestDistance(Station s1, Station s2){
        return dijkstra.findMinDistance(s1, s2);
    }
}