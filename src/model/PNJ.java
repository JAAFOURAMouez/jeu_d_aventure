package model;

public class PNJ extends Personnage {
    private boolean estHostile;
    private Comportement comportement;
    private String dialogue;
    private Inventaire inventaire;
    private String imagePath;

    public PNJ(String nom, int pointsDeVie, boolean estHostile, Comportement comportement, String dialogue) {
        this(nom, pointsDeVie, estHostile, comportement, dialogue, null);
    }

    public PNJ(String nom, int pointsDeVie, boolean estHostile, Comportement comportement, String dialogue, String imagePath) {
        super(nom, pointsDeVie);
        this.estHostile = estHostile;
        this.comportement = comportement;
        this.dialogue = dialogue;
        this.imagePath = imagePath;
        this.inventaire = new Inventaire(5);
    }

    public String getImagePath() {
        return imagePath;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public boolean isEstHostile() {
        return estHostile;
    }

    public void agir(Joueur j) {
        if (comportement != null) {
            comportement.appliquerComportement(j);
        }
    }

    public String getDialogue() {
        return dialogue;
    }

    @Override
    public String toString() {
        return getNom() + (estHostile ? " (hostile)" : " (amical)");
    }
}
