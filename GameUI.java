// GameUI.java
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class GameUI extends JPanel {

    private final GameLogic gameLogic; // 引用游戏逻辑类
    private final int cellSize = 40;
    private final int size;
    
    private boolean gameEnded = false; // 游戏是否结束的标志

    private final Color playerColor = Color.BLACK;
    private final Color aiColor = Color.WHITE;

    public GameUI(GameLogic logic) {
        this.gameLogic = logic;
        this.size = gameLogic.getSize();

        // 设置面板的首选大小
        setPreferredSize(new Dimension(size * cellSize, size * cellSize));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameEnded) {
                    return; // 游戏结束，不再响应点击
                }

                int x = e.getX() / cellSize;
                int y = e.getY() / cellSize;

                // 尝试让玩家下棋
                if (gameLogic.playerMove(x, y)) {
                    repaint(); // 重绘棋盘

                    // 1. 判断玩家是否胜利
                    if (gameLogic.checkWin(x, y, GameLogic.PLAYER)) {
                        gameEnded = true;
                        JOptionPane.showMessageDialog(null, "恭喜你，玩家赢了！");
                        return;
                    }
                    
                    // 2. 判断是否平局
                    if(gameLogic.isBoardFull()){
                        gameEnded = true;
                        JOptionPane.showMessageDialog(null, "平局！");
                        return;
                    }

                    // 3. 轮到AI下棋
                    Point aiMovePoint = gameLogic.aiMove();
                    repaint(); // AI下完棋后重绘

                    // 4. 判断AI是否胜利
                    if (gameLogic.checkWin(aiMovePoint.getx(), aiMovePoint.gety(), GameLogic.AI)) {
                        gameEnded = true;
                        JOptionPane.showMessageDialog(null, "很遗憾，机器人赢了！");
                    }
                    
                    // 5. 判断是否平局
                    if(gameLogic.isBoardFull()){
                        gameEnded = true;
                        JOptionPane.showMessageDialog(null, "平局！");
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoardAndPieces(g);
    }

    private void drawBoardAndPieces(Graphics g) {
        int[][] board = gameLogic.getBoard(); // 从逻辑类获取最新棋盘

        // 绘制棋盘背景
        g.setColor(new Color(240, 217, 181)); // 柔和的木质颜色
        g.fillRect(0, 0, getWidth(), getHeight());

        // 绘制网格线
        g.setColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            g.drawLine(cellSize / 2, cellSize / 2 + i * cellSize, size * cellSize - cellSize / 2, cellSize / 2 + i * cellSize);
            g.drawLine(cellSize / 2 + i * cellSize, cellSize / 2, cellSize / 2 + i * cellSize, size * cellSize - cellSize / 2);
        }

        // 开启抗锯齿，让棋子更平滑
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制棋子
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == GameLogic.PLAYER) {
                    g2d.setColor(playerColor);
                    g2d.fillOval(i * cellSize + 5, j * cellSize + 5, cellSize - 10, cellSize - 10);
                } else if (board[i][j] == GameLogic.AI) {
                    g2d.setColor(aiColor);
                    g2d.fillOval(i * cellSize + 5, j * cellSize + 5, cellSize - 10, cellSize - 10);
                }
            }
        }
    }
}