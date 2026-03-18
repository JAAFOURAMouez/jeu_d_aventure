package controller;

import model.Monde;

public class CmdDeplacer implements Commande {
    private String direction;
    private Monde modele;

    public CmdDeplacer(String direction, Monde modele) {
        this.direction = direction;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.deplacerJoueur(direction);
    }
}
