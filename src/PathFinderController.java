/*
 * The GUI and driver of the program. Creates the grid world, lays the nodes
 * to search, etc. Idea from Devon Crawford.
 * by Justin Tang
 */

// TODO: add functionality for other search algorithms too

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PathFinderController extends JPanel implements ActionListener,
                              MouseListener, KeyListener, MouseMotionListener {
  private AStarFinder path;
  private JPanel pane;
  private JFrame window;

  private Timer timer;

  private Node start;
  private Node end;

  private char keyPress;
  private boolean isOctile;
  private boolean isManhattan;

  private static final int CANVAS_WIDTH = 1250;
  private static final int CANVAS_HEIGHT = 700;
  private static final int NODE_SIZE = 25;

  public PathFinderController() {
    setLayout(null);

    addMouseListener(this);
    addKeyListener(this);
    addMouseMotionListener(this);

    setFocusable(true);

    pane = new JPanel();

    timer = new Timer(100, this);

    isOctile = true;
    isManhattan = false;

    //set up window
    window = new JFrame();
    window.setContentPane(this);
    window.setTitle("Pathfinding");
    window.getContentPane().setPreferredSize(new Dimension(CANVAS_WIDTH,
                                              CANVAS_HEIGHT));
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);

    path = new AStarFinder(this);

    //show changes to window
    this.revalidate();
    this.repaint();

    timer.start();
  }
  
  @Override
  public void paintComponents(Graphics g) {
    super.paintComponent(g);
  }

  public boolean isOctile() {
    return isOctile;
  }

  /*
   * All the graphics will be created using this method. Changing certain
   * things about the grid will prompt this method to be called again and
   * will recreate the grid reflecting the changes.
   */
  @Override
  public void paint(Graphics g) {
    //Draw grid for pathfind
    g.setColor(Color.lightGray);
    for(int j = 0; j < this.getHeight(); j += NODE_SIZE) {
      for(int i = 0; i < this.getWidth(); i += NODE_SIZE) {
        g.drawRect(i, j, NODE_SIZE, NODE_SIZE);
      }
    }

    //draw the wall nodes
    Set<Point> wallList = path.getWall();
    g.setColor(Color.black);
    for(Point pt : wallList) {
      int xCoord = (int) pt.getX();
      int yCoord = (int) pt.getY();

      g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw open list
    PriorityQueue<Node> openList = path.getOpen();
    g.setColor(new Color(132, 255, 138));
    for(Node e : openList) {
      g.fillRect(e.getX() + 1, e.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw closed list
    Set<Point> closedList = path.getClosed();
    g.setColor(new Color(253, 90, 90));
    for(Point pt : closedList) {
      int xCoord = (int) pt.getX();
      int yCoord = (int) pt.getY();

      g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw final path
    ArrayList<Node> finalPath = path.getFinal();
    g.setColor(new Color(32, 233, 255));
    for(int i = 0; i < finalPath.size(); i++) {
      g.fillRect(finalPath.get(i).getX() + 1, finalPath.get(i).getY() + 1,
                 NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw the start node
    if(start != null) {
      g.setColor(Color.blue);
      g.fillRect(start.getX() + 1, start.getY() + 1, NODE_SIZE - 2, NODE_SIZE -
          2);
    }

    //draw the end node
    if(end != null) {
      g.setColor(Color.red);
      g.fillRect(end.getX() + 1, end.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }
  }

  /*
   * After mouse actions, will trigger this method and make appropriate
   * calculations and nodes. For example, clicking will make walls, etc.
   */
  public void gridWork(MouseEvent e) {

    //if mouse click was left click
    if(e.getButton() == MouseEvent.BUTTON1) {

      //mouse clicks not exactly at node point of creation, so find remainder
      int xOver = e.getX() % NODE_SIZE;
      int yOver = e.getY() % NODE_SIZE;

      //s key and left mouse makes start node
      if(keyPress == 's') {

        int xTmp = e.getX() - xOver;
        int yTmp = e.getY() - yOver;

        //if start null, create start on nodes where end not already
        if(start == null) {

          if(!path.isWall(new Point(xTmp, yTmp))) {
            if(end == null) {
              start = new Node(xTmp, yTmp);
            } else {
              if(!end.equals(new Node(xTmp, yTmp))) {
                start = new Node(xTmp, yTmp);
              }
            }
          }

        //otherwise, do not move start to where end is
        } else {
          

          if(!path.isWall(new Point(xTmp, yTmp))) {
            if(end == null) {
              start.setXY(xTmp, yTmp);
            } else {
              if(!end.equals(new Node(xTmp, yTmp))) {
                start.setXY(xTmp, yTmp);
              }
            }
          }

        }

        repaint();

      //e key and left mouse makes end node
      } else if(keyPress == 'e') {
        
        int xTmp = e.getX() - xOver;
        int yTmp = e.getY() - yOver;

        //if end null, create end on nodes where start not already
        if(end == null) {
          
          if(!path.isWall(new Point(xTmp, yTmp))) {
            if(start == null) {
              end = new Node(xTmp, yTmp);
            } else {
              if(!start.equals(new Node(xTmp, yTmp))) {
                end = new Node(xTmp, yTmp);
              }
            }
          }

        //otherwise, do not move end to where start is
        } else {
          
          if(!path.isWall(new Point(xTmp, yTmp))) {
            if(start == null) {
              end.setXY(xTmp, yTmp);
            } else {
              if(!start.equals(new Node(xTmp, yTmp))) {
                end.setXY(xTmp, yTmp);
              }
            }
          }

        }

        repaint();

      //d key and left mouse deletes nodes
      } else if(keyPress == 'd') {
        //delete walls with this function if no right click
        int nodeX = e.getX() - xOver;
        int nodeY = e.getY() - yOver;

        if(start != null && start.equals(new Node(nodeX, nodeY))) {
          start = null;
        } else if(end != null && end.equals(new Node(nodeX, nodeY))) {
          end = null;
        } else {
          path.removeWall(new Point(nodeX, nodeY));
        }

        repaint();

      //just mouse click makes walls
      } else {
        //create walls and add to wall list
        Node tmpWall = new Node(e.getX() - xOver, e.getY() - yOver);

        if(start == null && end == null) {
          path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
        }

        if(!(tmpWall.equals(start)) && !(tmpWall.equals(end))) {
          path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
        }

        repaint();
      }

    //if mouse click was right click
    } else if(e.getButton() == MouseEvent.BUTTON1) {
      //delete nodes with right click
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    timer.setDelay(50);

    //check if is running, then do the pathfinding steps
    //if(isrunning)
    //  call one step of pathfinding
    if(path.isRun() && !path.isComplete() && !path.isPause()) {
      path.aStarPath();
      //path.printOpen();
    }
    repaint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    //all mouse clicks to change grid somehow
    if(!path.isRun()) {
      gridWork(e);
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  @Override
  public void mousePressed(MouseEvent e) {
    
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    
  }

  @Override
  public void keyPressed(KeyEvent e) {
    //get keyPress to know if we should make start, end, or delete perhaps
    keyPress = e.getKeyChar();

    if(keyPress == KeyEvent.VK_SPACE) {
      //start algorithm or stop it
      //set start and end node for path

      //on first run, set start and end; do again when finish algorithm
      if(!path.isComplete()) {
        if(!path.isRun()) {
          if(start != null && end != null) {
            path.setStart(start);
            path.setEnd(end);
            path.setisRun(true);
          }
        }
      }

      //press space to pause the algorithm
      if(!path.isPause()) {
        path.setisPause(true);
      } else if(path.isPause()) {
        path.setisPause(false);
      }
      
    } else if(keyPress == 'c') {
      //command to clear and reset
      path.reset();
      repaint();
    } else if(keyPress == 'o') {

      if(!path.isRun()) {
        isOctile = true;
        isManhattan = false;

        System.out.println("Using OCTILE distance\n");
      }

    } else if(keyPress == 'm') {

      if(!path.isRun()) {
        isManhattan = true;
        isOctile = false;

        System.out.println("Using MANHATTAN distance\n");
      }

    } else if(keyPress == KeyEvent.VK_BACK_SPACE) {

      if(!path.isRun()) {
        path.deleteWalls(true);
        path.reset();

        System.out.println("deleted all walls\n");
      }

    } else if(keyPress == '3') {

      if(!path.isRun()) {
        path.setisDijkstra(true);
        System.out.println("Running Dijkstra\n");
      }

    } else if(keyPress == '4') {

      if(!path.isRun()) {
        path.setisDijkstra(false);
        System.out.println("Running A Star\n");
      }

    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keyPress = 0;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if(!path.isRun()) {
      gridWork(e);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    
  }

  public static void main(String[] args) {
    new PathFinderController();
  }
}
