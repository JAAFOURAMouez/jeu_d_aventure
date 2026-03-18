package model;

public class ComportementPacifique implements Comportement {
    @Override
    public void appliquerComportement(Joueur j) {
        // Le dialogue sera géré par la commande Parler
        Monde.getInstance().getEtat().append("Le personnage vous observe calmement.\n");
    }
}
