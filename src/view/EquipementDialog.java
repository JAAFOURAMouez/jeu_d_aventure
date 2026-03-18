package view;

import model.Equipement;
import model.Joueur;
import model.Monde;
import model.Objet;
import model.SlotEquipement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Fenêtre d'équipement du joueur.
 * 5 slots visibles (tête, main droite, main gauche, corps, pieds).
 * Cliquer sur un item de l'inventaire l'équipe.
 * Cliquer sur un slot équipé le déséquipe.
 */
public class EquipementDialog extends JDialog {

    private static final Color BG       = new Color(16, 12, 8);
    private static final Color BG_PANEL = new Color(26, 20, 12);
    private static final Color TEXT     = new Color(230, 210, 170);
    private static final Color ACCENT   = new Color(160, 110, 20);
    private static final Color SLOT_EMPTY  = new Color(35, 28, 18);
    private static final Color SLOT_FILLED = new Color(50, 40, 20);
    private static final Color BTN_EQUIP   = new Color(30, 80, 30);
    private static final Color BTN_DESEQUIP = new Color(100, 35, 10);
    private static final Font  FONT_TITLE  = new Font("Serif", Font.BOLD, 16);
    private static final Font  FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    private static final int   SLOT_ICON   = 60;
    private static final int   INV_ICON    = 48;

    private final VueGraphique parent;

    // Main panels rebuilt on each refresh
    private JPanel panelSlots;
    private JPanel panelInventaire;
    private JLabel labelStats;

    private static final SlotEquipement[] SLOT_ORDER = {
        SlotEquipement.TETE,
        SlotEquipement.MAIN_DROITE,
        SlotEquipement.MAIN_GAUCHE,
        SlotEquipement.CORPS,
        SlotEquipement.PIEDS
    };

    public EquipementDialog(Frame owner, VueGraphique parent) {
        super(owner, "⚔ Équipement", true);
        this.parent = parent;
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(6, 6));
        construireUI();
        pack();
        setMinimumSize(new Dimension(700, 460));
        setLocationRelativeTo(owner);
    }

    // =========================================================================
    // CONSTRUCTION
    // =========================================================================
    private void construireUI() {
        add(construireEntete(), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(1, 2, 10, 0));
        centre.setBackground(BG);
        centre.setBorder(new EmptyBorder(6, 8, 6, 8));

        // Panneau gauche : silhouette + slots
        panelSlots = new JPanel(new GridLayout(SLOT_ORDER.length, 1, 0, 6));
        panelSlots.setBackground(BG_PANEL);
        panelSlots.setBorder(creerBordure("Équipement actuel (cliquer pour déséquiper)"));
        remplirSlots();

        // Panneau droit : inventaire
        JPanel invWrapper = new JPanel(new BorderLayout());
        invWrapper.setBackground(BG_PANEL);
        invWrapper.setBorder(creerBordure("Inventaire (cliquer pour équiper)"));

        panelInventaire = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
        panelInventaire.setBackground(BG_PANEL);
        remplirInventaire();

        JScrollPane scrollInv = new JScrollPane(panelInventaire);
        scrollInv.setBorder(null);
        scrollInv.getViewport().setBackground(BG_PANEL);
        invWrapper.add(scrollInv, BorderLayout.CENTER);

        centre.add(panelSlots);
        centre.add(invWrapper);
        add(centre, BorderLayout.CENTER);

        // Bas : stats et bouton fermer
        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(BG);
        bas.setBorder(new EmptyBorder(0, 8, 6, 8));

        labelStats = new JLabel();
        labelStats.setForeground(TEXT);
        labelStats.setFont(FONT_SMALL);
        majStats();
        bas.add(labelStats, BorderLayout.CENTER);

        JButton fermer = bouton("Fermer", new Color(55, 55, 55));
        fermer.addActionListener(e -> dispose());
        bas.add(fermer, BorderLayout.EAST);

        add(bas, BorderLayout.SOUTH);
    }

    private JPanel construireEntete() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(10, 8, 4));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel titre = new JLabel("⚔ Équipement du Joueur");
        titre.setForeground(ACCENT);
        titre.setFont(FONT_TITLE);
        p.add(titre, BorderLayout.WEST);
        return p;
    }

    // =========================================================================
    // SLOTS D'ÉQUIPEMENT
    // =========================================================================
    private void remplirSlots() {
        panelSlots.removeAll();
        Equipement eq = Monde.getInstance().getJoueur().getEquipement();

        for (SlotEquipement s : SLOT_ORDER) {
            Objet obj = eq.getSlot(s);
            boolean bloquee = s == SlotEquipement.MAIN_GAUCHE && eq.mainGaucheBloquee();
            panelSlots.add(creerCaseSlot(s, obj, bloquee));
        }
        panelSlots.revalidate();
        panelSlots.repaint();
    }

    private JPanel creerCaseSlot(SlotEquipement s, Objet obj, boolean bloquee) {
        JPanel c = new JPanel(new BorderLayout(6, 0));
        c.setBackground(obj != null ? SLOT_FILLED : SLOT_EMPTY);
        c.setBorder(BorderFactory.createLineBorder(ACCENT.darker(), 1));
        c.setPreferredSize(new Dimension(280, 58));

        // Icône du slot
        String iconText = bloquee ? "🔒" : (obj != null ? s.icone : "○");
        JLabel icSlot = new JLabel(iconText + " " + s.libelle, SwingConstants.CENTER);
        icSlot.setPreferredSize(new Dimension(90, 58));
        icSlot.setForeground(bloquee ? new Color(120, 80, 80) : ACCENT);
        icSlot.setFont(new Font("SansSerif", Font.BOLD, 11));

        // Contenu
        JLabel contenu;
        if (bloquee) {
            contenu = new JLabel("[ Bloquée — arme 2 mains ]", SwingConstants.LEFT);
            contenu.setForeground(new Color(140, 100, 80));
        } else if (obj != null) {
            String extra = obj.isDeuxMains() ? " [2 mains]" : "";
            String dmg   = obj.estArme() ? "  ⚔ " + obj.getDegats() + " dmg" : "";
            contenu = new JLabel("<html><b>" + obj.getNom() + extra + "</b>" + dmg + "</html>",
                construireIcon(obj.getImagePath(), 40, 40), SwingConstants.LEFT);
            contenu.setForeground(TEXT);
        } else {
            contenu = new JLabel("(vide)", SwingConstants.LEFT);
            contenu.setForeground(new Color(100, 90, 70));
        }
        contenu.setFont(FONT_SMALL);

        // Bouton déséquiper
        JButton btn = bouton("Déséquiper", BTN_DESEQUIP);
        btn.setEnabled(obj != null && !bloquee);
        btn.setPreferredSize(new Dimension(90, 30));
        btn.addActionListener(e -> {
            Monde.getInstance().desequiper(s);
            rafraichir();
            parent.miseAJour();
        });

        c.add(icSlot,   BorderLayout.WEST);
        c.add(contenu,  BorderLayout.CENTER);
        c.add(btn,       BorderLayout.EAST);
        return c;
    }

    // =========================================================================
    // INVENTAIRE
    // =========================================================================
    private void remplirInventaire() {
        panelInventaire.removeAll();
        boolean aucun = true;
        for (Objet o : Monde.getInstance().getJoueur().getInventaire().getObjets()) {
            if (o.estEquipable()) {
                panelInventaire.add(creerCarteInv(o));
                aucun = false;
            }
        }
        if (aucun) {
            JLabel l = new JLabel("Aucun objet équipable dans l'inventaire.");
            l.setForeground(TEXT);
            l.setFont(FONT_SMALL);
            panelInventaire.add(l);
        }
        panelInventaire.revalidate();
        panelInventaire.repaint();
    }

    private JPanel creerCarteInv(Objet o) {
        JPanel c = new JPanel(new BorderLayout(4, 4));
        c.setBackground(new Color(36, 28, 16));
        c.setBorder(BorderFactory.createLineBorder(new Color(70, 58, 30), 1));
        c.setPreferredSize(new Dimension(130, 130));

        JLabel ic = new JLabel(construireIcon(o.getImagePath(), INV_ICON, INV_ICON), SwingConstants.CENTER);
        ic.setBorder(new EmptyBorder(4, 0, 0, 0));

        String extra = o.isDeuxMains() ? "<br/>[2 mains]" : "";
        String dmg   = o.estArme() ? "<br/>⚔ " + o.getDegats() + " dmg" : "";
        JLabel info  = new JLabel("<html><center><small><b>" + o.getNom()
            + "</b><br/>" + o.getSlot().libelle + extra + dmg + "</small></center></html>",
            SwingConstants.CENTER);
        info.setForeground(TEXT);
        info.setFont(FONT_SMALL);

        JButton btn = bouton("Équiper", BTN_EQUIP);
        btn.addActionListener(e -> {
            Monde.getInstance().equiper(o.getNom());
            rafraichir();
            parent.miseAJour();
        });

        c.add(ic,   BorderLayout.NORTH);
        c.add(info, BorderLayout.CENTER);
        c.add(btn,  BorderLayout.SOUTH);
        return c;
    }

    // =========================================================================
    // UTILITAIRES
    // =========================================================================
    private void rafraichir() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(6, 6));
        construireUI();
        revalidate();
        repaint();
    }

    private void majStats() {
        Joueur j = Monde.getInstance().getJoueur();
        int dmg = j.getEquipement().getDegatsTotal();
        labelStats.setText("⚔ Dégâts totaux équipés : " + dmg
            + "  |  ❤ " + j.getPointsDeVie() + " PV  |  💰 " + j.getOr() + " 🪙");
    }

    private JButton bouton(String texte, Color bg) {
        JButton b = new JButton(texte);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 10));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(4, 6, 4, 6));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(bg.brighter()); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private TitledBorder creerBordure(String titre) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), titre, 0, 0,
            new Font("SansSerif", Font.PLAIN, 11), TEXT);
    }

    private ImageIcon construireIcon(String path, int w, int h) {
        if (path != null && !path.isBlank()) {
            try {
                BufferedImage bi = ImageIO.read(new File(path));
                return new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH));
            } catch (Exception ignored) {}
        }
        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        g.setColor(new Color(50, 40, 20));
        g.fillRoundRect(0, 0, w, h, 10, 10);
        g.setColor(new Color(180, 150, 80));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("?", w/2-5, h/2+5);
        g.dispose();
        return new ImageIcon(buf);
    }

    // WrapLayout
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container t) { return layoutSize(t, true); }
        @Override public Dimension minimumLayoutSize(Container t) { Dimension d = layoutSize(t, false); d.width -= getHgap()+1; return d; }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int tw = target.getSize().width; if (tw==0) tw = Integer.MAX_VALUE;
                int hg = getHgap(), vg = getVgap(); Insets ins = target.getInsets();
                int maxW = tw - ins.left - ins.right - hg*2;
                Dimension dim = new Dimension(0,0); int rW=0, rH=0;
                for (Component m : target.getComponents()) if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rW+d.width>maxW) { dim.width=Math.max(dim.width,rW); dim.height+=rH+vg; rW=0; rH=0; }
                    rW+=d.width+hg; rH=Math.max(rH,d.height);
                }
                dim.width=Math.max(dim.width,rW); dim.height+=rH+ins.top+ins.bottom+vg*2; return dim;
            }
        }
    }
}
