package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Lieu {
    private String nom;
    private String description;
    private String imagePath;
    private List<Objet> objets;
    private List<PNJ> pnjs;
    private Map<String, Lieu> voisins;

    public Lieu(String nom, String description) {
        this(nom, description, null);
    }

    public Lieu(String nom, String description, String imagePath) {
        this.nom = nom;
        this.description = description;
        this.imagePath = imagePath;
        this.objets = new ArrayList<>();
        this.pnjs = new ArrayList<>();
        this.voisins = new HashMap<>();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public List<Objet> getObjets() {
        return objets;
    }

    public void ajouterObjet(Objet o) {
        objets.add(o);
    }

    public void retirerObjet(Objet o) {
        objets.remove(o);
    }

    public List<PNJ> getPnjs() {
        return pnjs;
    }

    public void ajouterPNJ(PNJ pnj) {
        pnjs.add(pnj);
    }

    public void retirerPNJ(PNJ pnj) {
        pnjs.remove(pnj);
    }

    public void ajouterVoisin(String direction, Lieu lieu) {
        voisins.put(direction.toLowerCase(), lieu);
    }

    public Lieu getVoisin(String direction) {
        return voisins.get(direction.toLowerCase());
    }

    public Map<String, Lieu> getVoisins() {
        return voisins;
    }

    // Abstract method for Composite pattern processing later if needed
    public abstract void traiter();

    @Override
    public String toString() {
        return nom;
    }
}
