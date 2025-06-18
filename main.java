import java.util.*;
public class main
{
    //先是初始化棋盘大小
    private static final int size = 15;
    private int[][] board = new int[size][size];

    //定义棋盘中0是空格，1是玩家，2是机器人
    private static final int player=1;
    private static final int ai=2;

    //构造方法，初始化棋盘
    public main()
    {
        for (int i =0;i<size;i++)
        {
            for (int j=0;j<size;j++)
            {
                board[i][j]=0;
            }
        }
    }

    //先写一个简单的方法之后在优化
        // 玩家下棋
    public boolean playerMove(int x, int y) {
        if (board[x][y] == 0) {
            board[x][y] = player;
            return true;
        }
        return false;
    }

    // 机器人随机下棋
    public void aiMove() {
        Random rand = new Random();
        int x, y;
        // 随机选择一个空的格子
        do {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
        } while (board[x][y] != 0); // 确保选择的格子为空
        board[x][y] = ai;
    }

    //先不管时间复杂度，等到全都写出来再考虑优化
    public void graphical_search()
    {
        List<Point> res_points = new ArrayList<>();
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                if (board[i][j]!=0)
                    {
                        res_points.add(new Point(i,j,board[i][j]));
                    }
            }
        }
        // 对每个点进行判断
        for (Point p : res_points) {
            if (check_win(p)) {
                if (p.getpos() == player) {
                    System.out.println("玩家赢了！");
                } else {
                    System.out.println("机器人赢了！");
                }
                return;
            }
        }

        System.out.println("当前没有赢家。");
    }

    //还是先不管时间复杂度，使用递归来写
    public boolean check_win(Point p)
    {
        int x = p.getx();
        int y = p.gety();
        int pos = p.getpos();

        return  checkDirection(x, y, pos, 1, 0) ||  // 横向
                checkDirection(x, y, pos, 0, 1) ||  // 纵向
                checkDirection(x, y, pos, 1, 1) ||  // 右下对角线
                checkDirection(x, y, pos, 1, -1);   // 左下对角线
    }
    public boolean checkDirection(int x,int y,int pos,int dx,int dy)
    {
        int count = 1;//当前的点

        int nx = x+dx;
        int ny = y+dy;
        while (nx >= 0 && nx < size && ny >= 0 && ny < size && board[nx][ny] == pos)
        {
            count++;
            if(count==5) return true;
            nx+=dx;
            ny+=dy;
        }

        nx = x-dx;
        ny = y-dy;
        while (nx >= 0 && nx < size && ny >= 0 && ny < size && board[nx][ny] == pos) {
            count++;
            if (count == 5) return true;
            nx -= dx;
            ny -= dy;
        }
        return false;
    }
}