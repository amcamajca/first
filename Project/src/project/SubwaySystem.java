package project;
import java.io.*;
import java.util.*;


public class SubwaySystem {
    private Map<String, Map<String, Double>> map = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private Map<String, List<String>> lines = new HashMap<>();

    public SubwaySystem() {
        this.map = new LinkedHashMap<>();
    }

    public SubwaySystem(String filename) throws FileNotFoundException {
        readData2(filename);
    }

    public SubwaySystem readData1(String filename, SubwaySystem subwaySystem) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentLine = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("号线站点间距")) {
                    currentLine = line.split("号线站点间距")[0];
                    subwaySystem.addLine(currentLine);
                } else if (line.contains("---") || line.contains("—")) {
                    String separator = line.contains("---") ? "---" : "—";
                    String[] parts = line.split(separator);
                    String station1 = parts[0].trim();

                    String station2 = parts[1].split("\t")[0].trim();
                    double distance = Double.parseDouble(parts[1].split("\t")[1].trim());
                    subwaySystem.addStation(currentLine, station1, distance);
                    subwaySystem.addStation(currentLine, station2, distance);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subwaySystem;
    }

    private void readData2(String filename) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(filename));
        String currentLine = null;

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.contains("号线")) {
                currentLine = line.trim();
                lines.putIfAbsent(currentLine, new ArrayList<>());
            } else if (line.contains("---")) {
                String[] parts = line.split("\t");
                String[] stations = parts[0].split("---");
                Double distance = Double.parseDouble(parts[1]);
                String station1 = stations[0].trim();
                String station2 = stations[1].trim();
                addEdge(station1, station2, distance);
                addEdge(station2, station1, distance);
                lines.get(currentLine).add(station1);
                lines.get(currentLine).add(station2);
            }
        }
        fileScanner.close();
    }

    private void addEdge(String from, String to, double distance) {
        map.putIfAbsent(from, new HashMap<>());
        map.get(from).put(to, distance);
    }


    public void addLine(String lineName) {
        map.put(lineName, new LinkedHashMap<>());
    }

    public void addStation(String lineName, String stationName, double distance) {
        map.get(lineName).put(stationName, distance);
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




    public Set<String> getStationsNearby(String stationName, double distance) {
        Set<String> nearbyStations = new HashSet<>();

        if (!map.containsKey(stationName)) {
            System.out.println("输入的站点不存在！");
            return nearbyStations;
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> distances = new HashMap<>();

        queue.offer(stationName);
        distances.put(stationName, 0);

        while (!queue.isEmpty()) {
            String currentStation = queue.poll();
            int currentDistance = distances.get(currentStation);

            if (currentDistance == distance) {
                nearbyStations.add("<"+currentStation + ", " + getLineForStation(currentStation) + ", " + currentDistance+">");
                continue;
            }

            Map<String, Double> neighbors = map.get(currentStation);
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String neighborStation = entry.getKey();
                if (!distances.containsKey(neighborStation) || distances.get(neighborStation) > currentDistance + 1) {
                    queue.offer(neighborStation);
                    distances.put(neighborStation, currentDistance + 1);
                }
            }
        }

        return nearbyStations;
    }

    private String getLineForStation(String stationName) {
        for (Map.Entry<String, List<String>> entry : lines.entrySet()) {
            if (entry.getValue().contains(stationName)) {
                return entry.getKey().substring(0,3);
            }
        }
        return "";
    }


    public List<List<String>> getAllPaths(String startStation, String endStation) {
        List<List<String>> allPaths = new ArrayList<>();
        if (!map.containsKey(startStation) || !map.containsKey(endStation)) {
            return allPaths;
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


    public void findShortestPath() {
        System.out.print("请输入起点站：");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站：");
        String end = scanner.nextLine().trim();

        if (!map.containsKey(start) || !map.containsKey(end)) {
            System.out.println("站点名称有误，请检查输入！");
            return;
        }

        List<String> path = dijkstra(start, end);
        if (path.isEmpty()) {
            System.out.println("未找到从 " + start + " 到 " + end + " 的路径。");
        } else {
            System.out.println("从 " + start + " 到 " + end + " 的最短路径：");
            for (String station : path) {
                System.out.print(station + (station.equals(end) ? "" : " -> "));
            }
            System.out.println();
        }
    }

    public List<String> dijkstra(String start, String end) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> node.distance));
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : map.keySet()) {
            distances.put(vertex, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(new Node(start, 0.0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visited.add(node.station);
            if (node.station.equals(end)) break;
            for (Map.Entry<String, Double> neighbor : map.get(node.station).entrySet()) {
                if (visited.contains(neighbor.getKey())) continue;
                double newDist = distances.get(node.station) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), node.station);
                    queue.add(new Node(neighbor.getKey(), newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.isEmpty() || !path.get(0).equals(start) ? new ArrayList<>() : path;
    }

    static class Node {
        String station;
        double distance;

        public Node(String station, double distance) {
            this.station = station;
            this.distance = distance;
        }
    }




    public void calculateFare() {
        System.out.print("请输入起点站：");
        String start = scanner.nextLine().trim();
        System.out.print("请输入终点站：");
        String end = scanner.nextLine().trim();

        if (!map.containsKey(start) || !map.containsKey(end)) {
            System.out.println("站点名称有误，请检查输入！");
            return;
        }

        List<String> path = dijkstra(start, end);
        if (path.isEmpty()) {
            System.out.println("未找到从 " + start + " 到 " + end + " 的路径。");
            return;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += map.get(path.get(i)).get(path.get(i + 1));
        }

        double fare = calculateFareFromDistance(totalDistance);
        System.out.println("从 " + start + " 到 " + end + " 的票价为：" + fare + " 元");
    }
    private double calculateFareFromDistance(double distance) {
        if (distance <= 6) {
            return 2.0;
        } else if (distance <= 12) {
            return 3.0;
        } else if (distance <= 24) {
            return 4.0;
        } else if (distance <= 36) {
            return 5.0;
        } else {
            return 6.0;
        }
    }
}



