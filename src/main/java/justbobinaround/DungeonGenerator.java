package justbobinaround;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DungeonGenerator {

    public static Random r = new Random();
    public final int mapSize = 25;
    public final int mapDiv = (int)Math.sqrt(mapSize)/2;

    public int[][] map;
    private ArrayList<Point> untriedPoints;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Point> untriedPointsWater;

    public DungeonGenerator() {
        map = new int[mapSize][mapSize];
        r.setSeed(System.currentTimeMillis());
        generateMap();
    }


    public void generateMap(){

        for(int j = 0; j < map.length; j++) {
            for(int i = 0; i < map[0].length; i++) {
                if(r.nextBoolean() && j%mapDiv==0 && i%mapDiv==0) {
                    map[j][i] = 1;
                }else {
                    map[j][i] = 0;
                }
            }
        }
        filterRandom();
        filterLargestArea();
        getWater();
        getWalls();
        getStairs();
        //displayImage(map);
        //displayImage(getSimpleMap());
    }
    public int[][] getSimpleMap(){
        int[][] simpleMap = new int[map.length][map[0].length];
        for(int j = 1; j < map.length-1; j++) {
            for(int i = 1; i < map[0].length; i++) {
                if(map[j][i] <= 0) {
                    simpleMap[j][i]=0;
                }else {
                    simpleMap[j][i]=1;
                }
            }
        }
        return simpleMap;
    }

    public BufferedImage generateImageFromMatrix(int[][] tiles){
        int scale = 8;
        BufferedImage bi = new BufferedImage(tiles[0].length*scale,tiles.length*scale,BufferedImage.TYPE_INT_RGB);
        for(int j = 0; j < bi.getHeight(); j++){
            for(int i = 0; i < bi.getWidth(); i++){
                if(tiles[j/scale][i/scale]==0){
                    bi.setRGB(i, j, new Color(50,50,50).getRGB());
                }else if(tiles[j/scale][i/scale]<0){
                    bi.setRGB(i, j, new Color(255,0,0).getRGB());
                }else if(tiles[j/scale][i/scale]==2){
                    bi.setRGB(i, j, new Color(0,255,255).getRGB());
                }else if(tiles[j/scale][i/scale]==3){
                    bi.setRGB(i, j, new Color(0,255,0).getRGB());
                }else if(tiles[j/scale][i/scale]==4){
                    bi.setRGB(i, j, new Color(0,0,255).getRGB());
                }else{
                    bi.setRGB(i, j, new Color(0,0,0).getRGB());
                }

            }
        }
        return bi;
    }
    public void displayImage(int[][] tiles){
        JFrame frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel imageContainer = new JLabel(new ImageIcon(generateImageFromMatrix(tiles)));
        frame.add(imageContainer);
        frame.setVisible(true);
        frame.pack();
    }

    @SuppressWarnings("unused")
    public void displayImage(){
        displayImage(map);
    }

    public void filterRandom() {
        int tally;
        for (int j = 1; j < map.length-1; j++) {
            //this is to help remove the diagonal trend that occurs in a normal loop
            if(j%2==0) {
                //cycle from right to left on evens
                for (int i = map[0].length-2; i >=1 ; i--) {
                    if (map[j][i] == 1) {
                        tally = getTally(i,j);
                        makeChoice(tally,i,j);
                    }
                }
            }else {
                //cycle from left to right on odds
                for (int i = 1; i < map[0].length-1; i++) {
                    if (map[j][i] == 1) {
                        tally = getTally(i,j);

                        makeChoice(tally,i,j);
                    }
                }

            }
        }
    }
    private void getWater() {
        ArrayList<ArrayList<Point>> areas = this.getWaterAreas();
        ArrayList<Point> waterArea = areas.get(r.nextInt(areas.size()));
        for(Point p : waterArea) {
            map[p.y][p.x]= 2;
        }

    }

    private int getTally(int i, int j) {
        int tally = 0;
        if(map[j - 1][i] == 0){
            tally++;
        }
        if(map[j][i + 1] == 0) {
            tally++;
        }
        if(map[j + 1][i] == 0) {
            tally++;
        }
        if(map[j][i - 1] == 0) {
            tally++;
        }
        return tally;
    }
    private void makeChoice(int tally, int i, int j) {
        if (tally>=2) {
            int choice = r.nextInt(4);
            if(choice == 0) {
                map[j - 1][i] = 1;
                map[j][i+1] = 1;
            }else if(choice ==1) {
                map[j][i+1] = 1;
                map[j+1][i] = 1;
            }else if(choice ==2) {
                map[j+1][i] = 1;
                map[j][i-1] = 1;
            }else{
                map[j][i-1] = 1;
                map[j+1][i] = 1;
            }
        }
    }

    private void getWalls() {
        //make world border
        for(int j = 0; j < map.length; j++) {
            map[j][0] = 0;
            map[j][map[0].length-1] = 0;
        }
        for(int i = 0; i < map[0].length; i++) {
            map[0][i] = 0;
            map[map.length-1][i] = 0;
        }
        for(int j = 0; j < map.length; j++) {
            for(int i = 0; i < map[0].length; i++) {
                if(map[j][i]==0) {
                    setWallValue(i,j);
                }
            }
        }
    }

    private void setWallValue(int i, int j) {
        if(map[j][i]==0) {
            if(j>0) {
                if(map[j-1][i]==1) {
                    map[j][i] -= 1;
                }
            }
            if(i<map[0].length-2) {
                if(map[j][i+1]==1) {
                    map[j][i] -= 2;
                }
            }
            if(j<map.length-2) {
                if(map[j+1][i]==1) {
                    map[j][i] -= 4;
                }
            }
            if(i>0) {
                if(map[j][i-1]==1) {
                    map[j][i] -= 8;
                }
            }
        }
    }

    public void getStairs() {
        boolean fits;

        ArrayList<Point> possiblePoints = new ArrayList<>();
        for(int j = 1; j < map.length-1; j++) {
            for(int i = 1; i < map[0].length-1; i++) {
                fits = map[j-1][i]==1;
                fits = map[j-1][i+1]==1&&fits;
                fits = map[j][i+1]==1&&fits;
                fits = map[j+1][i+1]==1&&fits;
                fits = map[j+1][i]==1&&fits;
                fits = map[j+1][i-1]==1&&fits;
                fits = map[j][i-1]==1&&fits;
                fits = map[j+1][i-1]==1&&fits;
                if(fits) {
                    possiblePoints.add(new Point(i,j));
                }
            }
        }
        if(possiblePoints.size()<2) {
            //try again :(
            generateMap();
        }
        Point upstairs = possiblePoints.get(r.nextInt(possiblePoints.size()));
        possiblePoints.remove(upstairs);
        Point downstairs = possiblePoints.get(r.nextInt(possiblePoints.size()));
        possiblePoints.remove(downstairs);
        map[upstairs.y][upstairs.x] = 3;
        map[downstairs.y][downstairs.x] = 4;

    }

    public ArrayList<Point> getConnections(ArrayList<Point> connections, Point p) {
        Point connection;
        if(p.y>0&&p.y<map.length-1&&p.x>0&&p.x<map[0].length-1) {
            if(map[p.y-1][p.x]==1) {
                connection = new Point(p.x,p.y-1);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPoints.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y][p.x+1]==1) {
                connection = new Point(p.x+1,p.y);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPoints.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y+1][p.x]==1) {
                connection = new Point(p.x,p.y+1);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPoints.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y][p.x-1]==1) {
                connection = new Point(p.x-1,p.y);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPoints.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
        }
        return connections;
    }

    //TODO: revise and implement
    @SuppressWarnings("unused")
    public ArrayList<Point> getConnectionsWater(ArrayList<Point> connections, Point p) {
        Point connection;
        if(p.y>0&&p.y<map.length-1&&p.x>0&&p.x<map[0].length-1) {
            if(map[p.y-1][p.x]==0) {
                connection = new Point(p.x,p.y-1);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPointsWater.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y][p.x+1]==0) {
                connection = new Point(p.x+1,p.y);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPointsWater.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y+1][p.x]==0) {
                connection = new Point(p.x,p.y+1);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPointsWater.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
            if(map[p.y][p.x-1]==0) {
                connection = new Point(p.x-1,p.y);
                if(untriedPoints.contains(connection)) {
                    connections.add(connection);
                    untriedPointsWater.remove(connection);
                    connections = getConnections(connections,connection);
                }
            }
        }
        return connections;
    }

    public ArrayList<ArrayList<Point>> getAreas(){
        ArrayList<ArrayList<Point>> areas = new ArrayList<>();
        untriedPoints = new ArrayList<>();

        for(int j = 0; j < map.length;j++) {
            for(int i = 0; i < map[0].length; i++) {
                if(map[j][i]==1) {
                    untriedPoints.add(new Point(i,j));
                }
            }
        }
        for(int i = 0; i < untriedPoints.size(); i++) {
            System.out.println("New area Found: " + i);
            areas.add(getConnections(new ArrayList<>(), untriedPoints.get(i)));
        }

        return areas;
    }
    public ArrayList<ArrayList<Point>> getWaterAreas(){
        ArrayList<ArrayList<Point>> areas = new ArrayList<>();
        untriedPointsWater = new ArrayList<>();

        for(int j = 0; j < map.length;j++) {
            for(int i = 0; i < map[0].length; i++) {
                if(map[j][i]==0) {
                    untriedPointsWater.add(new Point(i,j));
                }
            }
        }
        for(int i = 0; i < untriedPoints.size(); i++) {
            System.out.println("New Possible Water Found: " + i);
            areas.add(getConnections(new ArrayList<>(), untriedPoints.get(i)));
        }

        return areas;
    }

    public void filterLargestArea() {
        ArrayList<ArrayList<Point>> areas = getAreas();
        ArrayList<Point> largest = new ArrayList<>();
        for(ArrayList<Point> area : areas) {
            if(area.size()>largest.size()) {
                largest = area;
            }
        }
        for(int j = 0; j < map.length; j++) {
            for(int i = 0; i < map[0].length; i++) {
                map[j][i] = 0;
            }
        }
        for(Point p : largest) {
            map[p.y][p.x] = 1;
        }
    }



    public static void main(String[] args) {
        DungeonGenerator dg = new DungeonGenerator();
        int[][] maze = dg.getSimpleMap();
        new MazeSolver(maze);
    }

}
