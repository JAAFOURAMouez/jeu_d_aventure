package model;

import util.Sujet;

public class Monde extends Sujet {
    private static Monde instance;

    private Joueur joueur;
    private StringBuilder etatJeu;
    private boolean victoire;
    private boolean defaite;

    private Monde() {
        etatJeu = new StringBuilder();
        initialiserMonde();
    }

    public static Monde getInstance() {
        if (instance == null) {
            instance = new Monde();
        }
        return instance;
    }

    private void initialiserMonde() {
        // Personnage principal
        joueur = new Joueur("Explorateur", 50, 5);
        victoire = false;
        defaite = false;

        // Création des lieux (avec leurs images de fond)
        String imgBase = "src/resources/images/";
        Rue ruePrincipale = new Rue("Rue principale",
                "La rue principale constitue le point central de la ville. Animée et poussiéreuse, elle relie les principaux lieux d'Alexandrie.",
                imgBase + "rue_principale.png");
        Rue agora = new Rue("Agora", "Lieu de rassemblement animé où marchands et citoyens échangent.",
                imgBase + "agora.png");
        Rue jardins = new Rue("Jardins Suspendus",
                "Un espace paisible rempli de végétation luxuriante, contrastant avec le reste de la ville.",
                imgBase + "jardins.png");
        Batiment temple = new Batiment("Temple", "Ancien temple mystérieux rempli de symboles et de secrets.", false,
                "", imgBase + "temple.png");
        Batiment bibliotheque = new Batiment("Bibliothèque", "Une immense bibliothèque remplie de savoir ancien.",
                false, "", imgBase + "bibliotheque.png");
        Batiment armurerie = new Batiment("Armurerie", "Lieu où sont stockées armes et armures.", false, "",
                imgBase + "armurerie.png");
        Batiment catacombes = new Batiment("Catacombes",
                "Un réseau souterrain sombre et dangereux, accessible uniquement avec une clé.", true, "Clé du temple",
                imgBase + "catacombes.png");

        // Connexions Rue Principale
        ruePrincipale.ajouterVoisin("nord", agora);
        ruePrincipale.ajouterVoisin("ouest", bibliotheque);
        ruePrincipale.ajouterVoisin("est", armurerie);
        ruePrincipale.ajouterVoisin("sud", catacombes);

        // Connexions Agora
        agora.ajouterVoisin("sud", ruePrincipale);
        agora.ajouterVoisin("ouest", jardins);
        agora.ajouterVoisin("est", temple);

        // Connexions Jardins
        jardins.ajouterVoisin("est", agora);

        // Connexions Temple
        temple.ajouterVoisin("ouest", agora);

        // Connexions Bibliothèque
        bibliotheque.ajouterVoisin("est", ruePrincipale);

        // Connexions Armurerie
        armurerie.ajouterVoisin("ouest", ruePrincipale);

        // Connexions Catacombes
        catacombes.ajouterVoisin("nord", ruePrincipale);

        // Ajout Objets et PNJ (avec leurs images et leurs prix)
        Objet cleCatacombes = new Objet("Clé du temple", "Clé", imgBase + "cle.png", 25);
        Objet relique = new Objet("Relique d'Alexandrie", "Artefact", imgBase + "relique.png", 200);
        Objet epee = new Objet("Épée", "Arme", imgBase + "epee.png", 30);
        Objet potion = new Objet("Potion de soin", "Potion", imgBase + "potion_de_soin.png", 15);

        // Agora PNJs et Objets
        agora.ajouterObjet(potion);
        PNJ marchand = new PNJ("Marchand", 20, false, new ComportementPacifique(),
                "J'ai ce qu'il vous faut... si vous avez de quoi payer.", imgBase + "pnj_marchand.png");
        // Inventaire du marchand (marchandises à vendre)
        marchand.getInventaire().ajouter(new Objet("Potion de soin", "Potion", imgBase + "potion_de_soin.png", 15));
        marchand.getInventaire().ajouter(new Objet("Dague", "Arme", imgBase + "epee.png", 12));
        marchand.getInventaire().ajouter(new Objet("Clé du temple", "Clé", imgBase + "cle.png", 25));
        agora.ajouterPNJ(marchand);

        // Temple PNJs et Objets
        temple.ajouterObjet(cleCatacombes);
        PNJ pretre = new PNJ("Prêtre", 20, false, new ComportementPacifique(),
                "La clé des profondeurs ne doit pas tomber entre de mauvaises mains.", imgBase + "pnj_pretre.png");
        temple.ajouterPNJ(pretre);

        // Armurerie PNJs et Objets
        armurerie.ajouterObjet(epee);
        PNJ forgeron = new PNJ("Forgeron", 30, false, new ComportementPacifique(),
                "Une bonne lame peut faire la différence entre la vie et la mort.", imgBase + "pnj_forgeron.png");
        armurerie.ajouterPNJ(forgeron);

        // Rue Principale PNJs
        PNJ garde = new PNJ("Garde", 40, false, new ComportementPacifique(),
                "Les catacombes sont scellées... seul un ancien artefact peut en briser le verrou.",
                imgBase + "pnj_garde.jpg");
        PNJ voleur = new PNJ("Voleur", 15, true, new ComportementHostile(),
                "Donne-moi ce que tu as, ou je me sers moi-même !", imgBase + "pnj_voleur.png");

        Objet dague = new Objet("Dague", "Arme", imgBase + "epee.png");
        Objet piecesOr = new Objet("Pièces d'or", "Trésor", null);
        voleur.getInventaire().ajouter(dague);
        voleur.getInventaire().ajouter(piecesOr);

        ruePrincipale.ajouterPNJ(garde);
        ruePrincipale.ajouterPNJ(voleur);

        // Catacombes PNJs et Objets
        catacombes.ajouterObjet(relique);
        PNJ creature = new PNJ("Créature ancienne", 60, true, new ComportementHostile(),
                "Tu n'aurais jamais dû venir...", imgBase + "pnj_creature.png");
        catacombes.ajouterPNJ(creature);

        // Objets équipables supplémentaires
        // Bibliothèque : Casque ancien + Bottes légères
        Objet casque = new Objet("Casque de bronze", "Casque", imgBase + "casque.png", 20,
                SlotEquipement.TETE, false);
        Objet bottes = new Objet("Bottes d'explorateur", "Bottes", imgBase + "bottes.png", 15,
                SlotEquipement.PIEDS, false);
        bibliotheque.ajouterObjet(casque);
        bibliotheque.ajouterObjet(bottes);

        // Armurerie : Armure de cuir + Épée à deux mains
        Objet armureCuir = new Objet("Armure de cuir", "Armure", imgBase + "armure.png", 35,
                SlotEquipement.CORPS, false);
        Objet epee2M = new Objet("Épée à deux mains", "Arme", imgBase + "epee.png", 55,
                SlotEquipement.MAIN_DROITE, true);
        epee2M.setDegats(22); // Arme 2H plus puissante
        armurerie.ajouterObjet(armureCuir);
        armurerie.ajouterObjet(epee2M);

        // Position initiale
        joueur.setPosition(ruePrincipale);
        ajouterMessage(joueur.getPosition().getNom() + "\n" + joueur.getPosition().getDescription());
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public StringBuilder getEtat() {
        return etatJeu;
    }

    public void effacerMessages() {
        etatJeu.setLength(0);
    }

    public void ajouterMessage(String msg) {
        etatJeu.append(msg).append("\n");
        informe();
    }

    public boolean isVictoire() {
        return victoire;
    }

    public boolean isDefaite() {
        return defaite;
    }

    public void deplacerJoueur(String direction) {
        if (victoire || defaite)
            return;

        Lieu actuel = joueur.getPosition();
        Lieu destination = actuel.getVoisin(direction);

        if (destination == null) {
            ajouterMessage("Vous ne pouvez pas aller par là.");
            return;
        }

        if (destination instanceof Batiment) {
            Batiment b = (Batiment) destination;
            if (b.isEstVerrouille()) {
                ajouterMessage("Le bâtiment " + b.getNom() + " est verrouillé. Il faut la clé: " + b.getCleRequise());
                return;
            }
        }

        joueur.setPosition(destination);
        ajouterMessage("Vous vous déplacez vers : " + destination.getNom());
        ajouterMessage(destination.getDescription());
    }

    public void entrerBatiment() {
        // En cherchant s'il y a un batiment adjacent unique (simplification de "Entrer"
        // si non explicite)
        // Mais selon le schéma, on clique juste sur Entrer si le batiment est le
        // voisin.
        // Puisque deplacerJoueur gère très bien le passage entre Rue/Batiment,
        // nous l'utiliserons principalement, ou implementerons entrerBatiment si
        // l'interface
        // nécessite une action séparée de type "Entrer dans ce qu'on a devant nous".
        // Nous le laissons comme utilitaire si la vue selectionne un lieu de la carte.
        ajouterMessage("Utilisez les boutons de direction (Nord/Sud/Est/Ouest) pour entrer.");
    }

    public void ramasserObjet(String nomObjet) {
        if (victoire || defaite)
            return;
        Lieu actuel = joueur.getPosition();
        Objet cible = null;

        for (Objet o : actuel.getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) {
                cible = o;
                break;
            }
        }

        if (cible == null) {
            ajouterMessage("Il n'y a pas d'objet nommé " + nomObjet + " ici.");
            return;
        }

        if (joueur.getInventaire().ajouter(cible)) {
            actuel.retirerObjet(cible);
            ajouterMessage("Vous avez ramassé : " + cible.getNom());

            if (cible.getNom().equals("Relique d'Alexandrie")) {
                validerVictoire();
            }
        } else {
            ajouterMessage("Votre inventaire est plein !");
        }
    }

    public void verifierEtatJoueur() {
        if (joueur.getPointsDeVie() <= 0) {
            defaite = true;
            ajouterMessage("\n*** GAME OVER ***\nVos points de vie sont tombés à 0.");
        }
    }

    public void validerVictoire() {
        victoire = true;
        ajouterMessage("\n*** VICTOIRE ! ***\nVous avez trouvé la Relique d'Alexandrie !");
    }

    public void attaquerPNJ(String nomPNJ) {
        if (victoire || defaite)
            return;
        Lieu actuel = joueur.getPosition();
        PNJ cible = null;
        for (PNJ p : actuel.getPnjs()) {
            if (p.getNom().equalsIgnoreCase(nomPNJ)) {
                cible = p;
                break;
            }
        }

        if (cible == null) {
            ajouterMessage("Personnage introuvable pour attaquer.");
            return;
        }

        cible.setPointsDeVie(cible.getPointsDeVie() - 10);
        ajouterMessage("Vous attaquez " + cible.getNom() + " et infligez 10 dégâts.");

        if (cible.getPointsDeVie() <= 0) {
            ajouterMessage(cible.getNom() + " a été vaincu !");

            // Drop items from PNJ inventory to the ground
            for (Objet obj : cible.getInventaire().getObjets()) {
                actuel.ajouterObjet(obj);
                ajouterMessage(cible.getNom() + " a fait tomber : " + obj.getNom());
            }
            cible.getInventaire().getObjets().clear();

            actuel.retirerPNJ(cible);
        } else {
            cible.agir(joueur); // Riposte
            verifierEtatJoueur();
        }
    }

    public void parlerPNJ(String nomPNJ) {
        if (victoire || defaite)
            return;
        Lieu actuel = joueur.getPosition();
        PNJ cible = null;
        for (PNJ p : actuel.getPnjs()) {
            if (p.getNom().equalsIgnoreCase(nomPNJ)) {
                cible = p;
                break;
            }
        }

        if (cible == null) {
            ajouterMessage("Personnage introuvable pour parler.");
            return;
        }

        if (cible.isEstHostile()) {
            ajouterMessage(cible.getNom() + " : " + cible.getDialogue());
            ajouterMessage(cible.getNom() + " est hostile ! Il vous attaque.");
            cible.agir(joueur);
            verifierEtatJoueur();
        } else {
            ajouterMessage(cible.getNom() + " : " + cible.getDialogue());
        }
    }

    public void utiliserObjet(String nomObjet) {
        if (victoire || defaite)
            return;
        Objet cible = null;
        for (Objet o : joueur.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) {
                cible = o;
                break;
            }
        }

        if (cible == null) {
            ajouterMessage("Vous n'avez pas cet objet.");
            return;
        }

        if (cible.getType().equalsIgnoreCase("Potion")) {
            joueur.setPointsDeVie(joueur.getPointsDeVie() + 20);
            joueur.getInventaire().retirer(cible);
            ajouterMessage("Vous buvez la potion et récupérez 20 points de vie.");
        } else if (cible.getType().equalsIgnoreCase("Clé")) {
            // Cherche un batiment adjacent verrouillé nécessitant cette clé
            boolean opened = false;
            for (Lieu voisin : joueur.getPosition().getVoisins().values()) {
                if (voisin instanceof Batiment) {
                    Batiment b = (Batiment) voisin;
                    if (b.isEstVerrouille() && b.getCleRequise().equalsIgnoreCase(cible.getNom())) {
                        b.deverrouiller();
                        joueur.getInventaire().retirer(cible);
                        ajouterMessage("Vous déverrouillez " + b.getNom() + " avec " + cible.getNom() + ".");
                        opened = true;
                        break;
                    }
                }
            }
            if (!opened) {
                ajouterMessage("Vous ne pouvez pas utiliser " + cible.getNom() + " ici.");
            }
        } else if (cible.getType().equalsIgnoreCase("Arme")) {
            ajouterMessage("Vous équipez l'arme : " + cible.getNom() + ", augmentant vos dégâts ! (Non implémenté)");
        } else {
            ajouterMessage("Cet objet ne peut pas être utilisé.");
        }
    }

    public void jeterObjet(String nomObjet) {
        if (victoire || defaite)
            return;
        Objet cible = null;
        for (Objet o : joueur.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) {
                cible = o;
                break;
            }
        }

        if (cible != null) {
            joueur.getInventaire().retirer(cible);
            joueur.getPosition().ajouterObjet(cible);
            ajouterMessage("Vous avez jeté : " + cible.getNom());
        }
    }

    /**
     * Le joueur achète un objet au Marchand.
     * @param marchand le PNJ Marchand
     * @param nomObjet le nom de l'objet à acheter
     */
    public void achatObjet(PNJ marchand, String nomObjet) {
        Objet cible = null;
        for (Objet o : marchand.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) {
                cible = o;
                break;
            }
        }
        if (cible == null) {
            ajouterMessage("Le marchand ne possède pas cet objet.");
            return;
        }
        if (joueur.getOr() < cible.getPrix()) {
            ajouterMessage("Vous n'avez pas assez d'or ! (besoin : " + cible.getPrix() + " 🪙, vous avez : " + joueur.getOr() + " 🪙)");
            return;
        }
        if (!joueur.getInventaire().ajouter(cible)) {
            ajouterMessage("Votre inventaire est plein !");
            return;
        }
        marchand.getInventaire().retirer(cible);
        joueur.setOr(joueur.getOr() - cible.getPrix());
        ajouterMessage("Vous achetez « " + cible.getNom() + " » pour " + cible.getPrix() + " 🪙. Or restant : " + joueur.getOr() + " 🪙");
    }

    /**
     * Le joueur vend un objet au Marchand.
     * @param marchand le PNJ Marchand
     * @param nomObjet le nom de l'objet à vendre
     */
    public void venteObjet(PNJ marchand, String nomObjet) {
        Objet cible = null;
        for (Objet o : joueur.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) {
                cible = o;
                break;
            }
        }
        if (cible == null) {
            ajouterMessage("Vous ne possédez pas cet objet.");
            return;
        }
        int prixVente = Math.max(1, cible.getPrix() / 2); // On vend à moitié prix
        joueur.getInventaire().retirer(cible);
        marchand.getInventaire().ajouter(cible);
        joueur.setOr(joueur.getOr() + prixVente);
        ajouterMessage("Vous vendez « " + cible.getNom() + " » pour " + prixVente + " 🪙. Or total : " + joueur.getOr() + " 🪙");
    }

    /**
     * Le Forgeron améliore une arme du joueur (+5 dégâts par niveau).
     * Coût = 20 × (niveau actuel + 1) pièces d'or. Max niveau 5.
     */
    public void ameliorerArme(String nomArme) {
        Objet arme = null;
        for (Objet o : joueur.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomArme) && o.estArme()) {
                arme = o;
                break;
            }
        }
        if (arme == null) {
            ajouterMessage("Vous ne possédez pas cette arme.");
            return;
        }
        if (arme.getNiveau() >= 5) {
            ajouterMessage(arme.getNom() + " est déjà au niveau maximum (+5) !");
            return;
        }
        int cout = 20 * (arme.getNiveau() + 1);
        if (joueur.getOr() < cout) {
            ajouterMessage("Pas assez d'or pour améliorer " + arme.getNom()
                + " ! (besoin : " + cout + " 🪙, vous avez : " + joueur.getOr() + " 🪙)");
            return;
        }
        joueur.setOr(joueur.getOr() - cout);
        arme.setNiveau(arme.getNiveau() + 1);
        arme.setDegats(arme.getDegats() + 5);
        ajouterMessage("⚒ " + arme.getNom() + " améliorée au niveau +" + arme.getNiveau()
            + " ! (" + arme.getDegats() + " dégâts). Or restant : " + joueur.getOr() + " 🪙");
    }

    /**
     * Le joueur équipe un objet de son inventaire.
     * Si un objet était déjà dans le même slot, il retourne dans l'inventaire.
     */
    public void equiper(String nomObjet) {
        Objet cible = null;
        for (Objet o : joueur.getInventaire().getObjets()) {
            if (o.getNom().equalsIgnoreCase(nomObjet)) { cible = o; break; }
        }
        if (cible == null) {
            ajouterMessage("Vous ne possédez pas cet objet.");
            return;
        }
        if (!cible.estEquipable()) {
            ajouterMessage(cible.getNom() + " n'est pas équipable.");
            return;
        }
        try {
            joueur.getInventaire().retirer(cible);
            Objet ancien = joueur.getEquipement().equiper(cible);
            if (ancien != null) {
                joueur.getInventaire().ajouter(ancien);
                ajouterMessage("Vous remplacez « " + ancien.getNom() + " » par « " + cible.getNom() + " ».");
            } else {
                ajouterMessage("Vous équipez : " + cible.getNom()
                    + (cible.isDeuxMains() ? " [arme à 2 mains]" : "") + ".");
            }
        } catch (IllegalArgumentException ex) {
            joueur.getInventaire().ajouter(cible); // remettre en inventaire si erreur
            ajouterMessage("⚠ " + ex.getMessage());
        }
    }

    /**
     * Le joueur déséquipe un slot et remet l'objet dans son inventaire.
     */
    public void desequiper(SlotEquipement slot) {
        Objet retiré = joueur.getEquipement().desequiper(slot);
        if (retiré == null) {
            ajouterMessage("Aucun objet dans cet emplacement.");
        } else {
            if (joueur.getInventaire().ajouter(retiré)) {
                ajouterMessage("Vous déséquipez : " + retiré.getNom() + ".");
            } else {
                // Inventaire plein : remettre l'objet dans l'équipement
                joueur.getEquipement().equiper(retiré);
                ajouterMessage("Inventaire plein — impossible de déséquiper " + retiré.getNom() + ".");
            }
        }
    }
}
