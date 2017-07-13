import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainFrame extends JFrame {

  private JPanel mainPanel;
  private JLabel scoreLabel;
  private int score;
  private int bestScore;
  private JLabel bestScoreLabel;
  private int N = 4;
  private int[] values = new int[N * N];
  private Random random = new Random();
  private boolean needToAddNewValue = true;

  public MainFrame() {
    createMainPanel();
    createScorePanel();
    this.addKeyListener(new ArrowsKeyListener());
  }

  private void createScorePanel() {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new GridLayout(1, 3));
    topPanel.add(label("2048", 40, new Color(87, 81, 80)));
    scoreLabel = label("0", 20, Color.WHITE);
    JPanel scorePanel = scorePanel("SCORE", scoreLabel);
    bestScoreLabel = label("0", 20, Color.WHITE);
    JPanel bestScorePanel = scorePanel("BEST", bestScoreLabel);
    topPanel.add(scorePanel);
    topPanel.add(bestScorePanel);
    this.add(topPanel, BorderLayout.PAGE_START);
  }

  private static JPanel scorePanel(String text, JLabel scoreLabel) {
    JPanel scorePanel = new JPanel();
    scorePanel.add(label(text, 20, new Color(87, 81, 80)));
    scorePanel.setOpaque(true);
    scorePanel.setBackground(Color.LIGHT_GRAY);
    scorePanel.add(scoreLabel);
    return scorePanel;
  }

  private static JLabel label(String text, int size, Color color) {
    JLabel label = new JLabel();
    label.setFont(new Font("Verdana", Font.BOLD, size));
    label.setText(text);
    label.setForeground(color);
    return label;
  }

  private void createMainPanel() {
    this.mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(N, N));
    addNewCell();
    this.add(mainPanel, BorderLayout.CENTER);
  }

  private void left() {
    nextMove(0, 1);
  }

  private void right() {
    nextMove(N - 1, -1);
  }

  private void up() {
    nextMove(0, N);
  }

  private void down() {
    nextMove(N * (N - 1), -N);
  }

  private void nextMove(int start, int step) {
    moveTo(start, step);
    updateMainPanel();
    updateScorePanel();
  }

  private void addNewCell() {
    List<Integer> emptyCells = new ArrayList<Integer>();
    for (int i = 0; i < N * N; i++) {
      if (values[i] == 0) {
        emptyCells.add(i);
      }
    }
    if (emptyCells.isEmpty()) {
      if (!stillHasMove()) {
        newGame();
      }
    } else if (needToAddNewValue) {
      addNewCell(emptyCells);
      needToAddNewValue = false;
      updateMainPanel();
    }
  }

  private boolean stillHasMove() {
    for (int i = 0; i < N * N; i++) {
      if ((i + 1) % N != 0 && values[i] == values[i + 1]) {
        return true;
      }
      if (i + N < N * N && values[i] == values[i + N]) {
        return true;
      }
    }
    return false;
  }

  private void newGame() {
    values = new int[N * N];
    if (score > bestScore) {
      bestScore = score;
    }
    score = 0;
    needToAddNewValue = true;
    addNewCell();
    updateScorePanel();
  }

  private void addNewCell(List<Integer> emptyCells) {
    int cell = random.nextInt(emptyCells.size());
    int value = random.nextInt(10) == 0 ? 4 : 2;
    values[emptyCells.get(cell)] = value;
  }

  private void updateScorePanel() {
    scoreLabel.setText(String.valueOf(score));
    bestScoreLabel.setText(String.valueOf(bestScore));
  }

  private void updateMainPanel() {
    mainPanel.removeAll();
    for (int i = 0; i < N * N; i++) {
      JLabel label = new JLabel();
      label.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
          new Color(136, 130, 129)));
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setVerticalAlignment(SwingConstants.CENTER);
      label.setOpaque(true);
      label.setText(values[i] == 0 ? "" : String.valueOf(values[i]));
      label.setForeground(foregroundByValue(values[i]));
      label.setFont(new Font("Verdana", Font.BOLD, fontSize(values[i])));
      label.setBackground(backgroundColorByValue(values[i]));
      mainPanel.add(label);
    }
    mainPanel.validate();
    mainPanel.repaint();
  }

  private static int fontSize(int value) {
    if (value > 10000) {
      return 26;
    } else if (value > 1000) {
      return 32;
    }
    return 42;
  }

  private static Color foregroundByValue(int value) {
    if (value == 2) {
      return new Color(55, 33, 18);
    }
    return Color.WHITE;
  }

  private static Color backgroundColorByValue(int value) {
    switch (value) {
      case 0:
        return Color.LIGHT_GRAY;
      case 2:
        return Color.WHITE;
      case 4:
        return new Color(179, 193, 214);
      case 8:
        return new Color(214, 143, 70);
      case 16:
        return new Color(214, 95, 10);
      case 32:
        return new Color(214, 107, 154);
      case 64:
        return new Color(226, 68, 45);
      case 128:
        return new Color(252, 192, 73);
      case 256:
        return new Color(242, 200, 12);
      case 512:
        return new Color(242, 216, 108);
      case 1024:
        return new Color(242, 242, 149);
      case 2048:
        return new Color(242, 83, 40);
      case 4096:
        return new Color(242, 65, 118);
      case 8192:
        return new Color(242, 21, 87);
      case 16384:
        return new Color(233, 102, 242);
    }
    return new Color(173, 88, 242);
  }

  private void moveTo(int start, int step) {
    int addToMinMax = Math.abs(step) == N ? 1 : N;
    int max = Math.abs(step) * (N - 1) + 1 - addToMinMax;
    int min = -addToMinMax;
    for (int i = 0; i < N; i++) {
      int element = addToMinMax * i + start;
      max += addToMinMax;
      min += addToMinMax;
      while (isInRange(element, min, max)) {
        int nextNotNullElement = element + step;
        while (isInRange(nextNotNullElement + step, min, max)
            && values[nextNotNullElement] == 0) {
          nextNotNullElement += step;
        }
        if (isInRange(nextNotNullElement, min, max)) {
          if (values[element] == values[nextNotNullElement] && values[element] != 0) {
            mergeTwoCells(element, nextNotNullElement);
          } else if (values[element] == 0 && values[nextNotNullElement] != 0) {
            moveNonZeroElement(element, nextNotNullElement);
            element -= step;
          }
        }
        element += step;
      }
    }
  }

  private static boolean isInRange(int element, int min, int max) {
    return element >= min && element < max;
  }

  private void mergeTwoCells(int first, int second) {
    values[first] *= 2;
    score += values[first];
    values[second] = 0;
    needToAddNewValue = true;
  }

  private void moveNonZeroElement(int first, int second) {
    values[first] = values[second];
    values[second] = 0;
    needToAddNewValue = true;
  }

  private class ArrowsKeyListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        left();
      } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        right();
      } else if (e.getKeyCode() == KeyEvent.VK_UP) {
        up();
      } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        down();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      addNewCell();
    }
  }


  public static void main(String[] args) {
    MainFrame mainFrame = new MainFrame();
    mainFrame.setVisible(true);
    mainFrame.setSize(500, 500);
    mainFrame.setMinimumSize(new Dimension(500, 500));
  }
}
