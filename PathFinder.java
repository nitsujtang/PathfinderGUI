/*
 * The PathFinder classes contains the implementation of the A* pathfinding
 * algorithm and frequently communicates with the controller class.
 * Algorithm and read up on A* from: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#S7
 * Name: Justin Tang
 */

import java.util.*;
import java.awt.Point;

public class PathFinder {
  private ArrayList<Node> finalPath;
  //list of all walls on the grid
  //TODO use a set of Location objects for walls
  //private ArrayList<Node> wall;
  private Set<Point> wall;

  //data structures for A* pathfinding
  private PriorityQueue<Node> open;
  //TODO use a set of Location objects for closed list
  //private ArrayList<Node> closed;
  private Set<Point> closed;

  //data structures for DFS

  //data structures for BFS

  //data structures for Dijkstra

  private PathFinderController control;
  private Node start;
  private Node end;

  private boolean isPause;
  private boolean run;

  private int diagonalMove;
  private int nodeSize;
  
  public PathFinder() {
    closed = new HashSet<Point>();
  }

  public PathFinder(PathFinderController control) {
    this.control = control;
    nodeSize = 25;

    run = false;
    isPause = true;

    diagonalMove = (int) (Math.sqrt(2 * (Math.pow(nodeSize, 2))));

    open = new PriorityQueue<Node>(new NodeComparator());
    //closed = new ArrayList<Node>();
    //wall = new ArrayList<Node>();
    closed = new HashSet<Point>();
    wall = new HashSet<Point>();

    finalPath = new ArrayList<Node>();
  }

  /*
   * Various getter methods to get the various lists containing the nodes.
   */
  public Set<Point> getWall() {
    return wall;
  }

  public PriorityQueue<Node> getOpen() {
    return open;
  }

  public Set<Point> getClosed() {
    return closed;
  }

  public ArrayList<Node> getFinal() {
    return finalPath;
  }

  /*
   * Checks to see if the list of walls contains a certain node.
   */
  public boolean isWall(Point pt) {
    return wall.contains(pt);
  }

  /*
   * Contains method to see if nodes are in the closed list.
   */
  public boolean closedContains(Point pt) {
    return closed.contains(pt);
  }

  public boolean closedRemove(Point pt) {
    return closed.remove(pt);
  }

  /*
   * Contains method to see if nodes are in the open list.
   */
  public boolean openContains(Node n) {
    return open.contains(n);
  }

  public boolean openRemove(Node n) {
    return open.remove(n);
  }

  /*
   * Adds a wall to the wall list if a wall at the same location is not
   * already present.
   */
  public boolean addWall(Point pt) {
    return wall.add(pt);
  }

  /*
   * Removes a wall node from the list of walls.
   */
  public boolean removeWall(Point pt) {
    return wall.remove(pt);
  }

  public void setStart(Node start) {
    this.start = new Node(start.getX(), start.getY());
    open.add(this.start);
  }

  public void setEnd(Node end) {
    this.end = new Node(end.getX(), end.getY());
  }

  public void setisPause(boolean isPause) {
    this.isPause = isPause;
  }

  public boolean isPause() {
    return isPause;
  }

  public void setisRun(boolean run) {
    this.run = run;
  }
  public boolean isRun() {
    return run;
  }

  /*
   * A* pathfinding algorithm. TODO MANY BUGS??
   */
  public void aStarPath() {
    //get node with lowest F cost off PQ
    Node current = open.poll();

    //if no min node, then no path
    if(current == null) {
      System.out.println("No path");
      run = false;
      isPause = true;
      return;
    }

    //if min node is the end, then stop algorithm and build final path
    if(current.equals(end)) {
      //stop algorithm
      //print final path
      end.setParent(current.getParent());
      run = false;
      isPause = true;
      control.repaint();
      constructPath();
      //System.out.println(finalPath);
      System.out.println(start.getX() + " " + start.getY());
      //System.out.println(open);
      //System.out.println(closed);
      System.out.println(open.size());
      System.out.println(closed.size());
      return;
    }

    closed.add(new Point(current.getX(), current.getY()));
    System.out.println("CURRENT: " + current + "\n");

    //calculate costs for the 8 possible adjacent nodes to current
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {

        //skip the current node we are exploring
        if(i == 1 && j == 1) {
          continue;
        }
        
        int xCoord = (current.getX() - nodeSize) + (nodeSize * i);
        int yCoord = (current.getY() - nodeSize) + (nodeSize * j);
        Node neighbor = new Node(xCoord, yCoord);

        //checks if node is within canvas boundary
        if(xCoord < 0 || yCoord < 0 || xCoord >= control.getWidth() || yCoord >=
          control.getHeight()) {
          continue;
        }

        //checks to see if the neighbor node is a wall, in the open/closed list
        if(isWall(new Point(neighbor.getX(), neighbor.getY()))) {
          continue;
        }

        int wallJumpX = current.getX() + (xCoord - current.getX());
        int wallJumpY = current.getY() + (yCoord - current.getY());

        //checks for border in adjacent pos, does not allow for a diagonal
        //jump across a border
        if(isWall(new Point(wallJumpX, current.getY())) || isWall(new
              Point(current.getX(), wallJumpY)) && ((j == 0 || j == 2) && i !=
              1)) {
          continue;
        }

        //calculate f, g, and h costs for this node
        int gCost = current.getG() + gCostMovement(current, neighbor);
        int hCost = hCostMovement(neighbor);
        int fCost = gCost + hCost;

        boolean inOpen = openContains(neighbor);
        boolean inClosed = closedContains(new Point(neighbor.getX(),
              neighbor.getY()));

        //if node in open and we found lower gCost, no need to search neighbor
        if(inOpen && (gCost < neighbor.getG())) {
          openRemove(neighbor);
        }

        //if neighbor in closed and found lower gCost, visit again
        if(inClosed && (gCost < neighbor.getG())) {
          //closedRemove(new Point(neighbor.getX(), neighbor.getY()));
        }

        //if neighbor not visited, then add to open list
        if(!inOpen && !inClosed) {
          neighbor.setG(gCost);
          neighbor.setH(hCost);
          neighbor.setF(fCost);
          neighbor.setParent(current);

          open.add(neighbor);
        }
      }
    }
  }

  /*
   * Method finds the cost associated with moving from the current node to
   * the neighbor node. Uses the formula for the distance between two points.
   */
  public int gCostMovement(Node parent, Node neighbor) {
    //distance from point to point in a grid
    int xCoord = neighbor.getX() - parent.getX();
    int yCoord = neighbor.getY() - parent.getY();

    return (int) (Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2)));
  }

  /*
   * Method finds the heuristic cost from the neighbor node to the end node.
   * From the Stanford page: "Here we compute the number of steps you take if 
   * you can’t take a diagonal, then subtract the steps you save by using the 
   * diagonal. There are min(dx, dy) diagonal steps, and each one costs D2 but 
   * saves you 2⨉D non-diagonal steps."
   * To break ties in fCost, we slightly weight the hCost so that we expand
   * vertices closer to the end node.
   */
  public int hCostMovement(Node neighbor) {
    int hXCost = Math.abs(end.getX() - neighbor.getX());
    int hYCost = Math.abs(end.getY() - neighbor.getY());
    int hCost = nodeSize * Math.max(hXCost, hYCost) + (diagonalMove - nodeSize)
                * Math.min(hXCost, hYCost);
    return (int) (hCost *= (1 + (1.0 / 1000)));
  }

  /*
   * Constructs the final path from start to end node. Only called once a
   * valid path is found.
   */
  public void constructPath() {
    Node current = end;
    while(!(current.getParent().equals(start))) {
      finalPath.add(0, current);
      current = current.getParent();
    }

    finalPath.add(0, current);
  }
}
