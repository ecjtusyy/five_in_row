// Run.java
import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        // 使用 Swing 的事件调度线程来确保线程安全
        SwingUtilities.invokeLater(() -> {
            // 1. 创建游戏逻辑实例
            GameLogic logic = new GameLogic();

            // 2. 创建游戏UI实例, 并将逻辑实例注入进去
            GameUI gameUI = new GameUI(logic);

            // 3. 创建窗口
            JFrame frame = new JFrame("五子棋对战");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gameUI); // 将UI面板添加到窗口中
            frame.pack(); // 根据UI面板的首选大小自动调整窗口大小
            frame.setLocationRelativeTo(null); // 窗口居中显示
            frame.setResizable(false); // 禁止调整窗口大小
            frame.setVisible(true);
        });
    }
}