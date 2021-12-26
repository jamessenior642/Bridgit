import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// class representing the world
class OurWorld extends World {
  Utils u = new Utils();
  ArrayList<Cell> grid;
  int n;
  boolean p1turn;

  // constructor for the world that takes in a grid size
  // grid size must be 3 or greater
  OurWorld(int n) {
    this.n = n;
    this.p1turn = true;
    if ((n >= 3) && (n % 2 != 0)) {
      this.grid = this.u.makeGrid(this.n);
      this.u.linkCells(this.grid, this.n);
    }
    else {
      throw new IllegalArgumentException("Invalid Argument");
    }
  }

  // constructor to test linkcells
  OurWorld(int n, boolean b) {
    this.n = n;
    this.p1turn = true;
    if ((n >= 3) && (n % 2 != 0)) {
      this.grid = this.u.makeGrid(this.n);
    }
    else {
      throw new IllegalArgumentException("Invalid Argument");
    }
  }

  // method to make the initial scene of the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(500, 500);
    for (Cell c : grid) {
      scene.placeImageXY(c.draw(), c.loc.x, c.loc.y);
    }
    return scene;
  }

  // EFFECT: resets the world when the r key is pressed
  public void onKeyEvent(String key) {
    Utils u = new Utils();
    if (key.equals("r")) {
      u.resetWorld(this);
    }
  }

  // EFFECT: Changes the colors of the white keys
  // that are pressed depending on the player's turn
  public void onMousePressed(Posn pos) {
    for (Cell c : this.grid) {
      if (c.color.equals(Color.WHITE) && c.isClicked(pos)) {
        if (this.p1turn) {
          c.color = Color.PINK;
        }
        else {
          c.color = Color.MAGENTA;
        }
        this.p1turn = !this.p1turn;
      }
    }
  }

  // EFFECT: Ends the game when a path exists from left to right
  // for player 1 or top to bottom for player 2
  public void onTick() {
    for (int i = this.n; i < this.n * this.n; i += this.n) {
      if (this.grid.get(i).color.equals(Color.PINK)
          && this.grid.get(i).hasNeighborsp1(new ArrayList<Cell>())) {
        this.endOfWorld("Player 1 Wins!");
      }
    }
    for (int i = 1; i < this.n; i++) {
      if (this.grid.get(i).color.equals(Color.MAGENTA)
          && this.grid.get(i).hasNeighborsp2(new ArrayList<Cell>())) {
        this.endOfWorld("Player 2 Wins!");
      }
    }
  }

  // returns the final scene of the game
  public WorldScene lastScene(String msg) {
    WorldScene scene = new WorldScene(500, 500);
    scene.placeImageXY(new TextImage(msg, Color.BLACK), 250, 250);
    return scene;
  }

}

//class representing a single cell in the grid
class Cell {
  Color color;
  Posn loc;
  Cell up;
  Cell down;
  Cell left;
  Cell right;

  // constructor setting linked cells to null
  Cell(Color color, Posn loc) {
    this.color = color;
    this.loc = loc;
    this.up = null;
    this.down = null;
    this.left = null;
    this.right = null;
  }

  // constructor that takes cells
  Cell(Color color, Posn loc, Cell up, Cell down, Cell left, Cell right) {
    this.color = color;
    this.loc = loc;
    this.up = up;
    this.down = down;
    this.right = right;
    this.left = left;
  }

  // method to draw a single cell
  WorldImage draw() {
    return new RectangleImage(40, 40, "solid", this.color);
  }

  // checks if the given pos is the same as the cell's loc
  public boolean isClicked(Posn pos) {
    return (pos.x > this.loc.x - 20) && (pos.x < this.loc.x + 20) && (pos.y > this.loc.y - 20)
        && (pos.y < this.loc.y + 20);
  }

  // function that finds if there is a path for player 1
  public boolean hasNeighborsp1(ArrayList<Cell> acc) {
    if (this.up != null && this.up.color.equals(this.color) && acc.indexOf(this.up) == -1) {
      acc.add(this.up);
      this.up.hasNeighborsp1(acc);
    }
    if (this.down != null && this.down.color.equals(this.color) && acc.indexOf(this.down) == -1) {
      acc.add(this.down);
      this.down.hasNeighborsp1(acc);
    }
    if (this.right != null && this.right.color.equals(this.color)
        && acc.indexOf(this.right) == -1) {
      acc.add(this.right);
      this.right.hasNeighborsp1(acc);
    }
    // acc.addAll(neighbors);
    for (Cell c : acc) {
      if (c.right == null) {
        return true;
      }
    }
    return false;
  }

  // function that finds if there is a path for player 2
  public boolean hasNeighborsp2(ArrayList<Cell> acc) {
    if (this.left != null && this.left.color.equals(this.color) && acc.indexOf(this.left) == -1) {
      acc.add(this.left);
      this.left.hasNeighborsp2(acc);
    }
    if (this.down != null && this.down.color.equals(this.color) && acc.indexOf(this.down) == -1) {
      acc.add(this.down);
      this.down.hasNeighborsp2(acc);
    }
    if (this.right != null && this.right.color.equals(this.color)
        && acc.indexOf(this.right) == -1) {
      acc.add(this.right);
      this.right.hasNeighborsp2(acc);
    }
    // acc.addAll(neighbors);
    for (Cell c : acc) {
      // c.hasNeighborsp1(acc);
      if (c.down == null) {
        return true;
      }
    }
    return false;
  }
}

// utilities class containing methods
class Utils {
  // method to create the initial grid of cells
  ArrayList<Cell> makeGrid(int n) {
    Color c = Color.PINK;
    ArrayList<Cell> grid = new ArrayList<Cell>();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i % 2 == 0) {
          if (j % 2 == 0) {
            c = Color.WHITE;
          }
          else if (j % 2 == 1) {
            c = Color.MAGENTA;
          }
        }
        else if (i % 2 == 1) {
          if (j % 2 == 0) {
            c = Color.PINK;
          }
          else if (j % 2 == 1) {
            c = Color.WHITE;
          }
        }
        Cell curCell = new Cell(c, new Posn(j * 40 + 47, i * 40 + 47));
        grid.add(curCell);
      }
    }
    return grid;
  }

  // method that links all of the cells in the grid together
  public void linkCells(ArrayList<Cell> grid, int n) {
    for (int r = 0; r < n; r++) {
      for (int c = 0; c < n; c++) {
        if (r != 0) {
          grid.get(c + (r * n)).up = grid.get(c + (r * n) - n);
        }
        if (r != n - 1) {
          grid.get(c + (r * n)).down = grid.get(c + (r * n) + n);
        }
        if (c != 0) {
          grid.get(c + (r * n)).left = grid.get(c + (r * n) - 1);
        }
        if (c != n - 1) {
          grid.get(c + (r * n)).right = grid.get(c + (r * n) + 1);
        }
      }
    }
  }

  // method used for testing reset
  ArrayList<Cell> fillInPink(int n) {
    ArrayList<Cell> grid = new ArrayList<Cell>();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        Cell curCell = new Cell(Color.PINK, new Posn(j * 40 + 47, i * 40 + 47));
        grid.add(curCell);
      }
    }
    return grid;
  }

  // helper to reset world
  public void resetWorld(OurWorld w) {
    w.grid = w.u.makeGrid(w.n);
    w.p1turn = true;
  }

}

// examples and tests for Bridgit
class ExamplesBridgit {
  Cell c1 = new Cell(Color.PINK, new Posn(40, 50));
  Cell c2 = new Cell(Color.MAGENTA, new Posn(100, 70));
  Cell c3 = new Cell(Color.WHITE, new Posn(60, 150));
  OurWorld w1 = new OurWorld(11);
  Utils util = new Utils();

  // test to use bigbang
  void testBigBang(Tester t) {
    OurWorld world = this.w1;
    int worldWidth = 500;
    int worldHeight = 500;
    double tickRate = .005;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // tests for the constructor of the world
  void testConstructor(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("Invalid Argument"), "OurWorld", 10);
    t.checkConstructorException(new IllegalArgumentException("Invalid Argument"), "OurWorld", 2);
  }

  // tests for draw
  void testDraw(Tester t) {
    t.checkExpect(c1.draw(), new RectangleImage(40, 40, "solid", Color.PINK));
    t.checkExpect(c2.draw(), new RectangleImage(40, 40, "solid", Color.MAGENTA));
    t.checkExpect(c3.draw(), new RectangleImage(40, 40, "solid", Color.WHITE));
  }

  // tests for makeGrid
  void testMakeGrid(Tester t) {
    t.checkExpect(this.util.makeGrid(1),
        new ArrayList<Cell>(Arrays.asList(new Cell(Color.WHITE, new Posn(47, 47)))));
    t.checkExpect(this.util.makeGrid(2),
        new ArrayList<Cell>(Arrays.asList(new Cell(Color.WHITE, new Posn(47, 47)),
            new Cell(Color.MAGENTA, new Posn(87, 47)), new Cell(Color.PINK, new Posn(47, 87)),
            new Cell(Color.WHITE, new Posn(87, 87)))));
    t.checkExpect(this.util.makeGrid(3),
        new ArrayList<Cell>(Arrays.asList(new Cell(Color.WHITE, new Posn(47, 47)),
            new Cell(Color.MAGENTA, new Posn(87, 47)), new Cell(Color.WHITE, new Posn(127, 47)),
            new Cell(Color.PINK, new Posn(47, 87)), new Cell(Color.WHITE, new Posn(87, 87)),
            new Cell(Color.PINK, new Posn(127, 87)), new Cell(Color.WHITE, new Posn(47, 127)),
            new Cell(Color.MAGENTA, new Posn(87, 127)),
            new Cell(Color.WHITE, new Posn(127, 127)))));
  }

  // tests for linkCells
  void testLinkCells(Tester t) {
    OurWorld w2 = new OurWorld(3, true);
    t.checkExpect(w2.grid.get(4).left, null);
    t.checkExpect(w2.grid.get(4).right, null);
    t.checkExpect(w2.grid.get(4).up, null);
    t.checkExpect(w2.grid.get(4).down, null);
    util.linkCells(w2.grid, w2.n);
    t.checkExpect(w2.grid.get(0).left, null);
    t.checkExpect(w2.grid.get(0).right, w2.grid.get(1));
    t.checkExpect(w2.grid.get(0).up, null);
    t.checkExpect(w2.grid.get(0).down, w2.grid.get(3));
    t.checkExpect(w2.grid.get(4).left, w2.grid.get(3));
    t.checkExpect(w2.grid.get(4).right, w2.grid.get(5));
    t.checkExpect(w2.grid.get(4).up, w2.grid.get(1));
    t.checkExpect(w2.grid.get(4).down, w2.grid.get(7));
    t.checkExpect(w2.grid.get(8).left, w2.grid.get(7));
    t.checkExpect(w2.grid.get(8).right, null);
    t.checkExpect(w2.grid.get(8).up, w2.grid.get(5));
    t.checkExpect(w2.grid.get(8).down, null);
  }

  // tests for makeScene
  void testMakeScene(Tester t) {
    OurWorld testWorld1 = new OurWorld(3);
    WorldScene emptyScene1 = new WorldScene(500, 500);
    Cell testCell1 = new Cell(Color.WHITE, new Posn(0, 0));
    Cell testCell2 = new Cell(Color.MAGENTA, new Posn(0, 0));
    Cell testCell3 = new Cell(Color.WHITE, new Posn(0, 0));
    Cell testCell4 = new Cell(Color.PINK, new Posn(0, 0));
    Cell testCell5 = new Cell(Color.WHITE, new Posn(0, 0));
    Cell testCell6 = new Cell(Color.PINK, new Posn(0, 0));
    Cell testCell7 = new Cell(Color.WHITE, new Posn(0, 0));
    Cell testCell8 = new Cell(Color.MAGENTA, new Posn(0, 0));
    Cell testCell9 = new Cell(Color.WHITE, new Posn(0, 0));
    emptyScene1.placeImageXY(testCell1.draw(), 47, 47);
    emptyScene1.placeImageXY(testCell2.draw(), 87, 47);
    emptyScene1.placeImageXY(testCell3.draw(), 127, 47);
    emptyScene1.placeImageXY(testCell4.draw(), 47, 87);
    emptyScene1.placeImageXY(testCell5.draw(), 87, 87);
    emptyScene1.placeImageXY(testCell6.draw(), 127, 87);
    emptyScene1.placeImageXY(testCell7.draw(), 47, 127);
    emptyScene1.placeImageXY(testCell8.draw(), 87, 127);
    emptyScene1.placeImageXY(testCell9.draw(), 127, 127);
    t.checkExpect(testWorld1.makeScene(), emptyScene1);
  }

  // tests for fillInPink
  void testFillInPink(Tester t) {
    t.checkExpect(util.fillInPink(1),
        new ArrayList<Cell>(Arrays.asList(new Cell(Color.PINK, new Posn(47, 47)))));
    t.checkExpect(util.fillInPink(3),
        new ArrayList<Cell>(Arrays.asList(new Cell(Color.PINK, new Posn(47, 47)),
            new Cell(Color.PINK, new Posn(87, 47)), new Cell(Color.PINK, new Posn(127, 47)),
            new Cell(Color.PINK, new Posn(47, 87)), new Cell(Color.PINK, new Posn(87, 87)),
            new Cell(Color.PINK, new Posn(127, 87)), new Cell(Color.PINK, new Posn(47, 127)),
            new Cell(Color.PINK, new Posn(87, 127)), new Cell(Color.PINK, new Posn(127, 127)))));

  }

  // tests for Reset method
  void testReset(Tester t) {
    OurWorld w1 = new OurWorld(3, true);
    w1.grid = util.fillInPink(3);
    OurWorld w2 = new OurWorld(5, true);
    w2.grid = util.fillInPink(5);
    t.checkExpect(w1.grid, util.fillInPink(3));
    util.resetWorld(w1);
    t.checkExpect(w1.grid, util.makeGrid(3));
    t.checkExpect(w2.grid, util.fillInPink(5));
    util.resetWorld(w2);
    t.checkExpect(w2.grid, util.makeGrid(5));
  }

  // tests for testIsClicked
  void testIsClicked(Tester t) {
    t.checkExpect(c1.isClicked(new Posn(21, 31)), true);
    t.checkExpect(c1.isClicked(new Posn(59, 69)), true);
    t.checkExpect(c1.isClicked(new Posn(10, 50)), false);
    t.checkExpect(c1.isClicked(new Posn(40, 100)), false);
  }

  // tests for onKeyEvent
  void testOnKeyEvent(Tester t) {
    OurWorld w1OnKey = new OurWorld(11, false);
    w1OnKey.grid = this.util.fillInPink(11);
    t.checkExpect(w1OnKey.grid, this.util.fillInPink(11));
    w1OnKey.onKeyEvent("a");
    t.checkExpect(w1OnKey.grid, this.util.fillInPink(11));
    w1OnKey.onKeyEvent("r");
    t.checkExpect(w1OnKey, new OurWorld(11, true));
  }

  // tests for OnTick
  void testOnTick(Tester t) {
    WorldScene w1scene = new WorldScene(500, 500);
    for (Cell c : w1.grid) {
      w1scene.placeImageXY(c.draw(), c.loc.x, c.loc.y);
    }

    OurWorld w2 = new OurWorld(3);
    w2.grid.get(4).color = Color.PINK;

    t.checkExpect(w1.lastWorld.worldEnds, false);
    w1.onTick();
    t.checkExpect(w1.lastWorld.worldEnds, false);

    t.checkExpect(w2.lastWorld.worldEnds, false);
    w2.onTick();
    t.checkExpect(w2.lastWorld.worldEnds, true);

    OurWorld w3 = new OurWorld(3);
    w3.grid.get(4).color = Color.MAGENTA;

    t.checkExpect(w3.lastWorld.worldEnds, false);
    w3.onTick();
    t.checkExpect(w3.lastWorld.worldEnds, true);
  }

  // tests for LastScene
  void testLastScene(Tester t) {
    WorldScene scene = new WorldScene(500, 500);
    scene.placeImageXY(new TextImage("Player 1 Wins!", Color.BLACK), 250, 250);
    t.checkExpect(this.w1.lastScene("Player 1 Wins!"), scene);
    WorldScene scene2 = new WorldScene(500, 500);
    scene2.placeImageXY(new TextImage("Player 2 Wins!", Color.BLACK), 250, 250);
    t.checkExpect(this.w1.lastScene("Player 2 Wins!"), scene2);
  }

  // tests for hasNeighborsp1
  void testHasNeighborsP1(Tester t) {
    Cell cell1 = new Cell(Color.PINK, new Posn(0, 0));
    Cell cell2 = new Cell(Color.PINK, new Posn(0, 0));
    Cell cell3 = new Cell(Color.MAGENTA, new Posn(0, 0));
    cell1.right = cell2;
    t.checkExpect(cell1.hasNeighborsp1(new ArrayList<Cell>()), true);
    cell2.right = cell3;
    t.checkExpect(cell1.hasNeighborsp1(new ArrayList<Cell>()), false);
  }

  // tests for hasNeighborsp2
  void testHasNeighborsP2(Tester t) {
    Cell cell1 = new Cell(Color.PINK, new Posn(0, 0));
    Cell cell2 = new Cell(Color.PINK, new Posn(0, 0));
    Cell cell3 = new Cell(Color.MAGENTA, new Posn(0, 0));
    cell1.down = cell2;
    t.checkExpect(cell1.hasNeighborsp2(new ArrayList<Cell>()), true);
    cell2.down = cell3;
    t.checkExpect(cell1.hasNeighborsp2(new ArrayList<Cell>()), false);
  }

  // tests for onMousePressed
  void testOnMousePressed(Tester t) {
    OurWorld w1 = new OurWorld(3);
    t.checkExpect(w1.grid.get(0).color, Color.WHITE);
    w1.onMousePressed(new Posn(57, 57));
    t.checkExpect(w1.grid.get(0).color, Color.PINK);

    t.checkExpect(w1.grid.get(2).color, Color.WHITE);
    w1.onMousePressed(new Posn(137, 57));
    t.checkExpect(w1.grid.get(2).color, Color.MAGENTA);

    t.checkExpect(w1.grid.get(1).color, Color.MAGENTA);
    w1.onMousePressed(new Posn(97, 57));
    t.checkExpect(w1.grid.get(1).color, Color.MAGENTA);
  }
}