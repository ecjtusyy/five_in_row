// Run.java
import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- 模式选择 ---
            Object[] options = {"人机对战", "双人对战"};
            int choice = JOptionPane.showOptionDialog(null, "请选择游戏模式",
                    "开始游戏",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choice == -1) {
                System.exit(0); // 如果用户关闭对话框，则退出
            }
            
            int gameMode = (choice == 0) ? GameLogic.MODE_PVE : GameLogic.MODE_PVP;

            // 1. 创建游戏逻辑实例
            GameLogic logic = new GameLogic();

            // 2. 创建游戏UI实例, 并将逻辑实例和游戏模式注入
            GameUI gameUI = new GameUI(logic, gameMode);

            // 3. 创建窗口
            JFrame frame = new JFrame("五子棋"); // 标题会由GameUI动态设置
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gameUI);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}