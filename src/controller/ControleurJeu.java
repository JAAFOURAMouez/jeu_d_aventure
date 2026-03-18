package controller;

import model.Monde;
import model.PNJ;
import view.VueGraphique;

public class ControleurJeu {
    private Monde modele;
    private VueGraphique vue;

    public ControleurJeu(Monde modele) {
        this.modele = modele;
    }

    public void setVue(VueGraphique vue) {
        this.vue = vue;
    }

    public void traiterAction(Commande c) {
        if (c != null) {
            c.executer();
        }
    }

    public void actionDeplacer(String direction) {
        traiterAction(new CmdDeplacer(direction, modele));
    }

    public void actionRamasser(String nomObjet) {
        traiterAction(new CmdRamasser(nomObjet, modele));
    }

    public void actionAttaquer(String nomPNJ) {
        traiterAction(new CmdAttaquer(nomPNJ, modele));
    }

    public void actionParler(String nomPNJ) {
        traiterAction(new CmdParler(nomPNJ, modele));
    }

    public void actionUtiliser(String nomObjet) {
        traiterAction(new CmdUtiliser(nomObjet, modele));
    }

    public void actionJeter(String nomObjet) {
        traiterAction(new CmdJeter(nomObjet, modele));
    }
    
    public void actionEntrer() {
        modele.entrerBatiment();
    }
}
