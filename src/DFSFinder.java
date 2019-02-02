/*
 * The DFSFinder class contains the implementation of the DFS search algorithm
 * and communicates with the PathFinderController class.
 *
 * By: Justin Tang
 */
import java.util.*;
import java.awt.Point;

public class DFSFinder {
  //list for finalPath creation and keeping track of walls
  private ArrayList<Node> finalPath;
  private Set<Point> wall;

  //data structures for DFS search
  private Stack<Node> open;
  private Set<Point> closed;

  private PathFinderController control;

  private Node start;
  private Node end;

  public DFSFinder(PathFinderController control) {
    this.control = control;

    finalPath = new ArrayList<Node>();
    wall = new HashSet<Point>();
    open = new Stack<Node>();
    closed = new HashSet<Point>();
  }
}
