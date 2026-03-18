package model;

public class Rue extends Lieu {
    public Rue(String nom, String description) {
        super(nom, description);
    }

    public Rue(String nom, String description, String imagePath) {
        super(nom, description, imagePath);
    }

    @Override
    public void traiter() {
        // Implementation for Composite pattern traversal
    }
}
