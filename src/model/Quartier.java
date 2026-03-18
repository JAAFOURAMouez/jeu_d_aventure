package model;

import java.util.ArrayList;
import java.util.List;

public class Quartier extends Lieu {
    private List<Lieu> enfants;

    public Quartier(String nom, String description) {
        super(nom, description);
        this.enfants = new ArrayList<>();
    }

    public void ajouterLieu(Lieu l) {
        enfants.add(l);
    }

    public void retirerLieu(Lieu l) {
        enfants.remove(l);
    }

    public List<Lieu> getEnfants() {
        return enfants;
    }

    @Override
    public void traiter() {
        // Implementation for Composite pattern traversal
        for (Lieu l : enfants) {
            l.traiter();
        }
    }
}
