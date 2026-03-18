package model;

public class Batiment extends Lieu {
    private boolean estVerrouille;
    private String cleRequise;

    public Batiment(String nom, String description, boolean estVerrouille, String cleRequise) {
        super(nom, description);
        this.estVerrouille = estVerrouille;
        this.cleRequise = cleRequise;
    }

    public Batiment(String nom, String description, boolean estVerrouille, String cleRequise, String imagePath) {
        super(nom, description, imagePath);
        this.estVerrouille = estVerrouille;
        this.cleRequise = cleRequise;
    }

    public boolean isEstVerrouille() {
        return estVerrouille;
    }

    public String getCleRequise() {
        return cleRequise;
    }

    public void deverrouiller() {
        this.estVerrouille = false;
    }

    @Override
    public void traiter() {
        // Implementation for Composite pattern traversal
    }
}
