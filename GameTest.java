// import java.util.ArrayList;
// import java.util.List;

// public class test {
//     public static void main(String[] args) {
//         List<Point> points = new ArrayList<>();
//         points.add(new Point(1, 1,1));
//         points.add(new Point(2,2,1));
//         points.add(new Point(2,3,1));
//         points.add(new Point(4,2,2));
//         System.out.println(points);
        
//     }   
// }


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameTest {
    public static void main(String[] args) {
        // 创建一个JFrame来运行游戏
        JFrame frame = new JFrame("五子棋测试");

        // 创建一个GameUI对象，并把它添加到JFrame中
        GameUI gameUI = new GameUI();
        frame.add(gameUI);

        // 设置窗口大小
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 添加鼠标点击事件监听
        gameUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 获取玩家点击的坐标
                int[] cell = gameUI.getClickedCell(e.getX(), e.getY());
                int x = cell[0];
                int y = cell[1];

                // 判断点击的格子是否为空，如果为空，则玩家下棋
                if (gameUI.board[x][y] == 0) {
                    gameUI.makeMove(x, y, GameUI.player);  // 玩家下棋

                    // 机器人随机下棋
                    gameUI.aiMove();
                }
            }
        });

        // 在控制台输出棋盘状态（辅助调试）
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printBoard(gameUI);
            }
        });
        timer.start();
    }

    // 打印棋盘状态，用于调试
    private static void printBoard(GameUI gameUI) {
        System.out.println("当前棋盘状态：");
        for (int i = 0; i < gameUI.size; i++) {
            for (int j = 0; j < gameUI.size; j++) {
                System.out.print(gameUI.board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
