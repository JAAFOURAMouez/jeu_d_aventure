package model;

import java.util.ArrayList;
import java.util.List;

public class Inventaire {
    private int capaciteMax;
    private List<Objet> objets;

    public Inventaire(int capaciteMax) {
        this.capaciteMax = capaciteMax;
        this.objets = new ArrayList<>();
    }

    public boolean ajouter(Objet o) {
        if (!estPlein()) {
            objets.add(o);
            return true;
        }
        return false;
    }

    public void retirer(Objet o) {
        objets.remove(o);
    }

    public boolean estPlein() {
        return objets.size() >= capaciteMax;
    }

    public List<Objet> getObjets() {
        return objets;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public int getTaille() {
        return objets.size();
    }
}
