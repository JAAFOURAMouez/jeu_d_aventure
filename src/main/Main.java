package main;

import controller.ControleurJeu;
import model.Monde;
import view.VueGraphique;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialisation MVC
            Monde modele = Monde.getInstance();
            ControleurJeu controleur = new ControleurJeu(modele);
            VueGraphique vue = new VueGraphique(controleur);
            
            // Lier les composants
            controleur.setVue(vue);
            modele.attache(vue);
            
            // Premier affichage
            vue.setVisible(true);
            vue.miseAJour();
        });
    }
}
