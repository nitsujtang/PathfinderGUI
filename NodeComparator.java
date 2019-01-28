/*
 * Comparator class comparing nodes for our priority queue representing the
 * open list. Want node with least F cost to be the one with highest priority.
 */

import java.util.*;
public class NodeComparator implements Comparator<Node> {
  public int compare(Node x, Node y) {
    if(x.getF() < y.getF()) {
      return -1;
    } else if(x.getF() > y.getF()) {
      return 1;
    }

    return 0;
  }
}
