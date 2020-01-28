import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Cell class used to populate the grid.
 */
class Cell {

  // **** members ****
  public int i; // row coordinate (vertical)
  public int j; // column coordinate (horizontal)

  public Cell parent; // parent for this cell

  public int heuristicCost;
  public int finalCost;

  public boolean solution; // cell is part of the solution path

  /**
   * Constructor.
   */
  public Cell(int i, int j) {
    this.i = i;
    this.j = j;
  }

  /**
   * Return string representation of Cell. (more fields to come)
   */
  public String toString() {
    return "(" + this.i + "," + this.j + ")";
  }
}

/**
 * A* (AStar) selects the path that minimizes:
 *
 * f(n) = g(n) + h(n)
 *
 * where n is the next node on the path, g(n) is the cost of the path from the
 * start node to n, and h(n) is a heuristic function that estimates the cost of
 * the cheapest path from n to the goal.
 */
public class AStar {

  // **** cost for diagonal and vertical / horizontal moves ****
  public static final int DIAGONAL_COST = 14;
  public static final int V_H_COST = 10;

  // **** cells for the grid ****
  private Cell[][] grid;

  // **** priority queue for open cells
  // open cells: set of noded to be evaluated
  // we insert cells with lowest cost first ****
  private PriorityQueue<Cell> openCells;

  // **** closed cells: flags if cell has already been evaluated evaluated ****
  private boolean[][] closedCells;

  // **** start cell coordinates ****
  private int startI;
  private int startJ;

  // **** end cell coordinates ****
  private int endI;
  private int endJ;

  /**
   * Constructor.
   */
  public AStar(int width, int height, int si, int sj, int ei, int ej, int[][] blocks) {

    // **** declare the grid based on the desired width and height ****
    grid = new Cell[width][height];

    // **** ****
    closedCells = new boolean[width][height];

    // **** priority queue with comparator ****
    openCells = new PriorityQueue<Cell>((Cell c1, Cell c2) -> {
      return c1.finalCost < c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0;
    });

    // **** set the coordinates for the start cell ****
    startCell(si, sj);

    // **** set the coordinates for the end cell ****
    endCell(ei, ej);

    // **** initialize cells ****
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {

        // **** cell for this grid location ****
        grid[i][j] = new Cell(i, j);

        // **** manhattan distance from this to the end cell ****
        grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ);

        // **** flag this cell is not part of solution yet ****
        grid[i][j].solution = false;
      }
    }

    // ???? ????
    System.out.println("heuristicCosts:");
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        System.out.printf("%-3d ", grid[i][j].heuristicCost);
      }
      System.out.println();
    }
    System.out.println();

    // **** *****
    grid[startI][startJ].finalCost = 0;

    // **** put blocks on the grid ****
    for (int i = 0; i < blocks.length; i++) {
      addBlockOnCell(blocks[i][0], blocks[i][1]);
    }
  }

  /**
   * Add a block to this cell in the grid. We do so by removing the cell from the
   * grid.
   */
  public void addBlockOnCell(int i, int j) {
    grid[i][j] = null;
  }

  /**
   * Set start cell coordinates.
   */
  public void startCell(int i, int j) {
    startI = i;
    startJ = j;
  }

  /**
   * Set end cell coordinates.
   */
  public void endCell(int i, int j) {
    endI = i;
    endJ = j;
  }

  /**
   * Update the cost of cell t and the parent. We will use the parent to display
   * the path from the end cell back to the start cell.
   */
  public void updateCostIfNeeded(Cell current, Cell t, int cost) {

    // **** check if there is no need to update cost ****
    if ((t == null) || closedCells[t.i][t.j]) {
      return;
    }

    // ???? ????
    System.out.print("updateCostIfNeeded <<< current: " + current.toString() + " t: " + t.toString());

    // **** ****
    int tFinalCost = t.heuristicCost + cost;

    // **** determine if this cell is in the queue ****
    boolean isOpen = openCells.contains(t);

    // **** add the updated cell to the priority queue (if needed) ****
    if (!isOpen || (tFinalCost < t.finalCost)) {

      // **** update the final cost and parent ****
      t.finalCost = tFinalCost;
      t.parent = current;

      // **** add the cell to the priority queue (if needed) ****
      if (!isOpen) {
        openCells.add(t);
      }
    }

    // ???? ????
    System.out.println(" tFinalCost: " + tFinalCost + " t.finalCost: " + t.finalCost);
  }

  /**
   * Search the grid for a path from the starting to the end cell.
   */
  public void search() {

    // // ???? ????
    // System.out.println("process <<< t: " + grid[startI][startJ].toString());

    // **** add start location to the open cells queue ****
    openCells.add(grid[startI][startJ]);

    // **** ****
    Cell current;

    // **** loop until queue is empty (will visit all cells) ****
    while (true) {

      // **** retrieve and remove the head cell from the queue ****
      current = openCells.poll();

      // **** check if we are done looping ****
      if (current == null) {
        break;
      }

      // **** ****
      closedCells[current.i][current.j] = true;

      // **** check if we reached the end cell ****
      if (current.equals(grid[endI][endJ])) {
        return;
      }

      // **** temporary cell ****
      Cell t;

      // **** top 3 cells ****
      if (current.i - 1 >= 0) {

        // **** top cell ****
        t = grid[current.i - 1][current.j];
        updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

        // **** top left diagonal cell ****
        if (current.j - 1 >= 0) {
          t = grid[current.i - 1][current.j - 1];
          updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
        }

        // **** top right diagonal cell ****
        if (current.j + 1 < grid[0].length) {
          t = grid[current.i - 1][current.j + 1];
          updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
        }
      }

      // **** left cell ****
      if (current.j - 1 >= 0) {
        t = grid[current.i][current.j - 1];
        updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
      }

      // **** right cell ****
      if (current.j + 1 < grid[0].length) {
        t = grid[current.i][current.j + 1];
        updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
      }

      // **** bottom 3 cells ****
      if (current.i + 1 < grid.length) {

        // **** bottom cell ****
        t = grid[current.i + 1][current.j];
        updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

        // **** bottom left diagonal cell ****
        if (current.j - 1 >= 0) {
          t = grid[current.i + 1][current.j - 1];
          updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
        }

        // **** bottom righ diagonal cell ****
        if (current.j + 1 < grid[0].length) {
          t = grid[current.i + 1][current.j + 1];
          updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
        }
      }

      // ???? ????
      System.out.println();
    }
  }

  /**
   * Display the grid of cells.
   */
  public void displayGrid() {

    // **** display label ****
    System.out.println("grid: ");

    // **** traverse the grid ****
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        if ((i == startI) && (j == startJ)) {
          System.out.print("SC  "); // start cell
        } else if ((i == endI) && (j == endJ)) {
          System.out.print("EC  "); // end cell
        } else if (grid[i][j] != null) {
          System.out.printf("%-3d ", 0); // open cell
        } else {
          System.out.print("BC  "); // blocked cell
        }

      }
      System.out.println();
    }
    System.out.println("0: Open Cell BC: Blocked Cell EC : End Cell CS: Start Cell\n");
  }

  /**
   * Display scores of cells.
   */
  public void displayScores() {

    // **** display a label ****
    System.out.println("scores:");

    // **** traverse the grid cells ****
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        if (grid[i][j] != null) {
          System.out.printf("%-3d ", grid[i][j].finalCost);
        } else {
          System.out.print("BC  "); // blocked cell
        }
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Display the points used in the path from the start to the end cell. Then
   * display the contents of the grid.
   */
  public void displayPath() {

    // **** end cell must be a closed cell ****
    if (closedCells[endI][endJ]) {

      // **** stack to reverse the path ****
      Stack<Cell> stack = new Stack<Cell>();

      // **** start with the end cell ****
      Cell current = grid[endI][endJ];

      // **** push the current cell into the stack ****
      stack.add(current);

      // **** flag that the current cell is part of the solution path ****
      grid[current.i][current.j].solution = true;

      // **** loop until we get to the start cell ****
      while (current.parent != null) {

        // **** push the parent cell into the stack ****
        stack.add(current.parent);

        // **** flag that the parent cell is part of the solution ****
        grid[current.parent.i][current.parent.j].solution = true;

        // **** update the current reference ****
        current = current.parent;
      }

      // **** display path from start to end cell ****
      System.out.print("path: ");
      while (!stack.isEmpty()) {
        Cell c = stack.pop();
        if (stack.size() > 0) {
          System.out.print(c + " -> ");
        } else {
          System.out.print(c);
        }
      }

      // **** for the looks ****
      System.out.println("\n\ngrid:");

      // **** traverse the grid cells ****
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[i].length; j++) {
          if ((i == startI) && (j == startJ)) {
            System.out.print("SC  "); // start cell
          } else if ((i == endI) && (j == endJ)) {
            System.out.print("EC  "); // end cell
          } else if (grid[i][j] != null) {
            System.out.printf("%-3s ", grid[i][j].solution ? "X" : "0");
          } else {
            System.out.print("BC  "); // blocked cell
          }
        }
        System.out.println();
      }
      System.out.println();
    } else {
      System.out.println("path from (" + startI + "," + startJ + ") to (" + endI + "," + endJ + ") NOT found :o(");
    }
  }

  /**
   * Display the array of closed cells.
   */
  public void displayClosedCells() {

    // **** display header ****
    System.out.println("closedCells:");

    // **** ****
    for (int i = 0; i < closedCells.length; i++) {
      for (int j = 0; j < closedCells[0].length; j++) {
        System.out.printf("%-3s ", closedCells[i][j] ? "T" : "F");
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Test scaffolding.
   */
  public static void main(String[] args) {

    // **** ****
    // AStar aStar = new AStar(5, 5, 0, 0, 3, 2,
    // new int[][] { { 0, 4 }, { 2, 2 }, { 3, 1 }, { 3, 3 }, { 2, 1 }, { 2, 3 } });
    // AStar aStar = new AStar(7, 6, 6, 0, 0, 5,
    // new int[][] { { 4, 0 }, { 5, 1 }, { 0, 4 }, { 1, 5 }, { 2, 1 }, { 2, 2 }, {
    // 2, 3 }, { 3, 3 }, { 4, 3 } });
    // AStar aStar = new AStar(3, 3, 2, 0, 0, 2, new int[][] { { 0, 1 }, { 1, 1 }, {
    // 1, 2 } });
    AStar aStar = new AStar(3, 3, 2, 0, 0, 2, new int[][] { { 1, 1 } });

    // **** display the grid of cells ****
    aStar.displayGrid();

    // **** process the grid with the A* Search algorithm ****
    aStar.search();

    // **** display the closed cells ****
    aStar.displayClosedCells();

    // **** display scores ****
    aStar.displayScores();

    // **** display solution path (if any) ****
    aStar.displayPath();
  }
}