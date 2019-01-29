/*
 * The PathFinder classes contains the implementation of the A* pathfinding
 * algorithm and frequently communicates with the controller class.
 * Algorithm and read up on A* from: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#S7
 * Name: Justin Tang
 */

//TODO add feature to draw another path when completed and compare costs between
//found path and drawn path?
import java.util.*;
import java.awt.Point;

public class PathFinder {
  private ArrayList<Node> finalPath;

  //list of all walls on the grid
  private Set<Point> wall;

  //data structures for A* pathfinding
  private PriorityQueue<Node> open;
  private Set<Point> closed;

  //data structures for DFS

  //data structures for BFS

  //data structures for Dijkstra

  private PathFinderController control;
  private Node start;
  private Node end;

  private boolean isPause;
  private boolean run;
  private boolean complete;

  private double diagonalMove;
  private int nodeSize;
  
  public PathFinder(PathFinderController control) {
    this.control = control;
    nodeSize = 25;

    run = false;
    isPause = true;

    diagonalMove = (int) (Math.sqrt(2 * (Math.pow(nodeSize, 2))));

    open = new PriorityQueue<Node>(new NodeComparator());
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

  public Node openFind(Node n) {
    for(Node x : open) {
      if(x.equals(n)) {
        return x;
      }
    }

    return null;
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

  public void reset() {
    run = false;
    isPause = true;
    complete = false;

    wall.clear();
    closed.clear();
    open.clear();
    finalPath.clear();
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

  public boolean isComplete() {
    return complete;
  }

  /*
   * A* pathfinding algorithm. Tries to explore the fewest number of nodes to
   * reach the end node. Self corrects the path to the end node using the
   * heuristic cost function h.
   *
   * TODO fix parent pointers?
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
      end.setParent(current.getParent());
      run = false;
      isPause = true;
      complete = true;
      control.repaint();
      constructPath();
      System.out.println("Total Cost of Path: " + end.getParent().getG());
      System.out.println("Size of Open: " + open.size());
      System.out.println("Size of Closed: " + closed.size());
      System.out.println("Size of Path: " + finalPath.size() + "\n");
      return;
    }

    closed.add(new Point(current.getX(), current.getY()));

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
              Point(current.getX(), wallJumpY)) && ((j == 0 | j == 2) && i != 1)) {
          continue;
        }

        //calculate f, g, and h costs for this node
        double gCost = current.getG() + gCostMovement(current, neighbor);
        double hCost = hCostMovement(neighbor);
        double fCost = gCost + hCost;

        boolean inOpen = openContains(neighbor);
        boolean inClosed = closedContains(new Point(neighbor.getX(),
              neighbor.getY()));
        Node found = openFind(neighbor);
        if(found != null)
          System.out.println("Found GCost: " + found.getG());

        //if inOpen and inClosed cases just in case, should not happen
        //if node in open and we found lower gCost, no need to search neighbor
        if(inOpen && (found != null) && (gCost < found.getG())) {
          openRemove(neighbor);
          //Node found = openFind(neighbor);

          neighbor.setG(gCost);
          neighbor.setF(gCost + found.getH());

          neighbor.setParent(current);

          open.add(neighbor);
          System.out.println("HEYOPEN");
          continue;
        }

        //if neighbor in closed and found lower gCost, visit again
        //if(inClosed && (gCost < neighbor.getG())) {
        if(inClosed && (gCost < neighbor.getG())) {
          //closedRemove(new Point(neighbor.getX(), neighbor.getY()));

          /*neighbor.setG(gCost);
          neighbor.setH(hCost);
          neighbor.setF(gCost + hCost);
          neighbor.setParent(current);*/

          //open.add(neighbor);
          System.out.println("HEYCLOSED");
          continue;
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

    //path correction for finding the shortest path
    if(!wall.isEmpty()) {
      //pathCorrection(open.peek());
    }
  }

  /*
   * Method finds the cost associated with moving from the current node to
   * the neighbor node. Uses the formula for the distance between two points.
   */
  public double gCostMovement(Node parent, Node neighbor) {
    //distance from point to point in a grid
    int xCoord = neighbor.getX() - parent.getX();
    int yCoord = neighbor.getY() - parent.getY();

    return (int) (Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2)));
    /*if(xCoord != 0 && yCoord != 0) {
      return diagonalMove;
    }

    return 25.0;*/
  }

  /*
   * Method finds the heuristic cost from the neighbor node to the end node.
   * From the Stanford page: "Here we compute the number of steps you take if 
   * you can’t take a diagonal, then subtract the steps you save by using the 
   * diagonal. There are min(dx, dy) diagonal steps, and each one costs D2 but 
   * saves you 2⨉D non-diagonal steps."
   * 
   * With no walls present, we can use a heuristic cost that factors in the
   * total weight of a moving one nodeSize distance or a diagonalMove distance.
   *
   * With walls present, we use octile distance in order to find the shortest
   * possible path with path corrections.
   */
  public double hCostMovement(Node neighbor) {
    int hXCost = Math.abs(end.getX() - neighbor.getX());
    int hYCost = Math.abs(end.getY() - neighbor.getY());
    double hCost = (nodeSize * Math.max(hXCost, hYCost)) + ((diagonalMove - nodeSize)
                * Math.min(hXCost, hYCost));

    if(hXCost > hYCost) {
      hCost = ((hXCost - hYCost) + Math.sqrt(2) * hYCost);
    } else {
      hCost = ((hYCost - hXCost) + Math.sqrt(2) * hXCost);
    }

    return hCost;
  }

  /*
   * Method checks nodes on the open list and allows for a possible path
   * correction so that we achieve the lowest cost. A possible reason for
   * why we may not achieve the lowest cost without this method is that we
   * deal with wall objects and our first greedy attempt at finding a path
   * may not work.
   */
  /*public void pathCorrection(Node parent) {
    //if parent null, then no path
    if(parent == null) {
      return;
    }

    //all possible neighbors to the current node, the parent
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        if(i == 1 && j == 1) {
          continue;
        }

        //find all adjacent x and y positions
        int xCoord = (parent.getX() - nodeSize) + (nodeSize * i);
        int yCoord = (parent.getY() - nodeSize) + (nodeSize * j);
        Node openNode = new Node(xCoord, yCoord);

        //check if an adjacent node in open list
        if(openContains(openNode)) {
          Node found = openFind(openNode);

          int gCost = (int) (parent.getG() + gCostMovement(parent, found));

          //calculate gCost from this current node to an open list node is
          //less, then we should use this node for our final path
          if(gCost < found.getG()) {
            open.remove(openNode);

            found.setG(gCost);
            found.setF(gCost + found.getH());
            found.setParent(parent);

            open.add(found);
          }
        }
      }
    }

  }*/

  /*
   * Constructs the final path from start to end node. Only called once a
   * valid path is found.
   */
  public void constructPath() {
    Node current = end.getParent();
    while(!(current.getParent().equals(start))) {
      finalPath.add(0, current);
      current = current.getParent();
    }

    finalPath.add(0, current);
  }
}
