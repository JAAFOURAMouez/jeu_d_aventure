package model;

/**
 * Emplacements d'équipement disponibles sur le joueur.
 * ARME_2M occupe MAIN_DROITE et MAIN_GAUCHE simultanément.
 */
public enum SlotEquipement {
    TETE        ("Tête",       "🪖"),
    MAIN_DROITE ("Main droite","⚔"),
    MAIN_GAUCHE ("Main gauche","🛡"),
    CORPS       ("Corps",      "🥋"),
    PIEDS       ("Pieds",      "🥾"),
    AUCUN       ("—",          "");

    public final String libelle;
    public final String icone;

    SlotEquipement(String libelle, String icone) {
        this.libelle = libelle;
        this.icone   = icone;
    }
}
