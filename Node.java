/*
 * The node class that represents the each searchable square. Represents
 * grid positions and helps to recreate the path once finished.
 * by Justin Tang
 */

public class Node {
  private int x;
  private int y;
  private int f, g, h;
  private Node parent;

  /*
   * The node constructor that creates the x and y positions.
   */
  public Node(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /*
   * Various getter functions to get the positions of the node, get the
   * heuristic/cost calculations, and the parent of this node.
   */

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getF() {
    return f;
  }

  public int getG() {
    return g;
  }

  public int getH() {
    return h;
  }

  public Node getParent() {
    return parent;
  }

  /*
   * Various setter functions to retrieve information about this node including
   * positions, heuristic calculations, and parent.
   */

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setF(int f) {
    this.f = f;
  }

  public void setG(int g) {
    this.g = g;
  }

  public void setH(int h) {
    this.h = h;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) {
      return false;
    }

    Node tmp = (Node) obj;
    if(this.x == tmp.getX() && this.y == tmp.getY()) {
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    //return "F Cost: " + f + " G Cost: " + g + " H Cost: " + h + "\n";
    return "X: " + x + " Y: " + y + "\n";
  }
}
