package controller;

import model.Monde;

public class CmdUtiliser implements Commande {
    private String objet;
    private Monde modele;

    public CmdUtiliser(String objet, Monde modele) {
        this.objet = objet;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.utiliserObjet(objet);
    }
}
