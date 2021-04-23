import javax.swing.*;
import java.awt.*;

/**
 * Program służący do rysowania i edytowania figur.
 * @version 1.0 2021-04-19
 * @author Marcin Wilk
 */

public class Main {
    public static void main(String[] args) {
        // Utworzenie, skonfigurowanie i wyświetlenie głównego okna programu
        EventQueue.invokeLater(() -> {
            var frame = new MainFrame();
            frame.setTitle("Edytor figur");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
