package controller;

import model.Monde;

public class CmdParler implements Commande {
    private String cible;
    private Monde modele;

    public CmdParler(String cible, Monde modele) {
        this.cible = cible;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.parlerPNJ(cible);
    }
}
