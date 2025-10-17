package view;

import javax.swing.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int heigth) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Illustrator z temu");
        setResizable(false);
        setVisible(true);

        panel = new Panel(width, heigth);
        add(panel);
        pack();

        panel.setFocusable(true);
        panel.grabFocus();
    }

    public Panel getPanel() {
        return panel;
    }
}
