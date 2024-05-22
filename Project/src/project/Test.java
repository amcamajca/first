package project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Test {
    public static void main(String[] args) {
        SubwaySystem subwaySystem = new SubwaySystem();

        try (BufferedReader br = new BufferedReader(new FileReader("D:/subway.txt"))) {
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
        System.out.println(subwaySystem);
        Set<String> transferStations = subwaySystem.getTransferStations();
        System.out.println("Transfer Stations:");
        for (String station : transferStations) {
            System.out.println(station);
        }

        // 测试获取附近站点的功能
        String stationName = "华中科技大学站";
        double distanceThreshold = 1.0;
        System.out.println("Nearby stations within " + distanceThreshold + " km of " + stationName + ":");
        Set<String> nearbyStations = subwaySystem.getNearbyStations(stationName, distanceThreshold);
        for (String station : nearbyStations) {
            System.out.println(station);
        }
        // 测试获取所有路径的功能
        String startStation = "华中科技大学";
        String endStation = "中南路";
        List<List<String>> allPaths = subwaySystem.getAllPaths(startStation, endStation);
        System.out.println("All paths from " + startStation + " to " + endStation + ":");
        for (List<String> path : allPaths) {
            System.out.println(path);
        }

        subwaySystem.printShortestPath(startStation,endStation);
        double fare1 = subwaySystem.calculateFare("Station A", "Station C");
        double fare2 =subwaySystem.calculateFareWithWuhanTong(startStation,endStation);
        double fare3 =subwaySystem.calculateFareWithDayPass(startStation,endStation,7);
        System.out.println("Fare for the journey: " + fare1 + " yuan");
        System.out.println("Fare for the journey: " + fare2+ " yuan");
        System.out.println("Fare for the journey: " + fare3 + " yuan");

    }
}

