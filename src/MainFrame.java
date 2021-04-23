import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Obiekt MainFrame zawiera główne okno programu. Zajmuje się stworzeniem interfejsu,
 * zapisaniem i wczytaniem wszystkich danych, oraz zawiera w sobie klasę tworzącą
 * główny komponent programu który obsługuje wszystkie funkcjonalności związane z tworzeniem
 * oraz edytowaniem figur. Posiada trzy pola, figures zawierające wszystkie figury znajdujące się
 * na ekranie, currentFigure zawierające nazwę aktualnie tworzonej figury oraz flagę isShiftPressed,
 * która przechowuje informację czy jest wciśnięty klawisz shift.
 */

public class MainFrame extends JFrame {
    private ArrayList<Figure> figures;
    private Figures currentFigure;

    private boolean isShiftPressed;

    /**
     * Tworzy główne okno programu, inicjalizuje zmienne, ładuje ustawienia z pliku,
     * tworzy menu, dodaje główny komponent programu i załącza obsługę klawiatury.
     * Ustawia również funkcję, która zapisze stan programu po zamknięciu.
     */

    public MainFrame() {
        currentFigure = null;
        isShiftPressed = false;
        figures = new ArrayList<>();

        loadSettings();
        createMenuBar();
        addComponents();
        attachKeyboardHandler();
        saveSettings();
    }

    /**
     * Tworzy pasek menu i wywołuje funkcje do utworzenia elementów na pasku.
     */

    private void createMenuBar() {
        var menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        createFiguresMenu(menuBar);
        createOptionsMenu(menuBar);
        createInfoMenu(menuBar);
    }

    /**
     * Tworzy element paska menu obsługujący figury.
     * @param menuBar pasek menu, w którym ma się utworzyć element
     */

    private void createFiguresMenu(JMenuBar menuBar) {
        // Utworzenie elementu menu i dodanie do niego figur
        var figuresMenu = new JMenu("Figury");
        menuBar.add(figuresMenu);

        var circle = new JMenuItem("Koło");
        figuresMenu.add(circle);

        var rectangle = new JMenuItem("Prostokąt");
        figuresMenu.add(rectangle);

        var triangle = new JMenuItem("Trójkąt");
        figuresMenu.add(triangle);

        // Dodanie reakcji na wybranie do koażdej figury z menu
        circle.addActionListener(e -> currentFigure = Figures.CIRCLE);
        rectangle.addActionListener(e -> currentFigure = Figures.RECTANGLE);
        triangle.addActionListener(e -> currentFigure = Figures.TRIANGLE);
    }

    /**
     * Tworzy element paska menu obsługujący opcje.
     * @param menuBar pasek menu, w którym ma się utworzyć element
     */

    private void createOptionsMenu(JMenuBar menuBar) {
        // Utworzenie elementu menu i dodanie do niego opcji wczytania i zapisania
        var options = new JMenu("Opcje");
        menuBar.add(options);

        var save = new JMenuItem("Zapisz");
        options.add(save);

        var load = new JMenuItem("Wczytaj");
        options.add(load);

        var chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        // Dodanie akcji do wciśnięcia przycisku zapisz, która wyświetla okno dialogowe,
        // a następnie zapisze wszystkie figury do wybranego pliku
        save.addActionListener(e -> {
            int result = chooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getPath();

                try (FileOutputStream f = new FileOutputStream(path);
                     ObjectOutputStream o = new ObjectOutputStream(f)) {
                    for (Figure g : figures) {
                        o.writeObject(g);
                    }
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        });

        // Dodanie akcji do wciśnięcia przycisku wczytaj, która wyświetla okno dialogowe,
        // a następnie wczyta wszystkie figury z wybranego pliku
        load.addActionListener(e -> {
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getPath();

                try (FileInputStream f = new FileInputStream(path);
                     ObjectInputStream o = new ObjectInputStream(f)) {
                    figures = new ArrayList<>();

                    while (true) {
                        try {
                            Figure g = (Figure) o.readObject();
                            figures.add(g);
                        } catch (EOFException exception) {
                            break;
                        }
                    }

                    repaint();
                } catch (IOException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * Tworzy element paska menu obsługujący informacje i instrukcję.
     * @param menuBar pasek menu, w którym ma się utworzyć element
     */

    private void createInfoMenu(JMenuBar menuBar) {
        // Utworzenie elementu menu i dodanie do niego opcji pokazania informacji i instrukcji
        var infoMenu = new JMenu("Info");
        menuBar.add(infoMenu);

        var info = new JMenuItem("Info");
        infoMenu.add(info);

        var instruction = new JMenuItem("Instrukcja");
        infoMenu.add(instruction);

        // Dodanie akcji do wciśnięcia przycisku info wyświetlającej informacje w oknie dialogowym
        info.addActionListener(e -> JOptionPane.showMessageDialog(
                    this,
                    "Edytor figur\nAutor: Marcin Wilk\nProgram służy do rysowania i edytowania figur geometrycznych.",
                    "Informacje", JOptionPane.PLAIN_MESSAGE)
        );

        // Dodanie akcji do wciśnięcia przycisku info wyświetlającej instrukcje w oknie dialogowym
        instruction.addActionListener(e -> JOptionPane.showMessageDialog(
                    this,
                    "Żeby utworzyć figurę wybierz ją z menu głównego.",
                    "Instrukcja użytkowania", JOptionPane.PLAIN_MESSAGE)
        );
    }

    /**
     * Ładuje ustawienia z pliku, a następnie wprowadza je do programu
     */

    private void loadSettings() {
        var node = Preferences.userRoot().node("/Wilk/Marcin/Figury");

        var dimensions = Toolkit.getDefaultToolkit().getScreenSize();

        int windowLeft = node.getInt("left", 0);
        int windowTop = node.getInt("top", 0);
        int windowWidth = node.getInt("width", (int) (dimensions.getWidth() / 2));
        int windowHeight = node.getInt("height", (int) (dimensions.getHeight() / 2));

        setBounds(windowLeft, windowTop, windowWidth, windowHeight);
    }

    /**
     * Dodaje metodę nasłuchującą zamknięcia programu, która zapisze ustawienia do pliku
     */

    private void saveSettings() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                var node = Preferences.userRoot().node("/Wilk/Marcin/Figury");

                node.putInt("left", getX());
                node.putInt("top", getY());
                node.putInt("width", getWidth());
                node.putInt("height", getHeight());
            }
        });
    }

    /**
     * Dodaje komponenty do głównego okna programu.
     */

    private void addComponents() {
        add(new MainComponent());
    }

    /**
     * Dodaje obsługę zdarzeń z klawiatury do głównego okna programu.
     */

    private void attachKeyboardHandler() {
        // Zmienia flagę isShiftPressed w zależności od wciśnięcia fizycznego przycisku
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT && !isShiftPressed) isShiftPressed = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT) isShiftPressed = false;
            }
        });
    }

    /**
     * Główny komponent programu rysujący figury i obsługujący funkcjonalności
     * z nimi związane. Zawiera dwa pola createdFigure, które przechowuje figurę
     * w momencie jej tworzenia oraz markedFigure, które przechowuje figurę,
     * która jest zaznaczona.
     */

    private class MainComponent extends JComponent {
        private Figure createdFigure;
        private Figure markedFigure;

        /**
         * Tworzy główny komponent, inicjalizuje pola oraz dodaje obsługę myszy
         * do danego komponentu.
         */

        public MainComponent() {
            markedFigure = null;
            createdFigure = null;

            attachMouseHandler();
        }

        /**
         * Wyświetla wszystkie figury w danym komponencie kolorując je na ich kolory.
         * @param g obiekt Graphics
         */

        @Override
        protected void paintComponent(Graphics g) {
            var g2 = (Graphics2D) g;

            // Iteruje po wszystkich figurach, zmienia kolor w zależności od figury,
            // rysuje figurę, a następnie zmienia kolor z powrotem na czarny.
            for (Figure f : figures) {
                g2.setColor(f.getColor());
                g2.fill(f.getShape());
                g2.setColor(Color.BLACK);
            }
        }

        /**
         * Dodaje do komponentu obsługę myszy.
         */

        private void attachMouseHandler() {
            var mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        int i = 0;

                        // Szuka figury, która została kliknięta, przekazuje ją do zmiennej markedFigure i
                        // przekazuje do tej figury położenie kursora względem niej.
                        while (i < figures.size()) {
                            if (figures.get(i).getShape().contains(e.getPoint())) {
                                markedFigure = figures.get(i);
                                markedFigure.setOffset(e.getX(), e.getY());
                                break;
                            }

                            i++;
                        }

                        // Jeżeli nie znajdzie figury to ustawia markedFigure na null.
                        if (i == figures.size()) markedFigure = null;
                    } else if(e.getButton() == MouseEvent.BUTTON3) {

                        // Szuka figury, która została kliknięta, a następnie wywołuje okno wyboru koloru,
                        // z którego pobiera wybrany kolor i przypisuje go danej figurze.
                        if(markedFigure != null && markedFigure.getShape().contains(e.getPoint())) {
                            markedFigure.setColor(JColorChooser.showDialog(null, "ASD", Color.RED));

                            repaint();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    createdFigure = null;
                    currentFigure = null;
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    // Skaluje figurę jeśli jest zaznaczona i jeżeli kursor się znajduje w jej obrębie.
                    if(markedFigure != null && markedFigure.getShape().contains(e.getPoint())) {
                        markedFigure.scale(e.getWheelRotation());
                        repaint();
                    }
                }
            };

            addMouseListener(mouseHandler);
            addMouseWheelListener(mouseHandler);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Jeżeli kursor znajduje się w obrębie zaznaczonej figury przesuwa ją.
                    if (currentFigure == null && markedFigure != null && markedFigure.getShape().contains(e.getPoint())) {
                        markedFigure.move(e.getX(), e.getY());
                        repaint();
                    } else if (currentFigure != null) {
                        // Jeżeli została wybrana figura, tworzy ją a następnie skaluje wraz z kursorem.
                        if(createdFigure == null) {
                            Figure f = new Figure(currentFigure, e.getX(), e.getY());
                            createdFigure = f;
                            figures.add(f);
                        } else {
                            Figure f = createdFigure;
                            f.resize(e.getX(), e.getY(), isShiftPressed);
                        }
                        repaint();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    // Zmienia kursor na krzyżyk jeżeli została wybrana figura do utworzenia.
                    if (currentFigure != null) setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    else setCursor(Cursor.getDefaultCursor());
                }
            });
        }
    }
}
