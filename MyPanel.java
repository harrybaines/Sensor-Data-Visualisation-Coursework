import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class MyPanel extends JPanel {
    JButton button = new JButton("Button");
    JTabbedPane tabPane = new JTabbedPane();

    public MyPanel(){
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        tabPane.add("Panel 1", panel1);
        tabPane.add("Panel 2", panel2);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new MyPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationByPlatform(true);
                frame.setSize(300, 300);
                frame.setVisible(true);
            }
        });
    }
}