package model;

public class Joueur extends Personnage {
    private Inventaire inventaire;
    private Lieu position;
    private int or;  // Pièces d'or
    private Equipement equipement;

    public Joueur(String nom, int pointsDeVie, int capaciteMax) {
        super(nom, pointsDeVie);
        this.inventaire  = new Inventaire(capaciteMax);
        this.or          = 30;
        this.equipement  = new Equipement();
    }

    public int getOr() { return or; }
    public void setOr(int or) { this.or = or; }

    public Equipement getEquipement() { return equipement; }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public Lieu getPosition() {
        return position;
    }

    public void setPosition(Lieu position) {
        this.position = position;
    }
}
