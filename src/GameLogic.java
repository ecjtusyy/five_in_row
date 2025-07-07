// GameLogic.java

public class GameLogic {
    private final int size = 30;
    private final int[][] board = new int[size][size];

    // --- 棋子常量 (重命名以适应两种模式) ---
    public static final int PLAYER_BLACK = 1; // 黑棋 (玩家1)
    public static final int PLAYER_WHITE = 2; // 白棋 (玩家2 或 AI)
    
    // --- 游戏模式常量 ---
    public static final int MODE_PVE = 0; // 人机对战
    public static final int MODE_PVP = 1; // 双人对战

    private int currentPlayer; // 当前执子玩家

    // --- 评估函数所需的分数常量 (保持不变) ---
    private static final int SCORE_WIN = 1000000;
    private static final int SCORE_LIVE_FOUR = 100000;
    private static final int SCORE_RUSH_FOUR = 10000;
    private static final int SCORE_LIVE_THREE = 5000;
    private static final int SCORE_SLEEP_THREE = 1000;
    private static final int SCORE_LIVE_TWO = 500;
    private static final int SCORE_SLEEP_TWO = 100;
    private static final int SCORE_LIVE_ONE = 50;
    private static final int SCORE_SLEEP_ONE = 10;

    public GameLogic() {
        startGame(); // 构造时直接开始新游戏
    }
    
    /**
     * 开始或重置游戏
     */
    public void startGame() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0; // 清空棋盘
            }
        }
        currentPlayer = PLAYER_BLACK; // 默认黑棋先手
    }

    /**
     * 为当前玩家下棋
     * @param x 棋盘x坐标
     * @param y 棋盘y坐标
     * @return 如果下棋成功返回 true, 否则返回 false
     */
    public boolean placePiece(int x, int y) {
        if (isValidMove(x, y)) {
            board[x][y] = currentPlayer;
            return true;
        }
        return false;
    }

    /**
     * 切换玩家
     */
    public void switchPlayer() {
        if (currentPlayer == PLAYER_BLACK) {
            currentPlayer = PLAYER_WHITE;
        } else {
            currentPlayer = PLAYER_BLACK;
        }
    }
    
    /**
     * 获取当前执子玩家
     * @return 当前玩家常量
     */
    public int getCurrentPlayer(){
        return this.currentPlayer;
    }


    /**
     * AI下棋逻辑 (保持不变, 但落子时使用 PLAYER_WHITE)
     * @return 返回机器人落子的坐标点 Point, 如果棋盘已满则返回 null
     */
    public Point aiMove() {
        if (isBoardFull()) {
            return null;
        }

        int[][] scoreBoard = new int[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    // AI是白棋，所以进攻评估用PLAYER_WHITE
                    int offensiveScore = calculatePointScore(i, j, PLAYER_WHITE);
                    // 玩家是黑棋，所以防守评估用PLAYER_BLACK
                    int defensiveScore = calculatePointScore(i, j, PLAYER_BLACK);
                    scoreBoard[i][j] = offensiveScore + defensiveScore;
                }
            }
        }
        
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

        board[bestX][bestY] = PLAYER_WHITE; // AI落子为白棋
        switchPlayer(); // AI下完后，切换回玩家
        return new Point(bestX, bestY, PLAYER_WHITE);
    }

    // calculatePointScore 和 evaluateDirection 方法保持不变，它们是通用的评估函数
    private int calculatePointScore(int x, int y, int player) {
        int totalScore = 0;
        int[][] directions = {{1, 2}, {2, 1}, {1, -2}, {2, -1},{1,0},{0,1},{1,1},{1,-1}}; 

        for (int[] dir : directions) {
            totalScore += evaluateDirection(x, y, player, dir[0], dir[1]);
        }
        return totalScore;
    }

    private int evaluateDirection(int x, int y, int player, int dx, int dy) {
        StringBuilder line = new StringBuilder();
        for (int i = -4; i <= 4; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;

            if (isValid(nx, ny)) {
                if (i == 0) {
                    line.append(player); 
                } else {
                    line.append(board[nx][ny]);
                }
            } else {
                line.append("3");
            }
        }
        
        String s = line.toString();
        String pStr = String.valueOf(player);
        if (s.contains(pStr.repeat(5))) return SCORE_WIN;
        if (s.contains("0" + pStr.repeat(4) + "0")) return SCORE_LIVE_FOUR;
        if (s.contains(pStr.repeat(4)) && (s.contains("0" + pStr.repeat(4)) || s.contains(pStr.repeat(4) + "0"))) return SCORE_RUSH_FOUR;
        if (s.contains("0" + pStr.repeat(3) + "0")) return SCORE_LIVE_THREE;
        if (s.contains("00" + pStr.repeat(2) + "00")) return SCORE_LIVE_TWO; 
        if (s.contains("0" + pStr.repeat(3)) || s.contains(pStr.repeat(3) + "0") || s.contains("0" + pStr + "0" + pStr + pStr + "0") || s.contains("0" + pStr + pStr + "0" + pStr + "0")) return SCORE_SLEEP_THREE;
        if (s.contains("0" + pStr.repeat(2)) || s.contains(pStr.repeat(2) + "0") || s.contains("0" + pStr + "0" + pStr + "0")) return SCORE_SLEEP_TWO;
        if (s.contains("0" + pStr + "0")) return SCORE_LIVE_ONE;
        if (s.contains("0" + pStr) || s.contains(pStr + "0")) return SCORE_SLEEP_ONE;
        
        return 0;
    }

    // --- 其他辅助方法 (保持不变) ---
    public boolean checkWin(int x, int y, int player) {
        return  checkDirection(x, y, player, 1, 2) ||  
                checkDirection(x, y, player, 2, 1) ||  
                checkDirection(x, y, player, 1, -2)   ||  
                checkDirection(x, y, player, 2, -1)   ||
                checkDirection(x, y, player, 1, 0) ||
                checkDirection(x, y, player, 0, 1) ||
                checkDirection(x, y, player, 1, 1) ||     
                checkDirection(x, y, player, 1, -1) ;
    }

    private boolean checkDirection(int x, int y, int player, int dx, int dy) {
        int count = 1;
        int nx = x + dx;
        int ny = y + dy;
        while (isValid(nx, ny) && board[nx][ny] == player) {
            count++;
            nx += dx;
            ny += dy;
        }
        nx = x - dx;
        ny = y - dy;
        while (isValid(nx, ny) && board[nx][ny] == player) {
            count++;
            nx -= dx;
            ny -= dy;
        }
        return count >= 5;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }
    
    public boolean isValidMove(int x, int y) {
        return isValid(x, y) && board[x][y] == 0;
    }

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

    public int[][] getBoard() {
        return board;
    }

    public int getSize() {
        return size;
    }
}