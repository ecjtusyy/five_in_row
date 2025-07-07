// GameUI.java
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class GameUI extends JPanel {

    private final GameLogic gameLogic;
    private final int gameMode; // 存储当前游戏模式
    private final int cellSize = 40;
    private final int size;
    
    private boolean gameEnded = false;

    private final Color playerColor = Color.BLACK;
    private final Color aiColor = Color.WHITE;

    public GameUI(GameLogic logic, int mode) {
        this.gameLogic = logic;
        this.gameMode = mode; // 接收并设置游戏模式
        this.size = gameLogic.getSize();

        setPreferredSize(new Dimension(size * cellSize, size * cellSize));
        updateTitle(); // 初始化窗口标题

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameEnded) return; // 游戏结束，不再响应

                int x = e.getX() / cellSize;
                int y = e.getY() / cellSize;

                int currentPlayer = gameLogic.getCurrentPlayer(); // 记录下棋前的玩家

                if (gameLogic.placePiece(x, y)) {
                    repaint();

                    // 1. 判断当前落子方是否胜利
                    if (gameLogic.checkWin(x, y, currentPlayer)) {
                        String winner = (currentPlayer == GameLogic.PLAYER_BLACK) ? "黑棋" : "白棋";
                        showWinnerAndRestart("恭喜, " + winner + " 赢了！");
                        return;
                    }
                    
                    // 2. 判断是否平局
                    if (gameLogic.isBoardFull()) {
                        showWinnerAndRestart("平局！");
                        return;
                    }

                    // 切换玩家
                    gameLogic.switchPlayer();

                    // --- 根据游戏模式决定下一步 ---
                    if (gameMode == GameLogic.MODE_PVE) {
                        // 3. 人机模式：轮到AI下棋
                        Point aiMovePoint = gameLogic.aiMove(); // aiMove内部已包含switchPlayer
                        repaint();
                        updateTitle(); // AI下完更新标题

                        // 4. 判断AI是否胜利
                        if (gameLogic.checkWin(aiMovePoint.getx(), aiMovePoint.gety(), GameLogic.PLAYER_WHITE)) {
                            showWinnerAndRestart("很遗憾，机器人赢了！");
                            return;
                        }
                        
                        // 5. 判断是否平局
                        if (gameLogic.isBoardFull()) {
                            showWinnerAndRestart("平局！");
                        }
                    }
                    // 如果是双人模式，只需切换玩家，然后等待下一次点击
                    
                    updateTitle(); // 更新标题显示轮到谁
                }
            }
        });
    }

    /**
     * 更新窗口标题以显示当前回合
     */
    private void updateTitle() {
        if (!gameEnded) {
            String modeStr = (gameMode == GameLogic.MODE_PVE) ? "人机对战" : "双人对战";
            String turnStr = (gameLogic.getCurrentPlayer() == GameLogic.PLAYER_BLACK) ? "黑棋回合" : "白棋回合";
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.setTitle("五子棋 - " + modeStr + " | " + turnStr);
            }
        }
    }
    
    /**
     * 显示结果并询问是否重新开始
     * @param message 结果信息
     */
    private void showWinnerAndRestart(String message){
        gameEnded = true;
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setTitle("五子棋 - 游戏结束");

        int choice = JOptionPane.showOptionDialog(this, 
            message + "\n是否要开始新的一局?", 
            "游戏结束", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, new String[]{"再来一局", "退出"}, "再来一局");

        if(choice == JOptionPane.YES_OPTION){
            gameLogic.startGame();
            gameEnded = false;
            updateTitle();
            repaint();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoardAndPieces(g);
    }

    private void drawBoardAndPieces(Graphics g) {
        int[][] board = gameLogic.getBoard();

        g.setColor(new Color(240, 217, 181));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            g.drawLine(cellSize / 2, cellSize / 2 + i * cellSize, size * cellSize - cellSize / 2, cellSize / 2 + i * cellSize);
            g.drawLine(cellSize / 2 + i * cellSize, cellSize / 2, cellSize / 2 + i * cellSize, size * cellSize - cellSize / 2);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // 使用新的棋子常量进行绘制
                if (board[i][j] == GameLogic.PLAYER_BLACK) {
                    g2d.setColor(playerColor);
                    g2d.fillOval(i * cellSize + 5, j * cellSize + 5, cellSize - 10, cellSize - 10);
                } else if (board[i][j] == GameLogic.PLAYER_WHITE) {
                    g2d.setColor(aiColor);
                    g2d.fillOval(i * cellSize + 5, j * cellSize + 5, cellSize - 10, cellSize - 10);
                }
            }
        }
    }
}