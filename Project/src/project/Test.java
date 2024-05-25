package project;
import java.io.FileNotFoundException;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        SubwaySystem s =new SubwaySystem();
        SubwaySystem subwaySystem=s.readData1("D:/subway.txt",s);

        System.out.println(subwaySystem);
        System.out.println();
        Set<String> transferStations = subwaySystem.getTransferStations();
        System.out.println("Transfer Stations:");
        for (String station : transferStations) {
            System.out.println(station);
        }

        System.out.println();

        try {
            SubwaySystem subwaysystem = new SubwaySystem("D:/subway.txt");
            // 测试获取附近站点的功能
            String stationName = "中南路";
            double distanceThreshold = 1.0;
            System.out.println("Nearby stations within " + distanceThreshold + " km of " + stationName + ":");
            Set<String> nearbyStations = subwaysystem.getStationsNearby(stationName, distanceThreshold);
            for (String station : nearbyStations) {
                System.out.println(station);
            }
            System.out.println();

            //查询两站点间所有路线
            String start = "华中科技大学";
            String end = "洪山广场";
            List<List<String>> allPaths = subwaysystem.getAllPaths(start, end);
            System.out.println("All paths from " + start + " to " + end + ":");
            for (List<String> path : allPaths) {
                System.out.println(path);
            }
            System.out.println();


            //寻找两站点最短路径并打印
            System.out.println("寻找两站点最短路径并打印");
            subwaysystem.findShortestPath();
            System.out.println();


            //输入给定起点、终点后，输出乘车费用
            System.out.println("输入给定起点、终点后，输出乘车费用");
            subwaysystem.calculateFare();

        } catch (FileNotFoundException e) {
            System.out.println("文件未找到：" + e.getMessage());
        }
    }
}

