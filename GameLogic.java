// GameLogic.java
import java.util.Random;

public class GameLogic {
    private final int size = 15;
    private final int[][] board = new int[size][size];

    public static final int PLAYER = 1;
    public static final int AI = 2;

    public GameLogic() {
        // 构造方法，初始化棋盘为空格
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    /**
     * 玩家下棋
     * @param x 棋盘x坐标
     * @param y 棋盘y坐标
     * @return 如果下棋成功返回 true, 否则返回 false
     */
    public boolean playerMove(int x, int y) {
        if (isValidMove(x, y)) {
            board[x][y] = PLAYER;
            return true;
        }
        return false;
    }

    /**
     * 机器人随机下棋
     * @return 返回机器人落子的坐标点 Point, 如果棋盘已满则返回 null
     */
    public Point aiMove() {
        // 检查棋盘是否已满
        if (isBoardFull()) {
            return null;
        }
        
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
        } while (board[x][y] != 0); // 随机选择一个空的格子
        
        board[x][y] = AI;
        return new Point(x, y, AI);
    }
    
    /**
     * 核心胜利判断逻辑，只判断传入的最后一个点
     * @param x 最后一个落子的x坐标
     * @param y 最后一个落子的y坐标
     * @param player 最后一个落子的玩家 (PLAYER or AI)
     * @return 如果该玩家胜利返回 true
     */
    public boolean checkWin(int x, int y, int player) {
        return checkDirection(x, y, player, 1, 0) ||  // 横向
               checkDirection(x, y, player, 0, 1) ||  // 纵向
               checkDirection(x, y, player, 1, 1) ||  // 右下对角线
               checkDirection(x, y, player, 1, -1);   // 右上对角线 (原代码是左下)
    }

    private boolean checkDirection(int x, int y, int player, int dx, int dy) {
        int count = 1; // 包括当前刚下的这颗子

        // 向正方向检查
        int nx = x + dx;
        int ny = y + dy;
        while (isValid(nx, ny) && board[nx][ny] == player) {
            count++;
            nx += dx;
            ny += dy;
        }

        // 向反方向检查
        nx = x - dx;
        ny = y - dy;
        while (isValid(nx, ny) && board[nx][ny] == player) {
            count++;
            nx -= dx;
            ny -= dy;
        }

        return count >= 5;
    }

    // 辅助方法：检查坐标是否在棋盘内
    private boolean isValid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }
    
    // 辅助方法：检查此位置是否可以落子
    public boolean isValidMove(int x, int y) {
        return isValid(x, y) && board[x][y] == 0;
    }

    // 辅助方法：检查棋盘是否已满（平局）
    public boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // 提供一个getter方法，让UI可以访问棋盘数据进行绘制
    public int[][] getBoard() {
        return board;
    }

    public int getSize() {
        return size;
    }
}