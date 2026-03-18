package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Gère les objets équipés sur le joueur.
 * Cinq emplacements : TETE, MAIN_DROITE, MAIN_GAUCHE, CORPS, PIEDS.
 * Une arme à deux mains occupe MAIN_DROITE et bloque MAIN_GAUCHE.
 */
public class Equipement {

    /** Arme à deux mains stockée pour référence croisée */
    private Objet armeDeuixMains = null;

    private final Map<SlotEquipement, Objet> slots = new HashMap<>();

    public Equipement() {
        slots.put(SlotEquipement.TETE, null);
        slots.put(SlotEquipement.MAIN_DROITE, null);
        slots.put(SlotEquipement.MAIN_GAUCHE, null);
        slots.put(SlotEquipement.CORPS, null);
        slots.put(SlotEquipement.PIEDS, null);
    }

    /** Retourne l'objet dans un slot (null si vide). */
    public Objet getSlot(SlotEquipement s) {
        if (s == SlotEquipement.MAIN_GAUCHE && armeDeuixMains != null)
            return armeDeuixMains;
        return slots.get(s);
    }

    /** Vrai si la main gauche est bloquée par une arme 2 mains. */
    public boolean mainGaucheBloquee() {
        return armeDeuixMains != null;
    }

    /**
     * Équipe un objet.
     * 
     * @return l'objet précédemment en place (à remettre en inventaire), null si le
     *         slot était vide.
     * @throws IllegalArgumentException si l'objet n'est pas équipable ou si le type
     *                                  de slot est invalide.
     */
    public Objet equiper(Objet objet) {
        SlotEquipement cible = objet.getSlot();
        if (cible == SlotEquipement.AUCUN) {
            throw new IllegalArgumentException("Cet objet n'est pas équipable.");
        }

        Objet ancien;

        if (objet.isDeuxMains()) {
            // Arme 2H : occupe la main droite et bloque la gauche
            ancien = slots.get(SlotEquipement.MAIN_DROITE);
            // Si une arme occupait la main gauche, on la retourne aussi (on prend l'ancien
            // MG d'abord)
            // Pour simplifier, la main gauche sera libérée silencieusement si occupée.
            slots.put(SlotEquipement.MAIN_DROITE, objet);
            armeDeuixMains = objet;
            return ancien;
        } else {
            // 1H : si une arme 2H est en main droite, on la retire d'abord
            if (cible == SlotEquipement.MAIN_DROITE && armeDeuixMains != null) {
                Objet a2h = armeDeuixMains;
                armeDeuixMains = null;
                slots.put(SlotEquipement.MAIN_DROITE, objet);
                return a2h;
            }
            if (cible == SlotEquipement.MAIN_GAUCHE && armeDeuixMains != null) {
                throw new IllegalArgumentException("La main gauche est bloquée par une arme à deux mains !");
            }
            ancien = slots.get(cible);
            slots.put(cible, objet);
            return ancien;
        }
    }

    /**
     * Déséquipe l'objet dans un slot donné.
     * 
     * @return l'objet retiré, null si le slot était vide.
     */
    public Objet desequiper(SlotEquipement s) {
        Objet objet;
        if (s == SlotEquipement.MAIN_DROITE && armeDeuixMains != null) {
            objet = armeDeuixMains;
            armeDeuixMains = null;
            slots.put(SlotEquipement.MAIN_DROITE, null);
        } else {
            objet = slots.put(s, null);
        }
        return objet;
    }

    /**
     * Calcule les dégâts totaux des armes équipées.
     */
    public int getDegatsTotal() {
        int total = 0;
        Objet md = slots.get(SlotEquipement.MAIN_DROITE);
        Objet mg = slots.get(SlotEquipement.MAIN_GAUCHE);
        if (md != null && md.estArme())
            total += md.getDegats();
        if (armeDeuixMains == null && mg != null && mg.estArme())
            total += mg.getDegats();
        return total;
    }

    /** Résumé de l'équipement actuel. */
    public String resume() {
        StringBuilder sb = new StringBuilder();
        for (SlotEquipement s : new SlotEquipement[] {
                SlotEquipement.TETE, SlotEquipement.MAIN_DROITE,
                SlotEquipement.MAIN_GAUCHE, SlotEquipement.CORPS, SlotEquipement.PIEDS }) {

            Objet o = getSlot(s);
            String nom = o != null ? o.getNom() : "(vide)";
            String extra = "";
            if (s == SlotEquipement.MAIN_DROITE && armeDeuixMains != null)
                extra = " [2 mains]";
            if (s == SlotEquipement.MAIN_GAUCHE && armeDeuixMains != null)
                extra = " [bloquée]";
            sb.append(s.icone).append(" ").append(s.libelle).append(" : ").append(nom).append(extra).append("\n");
        }
        return sb.toString();
    }
}
