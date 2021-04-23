import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

/**
 * Obiekt Figure reprezentuje figurę utworzoną w programie.
 * Posiada nazwę, kolor, kształt oraz prostokąt w który ten kształt jest wpisany.
 * Dodatkowo posiada współrzędne swojego lewego górnego rogu oraz odległość
 * tego rogu od kursora myszy podczas przesuwania.
 */

public class Figure implements Serializable {
    private final Rectangle2D bound;
    private final Figures name;
    private Shape shape;
    private Color color;

    private int offsetX;
    private int offsetY;

    private int x;
    private int y;

    /**
     * Tworzy nową figurę o nazwie name oraz współrzędnych (mouseX, mouseY).
     * @param name nazwa figury
     * @param mouseX współrzędna x figury
     * @param mouseY współrzędna y figury
     */

    public Figure(Figures name, int mouseX, int mouseY) {
        // Utworzenie prostokąta, w który wpisana jest figura.
        bound = new Rectangle2D.Double(mouseX, mouseY, 0, 0);

        this.name = name;
        color = Color.BLACK;

        this.x = mouseX;
        this.y = mouseY;

        // Utworzenie kształtu w zależności od nazwy figury.
        switch (name) {
            case RECTANGLE -> shape = new Rectangle2D.Double(mouseX, mouseY, 0, 0);
            case CIRCLE -> shape = new Ellipse2D.Double(mouseX, mouseY, 0, 0);
            case TRIANGLE -> shape = new Polygon(new int[]{mouseX, mouseX, mouseX}, new int[]{mouseY, mouseY, mouseY}, 3);
        }
    }

    /**
     * Zmienia wielkość figury w zależności od współrzędnych (mouseX, mouseY).
     * Dodatkowo można wymusić by figura była foremna przekazując isShiftPressed = true.
     * Stosować tylko podczas tworzenia figury.
     * @param mouseX współrzędna x do której będzie powiększona figura
     * @param mouseY współrzędna y do której będzie powiększona figura
     * @param isShiftPressed flaga sprawiająca że figura będzie foremna
     */

    public void resize(int mouseX, int mouseY, boolean isShiftPressed) {
        int newX, newY, newWidth, newHeight;

        // Liczenie nowych wymiarów.
        newWidth = Math.abs(x - mouseX);
        newHeight = Math.abs(y - mouseY);

        // Zmienianie figury na foremną w zależności od flagi isShiftPressed.
        if(isShiftPressed) {
            int side = Math.min(newWidth, newHeight);

            newWidth = side;
            newHeight = side;
        }

        // Obsługiwanie przypadku kiedy współrzędne (x,y) będą mniejsze niż współrzędne figury.
        if (mouseX < x) newX = x - newWidth;
            else newX = x;

        if (mouseY < y) newY = y - newHeight;
            else newY = y;

        // Transformacja prostokąta figury i wpisywanie do niego kształtu.
        bound.setFrame(newX, newY, newWidth, newHeight);
        merge();
    }

    /**
     * Skaluje figurę w zależności od wartości percentage.
     * @param percentage wartość o jaką skalujemy figurę
     *                   dla percentage mniejsze niz 0 figura zmniejsza się
     *                   dla percentage wieksze niz 0 figura powiększa się
     */

    public void scale(int percentage) {
        // Wyliczenie przeskalowanych wymiarów w zależności od starych wymiarów.
        int oldWidth = (int) getBound().getWidth();
        int oldHeight = (int) getBound().getHeight();

        int newWidth = (int)(oldWidth + percentage * oldWidth / 10.0);
        int newHeight = (int)(oldHeight + percentage * oldHeight / 10.0);

        // Transformacja prostokąta figury i wpisywanie do niego kształtu.
        bound.setFrame(x, y, newWidth, newHeight);
        merge();
    }

    /**
     * Przemieszcza figurę w zależności od przemieszczenia parametrów (x,y).
     * Wykorzystuje offsetX i offsetY żeby przemieszczać figurę w sposób naturalny.
     * @param x współrzędna x przemieszczenia
     * @param y współrzędna y przemieszczenia
     */

    public void move(int x, int y) {
        // Transformacja prostokąta figury w zależności od starych współrzędnych,
        // a następnie wpisanie do niego kształtu.
        int oldWidth = (int) bound.getWidth();
        int oldHeight = (int) bound.getHeight();

        bound.setFrame(x - offsetX, y - offsetY, oldWidth, oldHeight);

        this.x = (int) bound.getX();
        this.y = (int) bound.getY();

        merge();
    }

    /**
     * Wpisuje kształt figury w jej prostokąt.
     */

    private void merge() {
        // Wpisuje kształt figury w jej prostokąt w zależności od nazwy figury.
        switch (name) {
            case RECTANGLE -> ((Rectangle2D)shape).setFrame(bound);
            case CIRCLE -> ((Ellipse2D)shape).setFrame(bound);
            case TRIANGLE -> {
                Polygon p = (Polygon) shape;

                int boundX = (int) bound.getX();
                int boundY = (int) bound.getY();

                int boundWidth = (int) bound.getWidth();
                int boundHeight = (int) bound.getHeight();

                p.xpoints[0] = boundX;
                p.xpoints[1] = boundX + boundWidth / 2;
                p.xpoints[2] = boundX + boundWidth;

                p.ypoints[0] = boundY + boundHeight;
                p.ypoints[1] = boundY;
                p.ypoints[2] = boundY + boundHeight;
            }
        }
    }

    /**
     * Ustala odległość kursora myszy od górnego lewego rogu prostkąta naszej figury,
     * która jest potrzebna przy przesuwaniu figury.
     * @param x współrzędna x kursora myszy
     * @param y współrzędna y kursora myszy
     */

    public void setOffset(int x, int y) {
        offsetX = (int) (x - getBound().getX());
        offsetY = (int) (y - getBound().getY());
    }

    /**
     * Zwraca kształt figury.
     * @return kształt figury
     */

    public Shape getShape() {
        return shape;
    }

    /**
     * Zwraca prostokąt figury.
     * @return prostokąt figury
     */

    public Rectangle2D getBound() {
        return bound;
    }

    /**
     * Zwraca kolor figury.
     * @return kolor figury
     */

    public Color getColor() { return color; }

    /**
     * Ustawia nowy kolor figury.
     * @param color nowy kolor figury
     */

    public void setColor(Color color) { this.color = color; }
}
