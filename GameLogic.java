// GameLogic.java
import java.util.Random;

public class GameLogic {
    private final int size = 15;
    private final int[][] board = new int[size][size];

    public static final int PLAYER = 1;
    public static final int AI = 2;

    // --- 评估函数所需的分数常量 ---
    private static final int SCORE_WIN = 1000000;   // 连五
    private static final int SCORE_LIVE_FOUR = 100000;  // 活四
    private static final int SCORE_RUSH_FOUR = 10000;   // 冲四
    private static final int SCORE_LIVE_THREE = 5000;   // 活三
    private static final int SCORE_SLEEP_THREE = 1000;  // 眠三
    private static final int SCORE_LIVE_TWO = 500;      // 活二
    private static final int SCORE_SLEEP_TWO = 100;     // 眠二
    private static final int SCORE_LIVE_ONE = 50;       // 活一
    private static final int SCORE_SLEEP_ONE = 10;      // 眠一

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
     * 特别需要注意优化算法时，aiMove的函数的返回值要和之前的一模一样
     * @return 返回机器人落子的坐标点 Point, 如果棋盘已满则返回 null
     */
    public Point aiMove() {
        if (isBoardFull()) {
            return null;
        }

        // 创建一个评分板
        int[][] scoreBoard = new int[size][size];
        
        // 遍历棋盘所有空位，计算每个位置的得分
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    // 计算AI下在此处的进攻分
                    int offensiveScore = calculatePointScore(i, j, AI);
                    // 计算玩家下在此处的防守分
                    int defensiveScore = calculatePointScore(i, j, PLAYER);
                    // 总分 = 进攻分 + 防守分
                    scoreBoard[i][j] = offensiveScore + defensiveScore;
                }
            }
        }
        
        // 从评分板中找到分数最高的点
        int bestX = -1, bestY = -1;
        int maxScore = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0 && scoreBoard[i][j] > maxScore) {
                    maxScore = scoreBoard[i][j];
                    bestX = i;
                    bestY = j;
                }
            }
        }

        // 在分数最高的点落子
        board[bestX][bestY] = AI;
        return new Point(bestX, bestY, AI);
    }

    /**
     * 计算在指定点落子后，该点在所有四个方向上形成棋形的总分数
     * @param x, y 坐标
     * @param player 假设落子的玩家
     * @return 该点的总分数
     */
    private int calculatePointScore(int x, int y, int player) {
        int totalScore = 0;
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}}; // 横、竖、右下、右上

        for (int[] dir : directions) {
            totalScore += evaluateDirection(x, y, player, dir[0], dir[1]);
        }
        return totalScore;
    }

    /**
     * 评估在一个方向上形成的棋形并返回其分数
     */
    private int evaluateDirection(int x, int y, int player, int dx, int dy) {
        StringBuilder line = new StringBuilder();
        // 以(x,y)为中心，向前后各取4个子，形成长度为9的线
        for (int i = -4; i <= 4; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;

            if (isValid(nx, ny)) {
                if (i == 0) {
                    line.append(player); // 假设中心点已落子
                } else {
                    line.append(board[nx][ny]);
                }
            } else {
                line.append("3"); // 3代表墙壁或边界
            }
        }
        
        // 根据形成的字符串匹配棋形给分
        String s = line.toString();
        // 己方棋形
        if (s.contains(String.valueOf(player).repeat(5))) return SCORE_WIN;
        if (s.contains("0" + String.valueOf(player).repeat(4) + "0")) return SCORE_LIVE_FOUR;
        if (s.contains(String.valueOf(player).repeat(4)) && (s.contains("0" + String.valueOf(player).repeat(4)) || s.contains(String.valueOf(player).repeat(4) + "0"))) return SCORE_RUSH_FOUR;
        if (s.contains("0" + String.valueOf(player).repeat(3) + "0")) return SCORE_LIVE_THREE;
        if (s.contains("00" + String.valueOf(player).repeat(2) + "00")) return SCORE_LIVE_TWO; 
        if (
                s.contains("0" + String.valueOf(player).repeat(3)) || 
                s.contains(String.valueOf(player).repeat(3) + "0") ||
                s.contains("0" + player + "0" + player + player + "0") || 
                s.contains("0" + player + player + "0" + player + "0")
            ) return SCORE_SLEEP_THREE;
        if (
                s.contains("0" + String.valueOf(player).repeat(2)) ||
                s.contains(String.valueOf(player).repeat(2) + "0") ||
                s.contains("0" + player + "0" + player + "0")
            ) return SCORE_SLEEP_TWO;
        if (s.contains("0" + player + "0")) return SCORE_LIVE_ONE;
        if (s.contains("0" + player) || s.contains(player + "0")) return SCORE_SLEEP_ONE;

        
        return 0; // 默认不得分
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