package controller;

import model.Monde;

public class CmdRamasser implements Commande {
    private String objet;
    private Monde modele;

    public CmdRamasser(String objet, Monde modele) {
        this.objet = objet;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.ramasserObjet(objet);
    }
}
