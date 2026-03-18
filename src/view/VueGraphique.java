package view;

import controller.ControleurJeu;
import model.Inventaire;
import model.Joueur;
import model.Lieu;
import model.Monde;
import model.Objet;
import model.PNJ;
import util.Observateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.List;

/**
 * Vue graphique principale du jeu avec affichage à base d'images.
 * - Fond de pièce : image associée au Lieu courant.
 * - Objets au sol / inventaire : icônes cliquables (JLabel + ImageIcon).
 * - PNJs : icônes cliquables sur le panneau de la pièce.
 */
public class VueGraphique extends JFrame implements Observateur {

    // --- CONSTANTES D'AFFICHAGE ---
    private static final int ICON_SIZE = 72; // taille des icônes objets/PNJ
    private static final int THUMB_SIZE = 56; // taille miniature inventaire
    private static final Color BG_PANEL = new Color(20, 15, 10);
    private static final Color BG_SIDE = new Color(30, 23, 15);
    private static final Color TEXT_COLOR = new Color(235, 220, 180);
    private static final Color ACCENT = new Color(184, 134, 11);
    private static final Color BTN_NAV = new Color(26, 64, 95);
    private static final Color BTN_ACT = new Color(100, 40, 20);
    private static final Color BTN_DANGER = new Color(160, 30, 30);
    private static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_DESC = new Font("Serif", Font.PLAIN, 13);

    // --- ÉTAT SÉLECTION ---
    private String selectedObjetLieu = null;
    private String selectedPNJ = null;
    private String selectedItemInv = null;
    private String dernierLieuNom = null; // pour détecter les changements de lieu

    // --- COMPOSANTS PRINCIPAUX ---
    private JLabel labelLieu; // titre du lieu + PV
    private JTextArea textDescription; // console de jeu
    private JPanel panelScenePNJ; // fond de salle + PNJ (centre-gauche)
    private JPanel panelObjSol; // objets au sol
    private JPanel panelInventaire;// inventaire
    private JLabel labelInvSize;
    private JLabel labelPVBar;

    private ControleurJeu controleur;

    // =========================================================================
    // CONSTRUCTION
    // =========================================================================
    public VueGraphique(ControleurJeu controleur) {
        this.controleur = controleur;
        setTitle("La Relique d'Alexandrie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_PANEL);
        setLayout(new BorderLayout(6, 6));

        construireUI();
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
    }

    // =========================================================================
    // CONSTRUCTION UI
    // =========================================================================
    private void construireUI() {
        add(construireNord(), BorderLayout.NORTH);
        add(construireCentre(), BorderLayout.CENTER);
        add(construireSud(), BorderLayout.SOUTH);
    }

    /** Barre de titre haut */
    private JPanel construireNord() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(10, 8, 5));
        p.setBorder(new EmptyBorder(8, 14, 8, 14));

        labelLieu = new JLabel("Lieu Actuel");
        labelLieu.setForeground(ACCENT);
        labelLieu.setFont(FONT_TITLE);

        labelPVBar = new JLabel("PV : ??");
        labelPVBar.setForeground(new Color(220, 80, 80));
        labelPVBar.setFont(new Font("SansSerif", Font.BOLD, 14));

        p.add(labelLieu, BorderLayout.WEST);
        p.add(labelPVBar, BorderLayout.EAST);
        return p;
    }

    /** Zone centrale : scène (fond + PNJ) | Objets sol | Inventaire */
    private JPanel construireCentre() {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(4, 6, 4, 6));

        // --- SCÈNE (fond de salle + PNJs) ---
        panelScenePNJ = new JPanel() {
            private Image bgImg = null;
            private String lastPath = null;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Monde m = Monde.getInstance();
                Lieu l = m.getJoueur().getPosition();
                String path = (l != null) ? l.getImagePath() : null;

                if (path != null && !path.equals(lastPath)) {
                    bgImg = chargerImage(path, getWidth(), getHeight());
                    lastPath = path;
                }
                if (bgImg != null) {
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(60, 40, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelScenePNJ.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 16));
        panelScenePNJ.setPreferredSize(new Dimension(480, 320));
        panelScenePNJ.setOpaque(true);
        panelScenePNJ.setBorder(BorderFactory.createLineBorder(ACCENT, 1));

        // --- OBJETS SOL ---
        JPanel sideLeft = new JPanel(new BorderLayout());
        sideLeft.setBackground(BG_SIDE);
        sideLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT), "Objets au sol",
                0, 0, FONT_LABEL, TEXT_COLOR));

        panelObjSol = new JPanel();
        panelObjSol.setLayout(new WrapLayout(FlowLayout.LEFT, 8, 8));
        panelObjSol.setBackground(BG_SIDE);
        JScrollPane scrollObjSol = new JScrollPane(panelObjSol);
        scrollObjSol.setBorder(null);
        scrollObjSol.setBackground(BG_SIDE);
        scrollObjSol.getViewport().setBackground(BG_SIDE);
        sideLeft.add(scrollObjSol, BorderLayout.CENTER);

        // --- INVENTAIRE ---
        JPanel sideRight = new JPanel(new BorderLayout());
        sideRight.setBackground(BG_SIDE);
        sideRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT), "Inventaire",
                0, 0, FONT_LABEL, TEXT_COLOR));

        panelInventaire = new JPanel();
        panelInventaire.setLayout(new WrapLayout(FlowLayout.LEFT, 8, 8));
        panelInventaire.setBackground(BG_SIDE);
        JScrollPane scrollInv = new JScrollPane(panelInventaire);
        scrollInv.setBorder(null);
        scrollInv.setBackground(BG_SIDE);
        scrollInv.getViewport().setBackground(BG_SIDE);

        labelInvSize = new JLabel("(0/5)");
        labelInvSize.setForeground(TEXT_COLOR);
        labelInvSize.setFont(FONT_LABEL);
        labelInvSize.setBorder(new EmptyBorder(2, 6, 2, 2));

        sideRight.add(scrollInv, BorderLayout.CENTER);
        sideRight.add(labelInvSize, BorderLayout.SOUTH);

        // Assemblage horizontal
        JSplitPane splitSides = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideLeft, sideRight);
        splitSides.setResizeWeight(0.5);
        splitSides.setDividerSize(4);
        splitSides.setBackground(BG_PANEL);
        splitSides.setPreferredSize(new Dimension(350, 320));

        // Scène + côtés
        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelScenePNJ, splitSides);
        splitMain.setResizeWeight(0.6);
        splitMain.setDividerSize(4);

        p.add(splitMain, BorderLayout.CENTER);
        return p;
    }

    /** Console + boutons en bas */
    private JPanel construireSud() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(2, 6, 6, 6));

        // Console de jeu
        textDescription = new JTextArea(6, 60);
        textDescription.setEditable(false);
        textDescription.setBackground(new Color(15, 12, 8));
        textDescription.setForeground(TEXT_COLOR);
        textDescription.setFont(FONT_DESC);
        textDescription.setLineWrap(true);
        textDescription.setWrapStyleWord(true);

        JScrollPane scrollDesc = new JScrollPane(textDescription);
        scrollDesc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT), "Journal",
                0, 0, FONT_LABEL, TEXT_COLOR));
        p.add(scrollDesc, BorderLayout.CENTER);

        // Panneaux de boutons
        p.add(construireBoutons(), BorderLayout.SOUTH);
        return p;
    }

    /** Tous les boutons d'action */
    private JPanel construireBoutons() {
        JPanel tout = new JPanel(new GridLayout(2, 1, 2, 2));
        tout.setBackground(BG_PANEL);

        // Rangée 1 : déplacements
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        nav.setBackground(BG_PANEL);
        nav.add(bouton("↑ Nord", BTN_NAV, e -> controleur.actionDeplacer("nord")));
        nav.add(bouton("↓ Sud", BTN_NAV, e -> controleur.actionDeplacer("sud")));
        nav.add(bouton("← Ouest", BTN_NAV, e -> controleur.actionDeplacer("ouest")));
        nav.add(bouton("→ Est", BTN_NAV, e -> controleur.actionDeplacer("est")));
        nav.add(bouton("Entrer", BTN_NAV, e -> controleur.actionEntrer()));

        // Rangée 2 : actions
        JPanel act = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        act.setBackground(BG_PANEL);
        act.add(bouton("Ramasser", BTN_ACT, e -> {
            if (selectedObjetLieu != null)
                controleur.actionRamasser(selectedObjetLieu);
            else
                afficherMessage("Cliquez d'abord sur un objet au sol.");
        }));
        act.add(bouton("Utiliser", BTN_ACT, e -> {
            if (selectedItemInv != null)
                controleur.actionUtiliser(selectedItemInv);
            else
                afficherMessage("Cliquez d'abord sur un objet de votre inventaire.");
        }));
        act.add(bouton("Jeter", BTN_ACT, e -> {
            if (selectedItemInv != null)
                controleur.actionJeter(selectedItemInv);
            else
                afficherMessage("Cliquez d'abord sur un objet de votre inventaire.");
        }));
        act.add(bouton("Parler", BTN_ACT, e -> {
            if (selectedPNJ != null) {
                // Cas spécial : ouvrir la boutique si c'est le Marchand
                Lieu actuelLieu = Monde.getInstance().getJoueur().getPosition();
                model.PNJ pnjCible = actuelLieu.getPnjs().stream()
                        .filter(p -> p.getNom().equals(selectedPNJ))
                        .findFirst().orElse(null);
                if (pnjCible != null && pnjCible.getNom().equalsIgnoreCase("Marchand")) {
                    new MarchandDialog(this, pnjCible, this).setVisible(true);
                } else if (pnjCible != null && pnjCible.getNom().equalsIgnoreCase("Forgeron")) {
                    new ForgeronDialog(this, this).setVisible(true);
                } else {
                    controleur.actionParler(selectedPNJ);
                }
            } else {
                afficherMessage("Cliquez d'abord sur un personnage.");
            }
        }));
        act.add(bouton("Attaquer", BTN_DANGER, e -> {
            if (selectedPNJ != null)
                controleur.actionAttaquer(selectedPNJ);
            else
                afficherMessage("Cliquez d'abord sur un personnage.");
        }));
        act.add(bouton("Équiper", new Color(80, 50, 0), e ->
            new EquipementDialog(this, this).setVisible(true)));
        act.add(bouton("Quitter", new Color(60, 60, 60), e -> System.exit(0)));

        tout.add(nav);
        tout.add(act);
        return tout;
    }

    // =========================================================================
    // MISE À JOUR (Observer)
    // =========================================================================
    @Override
    public void miseAJour() {
        SwingUtilities.invokeLater(() -> {
            Monde monde = Monde.getInstance();
            Joueur joueur = monde.getJoueur();
            Lieu actuel = joueur.getPosition();

            // --- Journal ---
            if (monde.getEtat().length() > 0) {
                afficherMessage(monde.getEtat().toString());
                monde.effacerMessages();
            }

            // --- Titre ---
            if (actuel != null) {
                labelLieu.setText("📍 " + actuel.getNom());
                labelPVBar.setText("❤ " + joueur.getPointsDeVie() + " PV  |  💰 " + joueur.getOr() + " 🪙");

                // Réinitialiser sélections SEULEMENT si on change de lieu
                boolean lieuChange = !actuel.getNom().equals(dernierLieuNom);
                if (lieuChange) {
                    selectedObjetLieu = null;
                    selectedPNJ = null;
                    dernierLieuNom = actuel.getNom();
                }

                // Vérifier que les PNJs/objets sélectionnés sont toujours présents
                boolean pnjPresent = actuel.getPnjs().stream()
                        .anyMatch(p -> p.getNom().equals(selectedPNJ));
                if (!pnjPresent) selectedPNJ = null;

                boolean objPresent = actuel.getObjets().stream()
                        .anyMatch(o -> o.getNom().equals(selectedObjetLieu));
                if (!objPresent) selectedObjetLieu = null;

                // --- Fond + PNJs ---
                panelScenePNJ.removeAll();
                panelScenePNJ.repaint(); // force repaint du fond
                for (PNJ pnj : actuel.getPnjs()) {
                    panelScenePNJ.add(creerIconePNJ(pnj));
                }

                // --- Objets sol ---
                panelObjSol.removeAll();
                for (Objet o : actuel.getObjets()) {
                    panelObjSol.add(creerIconeObjet(o, false));
                }

                // --- Inventaire ---
                Inventaire inv = joueur.getInventaire();
                panelInventaire.removeAll();
                // Reset inv selection if item no longer present
                boolean invSelectionStillPresent = false;
                for (Objet o : inv.getObjets()) {
                    panelInventaire.add(creerIconeObjet(o, true));
                    if (o.getNom().equals(selectedItemInv))
                        invSelectionStillPresent = true;
                }
                if (!invSelectionStillPresent)
                    selectedItemInv = null;

                labelInvSize.setText("(" + inv.getTaille() + "/" + inv.getCapaciteMax() + " objets)");

                panelScenePNJ.revalidate();
                panelObjSol.revalidate();
                panelInventaire.revalidate();
                panelScenePNJ.repaint();
                panelObjSol.repaint();
                panelInventaire.repaint();
            }

            // --- Fin de partie ---
            if (monde.isVictoire() || monde.isDefaite()) {
                desactiverBoutons();
                String msg = monde.isVictoire()
                        ? "VICTOIRE !\nVous avez trouvé la Relique d'Alexandrie !"
                        : "GAME OVER\nVos points de vie sont tombés à 0.";
                int type = monde.isVictoire()
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog(this, msg, "Fin de partie", type);
            }
        });
    }

    // =========================================================================
    // CRÉATION DES ICÔNES CLIQUABLES
    // =========================================================================

    /** Crée un JLabel-icône pour un PNJ visible dans la scène */
    private JLabel creerIconePNJ(PNJ pnj) {
        ImageIcon icon = construireIcon(pnj.getImagePath(), ICON_SIZE, ICON_SIZE,
                pnj.isEstHostile() ? new Color(120, 20, 20) : new Color(20, 60, 20));

        JLabel label = new JLabel(icon, SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setText("<html><center><b>" + pnj.getNom() + "</b><br/>"
                + (pnj.isEstHostile() ? "⚔" : "☮") + " " + pnj.getPointsDeVie() + " PV</center></html>");
        label.setForeground(Color.WHITE);
        label.setFont(FONT_LABEL);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 140));
        label.setToolTipText(pnj.getNom());
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        styleIcone(label, pnj.getNom().equals(selectedPNJ));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedPNJ = pnj.getNom();
                // Surligner la sélection
                miseAJour();
            }
        });
        return label;
    }

    /** Crée un JLabel-icône pour un objet (sol ou inventaire) */
    private JLabel creerIconeObjet(Objet obj, boolean inventaire) {
        int sz = inventaire ? THUMB_SIZE : ICON_SIZE;
        String selected = inventaire ? selectedItemInv : selectedObjetLieu;
        boolean isSelected = obj.getNom().equals(selected);

        ImageIcon icon = construireIcon(obj.getImagePath(), sz, sz, new Color(50, 40, 10));

        JLabel label = new JLabel(icon, SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setText("<html><center>" + obj.getNom() + "</center></html>");
        label.setForeground(TEXT_COLOR);
        label.setFont(FONT_LABEL);
        label.setOpaque(true);
        label.setBackground(BG_SIDE);
        label.setToolTipText(obj.getNom() + " [" + obj.getType() + "]");
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        styleIcone(label, isSelected);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inventaire)
                    selectedItemInv = obj.getNom();
                else
                    selectedObjetLieu = obj.getNom();
                // Mettre à jour les bordures
                rafraichirSélections();
            }
        });
        return label;
    }

    /** Met à jour l'apparence des icônes sélectionnées sans tout recharger */
    private void rafraichirSélections() {
        for (Component c : panelObjSol.getComponents()) {
            if (c instanceof JLabel l && l.getToolTipText() != null) {
                String nom = l.getToolTipText().split(" \\[")[0];
                styleIcone(l, nom.equals(selectedObjetLieu));
            }
        }
        for (Component c : panelInventaire.getComponents()) {
            if (c instanceof JLabel l && l.getToolTipText() != null) {
                String nom = l.getToolTipText().split(" \\[")[0];
                styleIcone(l, nom.equals(selectedItemInv));
            }
        }
        panelObjSol.repaint();
        panelInventaire.repaint();
    }

    private void styleIcone(JLabel label, boolean selected) {
        if (selected) {
            label.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
            label.setBackground(new Color(60, 50, 20));
        } else {
            label.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 50), 1));
            label.setBackground(BG_SIDE);
        }
    }

    // =========================================================================
    // UTILITAIRES
    // =========================================================================

    /**
     * Charge une image depuis le disque, la redimensionne, retourne null si absente
     */
    private Image chargerImage(String path, int w, int h) {
        if (path == null || path.isBlank())
            return null;
        try {
            BufferedImage bi = ImageIO.read(new File(path));
            return bi.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Construit un ImageIcon depuis un chemin (redimensionné).
     * Si le fichier est absent, retourne une icône colorée (carré plein).
     */
    private ImageIcon construireIcon(String path, int w, int h, Color fallbackColor) {
        Image img = chargerImage(path, w, h);
        if (img != null) {
            return new ImageIcon(img);
        }
        // Fallback : carré coloré
        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buf.createGraphics();
        g2.setColor(fallbackColor);
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.drawString("?", w / 2 - 3, h / 2 + 4);
        g2.dispose();
        return new ImageIcon(buf);
    }

    /** Crée un bouton stylé */
    private JButton bouton(String texte, Color bg, ActionListener listener) {
        JButton b = new JButton(texte);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(5, 12, 5, 12)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        });
        b.addActionListener(listener);
        return b;
    }

    public void afficherMessage(String msg) {
        textDescription.append(msg + "\n");
        textDescription.setCaretPosition(textDescription.getDocument().getLength());
    }

    private void desactiverBoutons() {
        desactiverR(getContentPane());
    }

    private void desactiverR(Component c) {
        if (c instanceof JButton b && !b.getText().equals("Quitter")) {
            b.setEnabled(false);
        } else if (c instanceof Container cont) {
            for (Component enfant : cont.getComponents())
                desactiverR(enfant);
        }
    }

    // =========================================================================
    // WrapLayout (FlowLayout avec retour à la ligne automatique)
    // =========================================================================
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right - hgap * 2;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                for (Component m : target.getComponents()) {
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            dim.width = Math.max(dim.width, rowWidth);
                            dim.height += rowHeight + vgap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                dim.width = Math.max(dim.width, rowWidth);
                dim.height += rowHeight + insets.top + insets.bottom + vgap * 2;
                return dim;
            }
        }
    }
}
