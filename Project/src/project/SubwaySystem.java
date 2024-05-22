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
    public List<List<String>> getAllPaths(String startStation, String endStation) {
        List<List<String>> allPaths = new ArrayList<>();
        if (!map.containsKey(startStation) || !map.containsKey(endStation)) {
            return allPaths; // Return empty list if start or end station is not found
        }
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();
        path.add(startStation);
        dfs(startStation, endStation, visited, path, allPaths);
        return allPaths;
    }

    private void dfs(String currentStation, String endStation, Set<String> visited, List<String> path, List<List<String>> allPaths) {
        visited.add(currentStation);
        if (currentStation.equals(endStation)) {
            allPaths.add(new ArrayList<>(path));
        } else {
            Map<String, Double> stationsOnLine = map.getOrDefault(currentStation, new HashMap<>());
            for (String nextStation : stationsOnLine.keySet()) {
                if (!visited.contains(nextStation)) {
                    path.add(nextStation);
                    dfs(nextStation, endStation, visited, path, allPaths);
                    path.remove(path.size() - 1);
                }
            }
        }
        visited.remove(currentStation);
    }

    // Other methods omitted for brevity


    public List<String> getShortestPath(String startStation, String endStation) {
        // 使用 Dijkstra 算法查找最短路径
        Map<String, Double> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distance::get));
        Set<String> visited = new HashSet<>();

        // 初始化距离和前驱节点信息
        for (String station : map.keySet()) {
            distance.put(station, Double.MAX_VALUE);
            previous.put(station, null);
        }

        // 设置起点距离为0，并加入优先队列
        distance.put(startStation, 0.0);
        priorityQueue.add(startStation);

        // 开始 Dijkstra 算法
        while (!priorityQueue.isEmpty()) {
            String currentStation = priorityQueue.poll();
            visited.add(currentStation);

            Map<String, Double> neighbors = map.get(currentStation);
            if (neighbors == null) {
                continue;
            }

            for (String neighbor : neighbors.keySet()) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                double newDistance = distance.get(currentStation) + neighbors.get(neighbor);
                if (newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, currentStation);
                    priorityQueue.add(neighbor);
                }
            }
        }


        List<String> shortestPath = new ArrayList<>();
        String currentStation = endStation;
        while (currentStation != null) {
            shortestPath.add(currentStation);
            currentStation = previous.get(currentStation);
        }
        Collections.reverse(shortestPath);

        return shortestPath;
    }
    public void printShortestPath(String startStation, String endStation) {
        List<String> shortestPath = getShortestPath(startStation, endStation);

        if (shortestPath.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        System.out.println("Shortest path from " + startStation + " to " + endStation + ":");

        String currentLine = null;
        String previousLine = null;

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            String currentStation = shortestPath.get(i);
            String nextStation = shortestPath.get(i + 1);

            for (String line : map.keySet()) {
                if (map.get(line).containsKey(currentStation) && map.get(line).containsKey(nextStation)) {
                    currentLine = line;
                    break;
                }
            }

            if (currentLine.equals(previousLine)) {
                System.out.print(" -> " + nextStation);
            } else {
                if (previousLine != null) {
                    System.out.println();
                }
                System.out.print("Take " + currentLine + " line from " + currentStation);
            }

            previousLine = currentLine;
        }

        System.out.println();
    }
    public double calculateFare(String startStation, String endStation) {
        List<String> shortestPath = getShortestPath(startStation, endStation);
        double fare = (shortestPath.size() - 1) * 2;
        return fare;
    }



    public double calculateFareWithWuhanTong(String startStation, String endStation) {
        double fare = calculateFare(startStation, endStation);
        fare *= 0.9;
        return fare;
    }

    public double calculateFareWithDayPass(String startStation, String endStation, int days) {

        if (days == 1) {
            return 18;
        } else if (days == 3) {
            return 45;
        } else if (days == 7) {
            return 90;
        } else {
            return 0;
        }
    }


}


