/**
 * Enum zawierający nazwy dostępnych w programie figur.
 */

public enum Figures {
    CIRCLE("koło"),
    RECTANGLE("prostokąt"),
    TRIANGLE("trójkąt");

    private String name;

    Figures(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }
}
