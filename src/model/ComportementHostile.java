package model;

public class ComportementHostile implements Comportement {
    @Override
    public void appliquerComportement(Joueur j) {
        // En combat, le PNJ hostile riposte/attaque le joueur
        j.setPointsDeVie(j.getPointsDeVie() - 10);
        Monde.getInstance().getEtat().append("Le PNJ vous attaque et vous inflige 10 points de dégâts.\n");
    }
}
