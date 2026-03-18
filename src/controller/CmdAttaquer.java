package controller;

import model.Monde;

public class CmdAttaquer implements Commande {
    private String cible;
    private Monde modele;

    public CmdAttaquer(String cible, Monde modele) {
        this.cible = cible;
        this.modele = modele;
    }

    @Override
    public void executer() {
        modele.attaquerPNJ(cible);
    }
}
