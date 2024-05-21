package project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SubwaySystem {
    private Map<String, Map<String, Double>> map;

    public SubwaySystem() {
        this.map = new LinkedHashMap<>();
    }

    public void addLine(String lineName) {
        map.put(lineName, new LinkedHashMap<>());
    }

    public void addStation(String lineName, String stationName, double distance) {
        map.get(lineName).put(stationName, distance);
    }

    public double getDistance(String lineName, String station1, String station2) {
        return map.get(lineName).get(station1) + map.get(lineName).get(station2);
    }

    public Set<String> getTransferStations() {
        Map<String, Set<String>> stationLines = new HashMap<>();
        for (String line : map.keySet()) {
            for (String station : map.get(line).keySet()) {
                stationLines.putIfAbsent(station, new HashSet<>());
                stationLines.get(station).add(line);
            }
        }

        Set<String> transferStations = new HashSet<>();
        for (String station : stationLines.keySet()) {
            if (stationLines.get(station).size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("<").append(station).append(", <");
                for (String line : stationLines.get(station)) {
                    sb.append(line).append(" 号线、");
                }
                sb.setLength(sb.length() - 1); // Remove the last comma
                sb.append(">>");
                transferStations.add(sb.toString());
            }
        }

        return transferStations;
    }

    @Override
    public String toString() {
        return this.map.values().toString();
    }

    public Set<String> getNearbyStations(String stationName, double distanceThreshold) {
        Set<String> nearbyStations = new HashSet<>();

        for (String line : map.keySet()) {
            Map<String, Double> stationsOnLine = map.get(line);
            for (String station : stationsOnLine.keySet()) {
                if (!station.equals(stationName)) {
                    double distance = stationsOnLine.get(station);
                    if (distance <= distanceThreshold) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<").append(station).append(", ")
                                .append(line).append(" 号线, ").append(distance).append(">");
                        nearbyStations.add(sb.toString());
                    }
                }
            }
        }

        return nearbyStations;
    }
}

