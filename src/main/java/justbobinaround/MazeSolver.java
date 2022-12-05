package justbobinaround;
import java.awt.Point;
import java.util.ArrayList;

public class MazeSolver {
    ArrayList<MazeVectorSet> vectorSets = new ArrayList<>();

    public MazeSolver(int[][] maze) {
        System.out.println("Loading");
        for(int j = 0; j < maze.length; j++) {
            for(int i = 0; i < maze[0].length; i++) {
                if(maze[j][i]==1) {
                    vectorSets.add(new MazeVectorSet(maze,new Point(i,j)));
                }
            }
        }


    }



}

class MazeVector{
    public static final int WALL = -2;
    public static final int VOID = -1;
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public int direction = -1;
    public int value;

    public MazeVector(int value) {
        this.value = value;
    }


}

class MazeVectorSet{
    public MazeVector[][] vectorSet;
    public Point focalPoint;
    public MazeVectorSet(int[][] maze, Point p) {
        if(maze[p.y][p.x]==1) {
            focalPoint = p;
            vectorSetInit(maze, p);
            while(hasOpen()) {
                p = nextLargestOpenVector();
                getNext(p);
            }
            this.clearDirections();
            this.getDirections();
            this.printVectorSetDir();
            System.out.println();
        }
    }

    @SuppressWarnings("unused")
    public void printVectorSet() {
        for (MazeVector[] mazeVectors : vectorSet) {
            for (int i = 0; i < vectorSet[0].length; i++) {
                System.out.print(mazeVectors[i].value + "\t");
            }
            System.out.println();
        }
    }
    public void printVectorSetDir() {
        for (MazeVector[] mazeVectors : vectorSet) {
            for (int i = 0; i < vectorSet[0].length; i++) {
                if (mazeVectors[i].direction == MazeVector.WALL) {
                    System.out.print("  ");
                }
                if (mazeVectors[i].direction == MazeVector.VOID) {
                    System.out.print("* ");
                }
                if (mazeVectors[i].direction == MazeVector.UP) {
                    System.out.print("U ");
                }
                if (mazeVectors[i].direction == MazeVector.RIGHT) {
                    System.out.print("R ");
                }
                if (mazeVectors[i].direction == MazeVector.DOWN) {
                    System.out.print("D ");
                }
                if (mazeVectors[i].direction == MazeVector.LEFT) {
                    System.out.print("L ");
                }


            }
            System.out.println();
        }
    }

    private void clearDirections() {
        for (MazeVector[] mazeVectors : vectorSet) {
            for (int i = 0; i < vectorSet[0].length; i++) {
                if (mazeVectors[i].direction != MazeVector.WALL) {

                    mazeVectors[i].direction = MazeVector.VOID;
                }
            }
        }
    }
    private void getDirections() {
        int largestDir;

        for(int j = 1; j < vectorSet.length-1; j++) {
            for(int i = 1; i < vectorSet[0].length-1; i++) {
                if(hasNoDirection(i,j)) {
                    largestDir = 0;
                    if(vectorSet[j-1][i].value>largestDir) {
                        largestDir = vectorSet[j-1][i].value;
                    }
                    if(vectorSet[j][i+1].value>largestDir) {
                        largestDir = vectorSet[j][i+1].value;
                    }
                    if(vectorSet[j+1][i].value>largestDir) {
                        largestDir = vectorSet[j+1][i].value;
                    }
                    if(vectorSet[j][i-1].value>largestDir) {
                        largestDir = vectorSet[j][i-1].value;
                    }

                    if(vectorSet[j-1][i].value==largestDir) {
                        vectorSet[j][i].direction = MazeVector.UP;
                    }else if(vectorSet[j][i+1].value==largestDir) {
                        vectorSet[j][i].direction = MazeVector.RIGHT;
                    }else if(vectorSet[j+1][i].value==largestDir) {
                        vectorSet[j][i].direction = MazeVector.DOWN;
                    }else if(vectorSet[j][i-1].value==largestDir) {
                        vectorSet[j][i].direction = MazeVector.LEFT;
                    }
                }
            }
        }
        vectorSet[focalPoint.y][focalPoint.x].direction = MazeVector.VOID;
    }
    private boolean hasNoDirection(Point p) {
        return vectorSet[p.y][p.x].direction==MazeVector.VOID;
    }
    private boolean hasNoDirection(int x, int y) {
        return hasNoDirection(new Point(x,y));
    }
    private boolean hasOpen() {
        for (MazeVector[] mazeVectors : vectorSet) {
            for (int i = 0; i < vectorSet[0].length; i++) {
                if (mazeVectors[i].value == -1) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hasOpenNeighbor(Point p) {
        boolean result = false;
        if(p.y>=0 && p.y<vectorSet.length-1 && p.x>=0 && p.x<vectorSet[0].length-1) {
            if(p.y>0) {
                result = vectorSet[p.y-1][p.x].value==-1;
            }
            if(p.x<vectorSet[0].length-2) {
                result = vectorSet[p.y][p.x+1].value==-1 || result;
            }
            if(p.y<vectorSet.length-2) {
                result = vectorSet[p.y+1][p.x].value==-1 || result;
            }
            if(p.x>0) {
                result = vectorSet[p.y][p.x-1].value==-1 || result;
            }
        }

        return result;
    }
    private boolean hasOpenNeighbor(int x, int y) {
        return hasOpenNeighbor(new Point(x,y));
    }
    private Point nextLargestOpenVector() {
        MazeVector largest = new MazeVector(0);
        Point p = new Point();
        for(int j = 0; j < vectorSet.length; j++) {
            for(int i = 0; i < vectorSet[0].length; i++) {
                if(hasOpenNeighbor(i,j)) {
                    if(vectorSet[j][i].value>=largest.value) {
                        largest = vectorSet[j][i];
                        p = new Point(i,j);
                        //System.out.println(largest.value+" : ("+p.x + "," + p.y+")");
                    }
                }
            }
        }
        return p;
    }

    private void getNext(Point p) {
        if(p.y>=0 && p.y<vectorSet.length-1 && p.x>=0 && p.x<vectorSet[0].length-1) {
            int pastValue = vectorSet[p.y][p.x].value;
            boolean done = false;
            if(p.y>0) {
                if(vectorSet[p.y-1][p.x].value==-1) {
                    vectorSet[p.y][p.x].direction = MazeVector.UP;
                    vectorSet[p.y-1][p.x].value = pastValue-1;
                    getNext(new Point(p.x,p.y-1), MazeVector.UP);
                    done=true;
                }
            }
            if(p.x<vectorSet[0].length-2&&!done) {
                if(vectorSet[p.y][p.x+1].value==-1) {
                    vectorSet[p.y][p.x].direction = MazeVector.RIGHT;
                    vectorSet[p.y][p.x+1].value = pastValue-1;
                    getNext(new Point(p.x+1,p.y), MazeVector.RIGHT);
                    done=true;
                }
            }
            if(p.y<vectorSet.length-2&&!done) {
                if(vectorSet[p.y+1][p.x].value==-1) {
                    vectorSet[p.y][p.x].direction = MazeVector.DOWN;
                    vectorSet[p.y+1][p.x].value = pastValue-1;
                    getNext(new Point(p.x,p.y+1), MazeVector.DOWN);
                    done = true;
                }
            }
            if(p.x>0&&!done) {
                if(vectorSet[p.y][p.x-1].value==-1) {
                    vectorSet[p.y][p.x].direction = MazeVector.LEFT;
                    vectorSet[p.y][p.x-1].value = pastValue-1;
                    getNext(new Point(p.x-1,p.y), MazeVector.LEFT);
                }
            }

        }
    }

    private void getNext(Point p, int direction) {
        if(p.y>=0 && p.y<vectorSet.length-1 && p.x>=0 && p.x<vectorSet[0].length-1) {
            vectorSet[p.y][p.x].direction = direction;
            int pastValue = vectorSet[p.y][p.x].value;
            boolean done = false;
            if(p.y>0) {
                if(direction==MazeVector.UP&&vectorSet[p.y-1][p.x].value==-1) {
                    vectorSet[p.y-1][p.x].value = pastValue-1;
                    getNext(new Point(p.x,p.y-1), direction);
                    done = true;
                }
            }
            if(p.x<vectorSet[0].length-2 && !done) {
                if(direction==MazeVector.RIGHT&&vectorSet[p.y][p.x+1].value==-1) {
                    vectorSet[p.y][p.x+1].value = pastValue-1;
                    getNext(new Point(p.x+1,p.y), direction);
                    done = true;
                }
            }
            if(p.y<vectorSet.length-2 && !done) {
                if(direction==MazeVector.DOWN&&vectorSet[p.y+1][p.x].value==-1) {
                    vectorSet[p.y+1][p.x].value = pastValue-1;
                    getNext(new Point(p.x,p.y+1), direction);
                    done = true;
                }
            }
            if(p.x>0 && !done) {
                if(direction==MazeVector.LEFT&&vectorSet[p.y][p.x-1].value==-1) {
                    vectorSet[p.y][p.x-1].value = pastValue-1;
                    getNext(new Point(p.x-1,p.y), direction);
                }
            }
        }
    }

    private void vectorSetInit(int[][] maze, Point p) {
        vectorSet = new MazeVector[maze.length][maze[0].length];
        int sum  = 0;

        for(int j = 0; j < maze.length; j++) {
            for(int i = 0; i < maze[0].length; i++) {
                if(maze[j][i]==0) {
                    vectorSet[j][i] = new MazeVector(0);
                    vectorSet[j][i].direction = MazeVector.WALL;
                }else {
                    vectorSet[j][i] = new MazeVector(-1);
                    sum++;
                }
            }
        }
        vectorSet[p.y][p.x].value = sum+1;
    }
}
