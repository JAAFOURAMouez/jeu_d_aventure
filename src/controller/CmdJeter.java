package controller;

import model.Monde;

public class CmdJeter implements Commande {
    private String objet;
    private Monde modele;

    public CmdJeter(String objet, Monde modele) {
        this.objet = objet;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.jeterObjet(objet);
    }
}
