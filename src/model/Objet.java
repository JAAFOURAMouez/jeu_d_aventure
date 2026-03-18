package model;

public class Objet {
    private String nom;
    private String type;
    private String imagePath;
    private int prix; // Valeur en pièces d'or
    private int degats; // Dégâts (si arme)
    private int niveau; // Niveau d'amélioration (0 = base)
    private SlotEquipement slot; // Emplacement d'équipement
    private boolean deuixMains; // Vrai si arme à deux mains

    private int bonusDefense; // Réduction des dégâts encaissés (si armure/casque/bottes)
    private int bonusPV; // Points de vie supplémentaires (si armure défensive)

    public Objet(String nom, String type) {
        this(nom, type, null, 10, inferSlot(type), false);
    }

    public Objet(String nom, String type, String imagePath) {
        this(nom, type, imagePath, 10, inferSlot(type), false);
    }

    public Objet(String nom, String type, String imagePath, int prix) {
        this(nom, type, imagePath, prix, inferSlot(type), false);
    }

    /** Constructeur complet permettant de spécifier slot et 2H manuellement */
    public Objet(String nom, String type, String imagePath, int prix,
            SlotEquipement slot, boolean deuxMains) {
        this.nom = nom;
        this.type = type;
        this.imagePath = imagePath;
        this.prix = prix;
        this.slot = slot;
        this.deuixMains = deuxMains;
        // Dégâts de base selon le type
        this.degats = type.equalsIgnoreCase("Arme") ? 10 : 0;
        this.niveau = 0;
        // Bonus défense / PV par défaut selon le type d'équipement
        this.bonusDefense = switch (type.toLowerCase()) {
            case "armure" -> 8;
            case "casque" -> 4;
            case "bottes" -> 2;
            default -> 0;
        };
        this.bonusPV = type.equalsIgnoreCase("Armure") ? 10 : 0;
    }

    /** Déduit le slot d'équipement depuis le type d'objet */
    private static SlotEquipement inferSlot(String type) {
        return switch (type.toLowerCase()) {
            case "arme" -> SlotEquipement.MAIN_DROITE;
            case "casque" -> SlotEquipement.TETE;
            case "armure" -> SlotEquipement.CORPS;
            case "bottes" -> SlotEquipement.PIEDS;
            default -> SlotEquipement.AUCUN;
        };
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getPrix() {
        return prix;
    }

    public int getDegats() {
        return degats;
    }

    public void setDegats(int d) {
        this.degats = d;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int n) {
        this.niveau = n;
    }

    public SlotEquipement getSlot() {
        return slot;
    }

    public boolean isDeuxMains() {
        return deuixMains;
    }

    public boolean estEquipable() {
        return slot != SlotEquipement.AUCUN;
    }

    public boolean estArme() {
        return type.equalsIgnoreCase("Arme");
    }

    public int getBonusDefense() {
        return bonusDefense;
    }

    public void setBonusDefense(int b) {
        this.bonusDefense = b;
    }

    public int getBonusPV() {
        return bonusPV;
    }

    public void setBonusPV(int b) {
        this.bonusPV = b;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        if (estArme() && niveau > 0) {
            return nom + " [+" + niveau + "] (" + degats + " dmg)";
        }
        return nom + " [" + type + "]";
    }
}
