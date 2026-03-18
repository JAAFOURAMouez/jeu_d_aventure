package view;

import model.Inventaire;
import model.Joueur;
import model.Monde;
import model.Objet;
import model.PNJ;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Fenêtre de boutique du Marchand.
 * Permet d'acheter et de vendre des objets.
 */
public class MarchandDialog extends JDialog {

    private static final Color BG         = new Color(20, 15, 10);
    private static final Color BG_PANEL   = new Color(30, 23, 15);
    private static final Color TEXT        = new Color(235, 220, 180);
    private static final Color ACCENT      = new Color(184, 134, 11);
    private static final Color BTN_BUY     = new Color(30, 90, 30);
    private static final Color BTN_SELL    = new Color(90, 50, 10);
    private static final Font  FONT_TITLE  = new Font("Serif", Font.BOLD, 16);
    private static final Font  FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
    private static final int   ICON        = 54;

    private final PNJ     marchand;
    private final VueGraphique parent;
    private JLabel        labelOr;

    public MarchandDialog(Frame owner, PNJ marchand, VueGraphique parent) {
        super(owner, "Boutique du Marchand", true);
        this.marchand = marchand;
        this.parent   = parent;
        setBackground(BG);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(6, 6));

        construireUI();
        pack();
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(owner);
    }

    private void construireUI() {
        // --- En-tête ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 8, 5));
        header.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titre = new JLabel("⚜ Boutique du Marchand");
        titre.setForeground(ACCENT);
        titre.setFont(FONT_TITLE);

        labelOr = new JLabel();
        labelOr.setForeground(new Color(255, 215, 0));
        labelOr.setFont(new Font("SansSerif", Font.BOLD, 13));
        majOr();

        header.add(titre,    BorderLayout.WEST);
        header.add(labelOr,  BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Corps : deux colonnes ---
        JPanel corps = new JPanel(new GridLayout(1, 2, 8, 0));
        corps.setBackground(BG);
        corps.setBorder(new EmptyBorder(8, 8, 8, 8));

        corps.add(construirePanneauMarchand());
        corps.add(construirePanneauJoueur());
        add(corps, BorderLayout.CENTER);

        // --- Pied ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG);
        JButton fermer = bouton("Fermer", new Color(60, 60, 60));
        fermer.addActionListener(e -> dispose());
        footer.add(fermer);
        add(footer, BorderLayout.SOUTH);
    }

    /** Panneau gauche : articles du marchand (à acheter) */
    private JPanel construirePanneauMarchand() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(creerBordure("Wares du Marchand (cliquer pour acheter)"));

        JPanel grille = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
        grille.setBackground(BG_PANEL);

        for (Objet o : marchand.getInventaire().getObjets()) {
            grille.add(creerCarteObjet(o, true));
        }

        JScrollPane scroll = new JScrollPane(grille);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_PANEL);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /** Panneau droit : inventaire du joueur (à vendre) */
    private JPanel construirePanneauJoueur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(creerBordure("Votre inventaire (cliquer pour vendre — ½ prix)"));

        JPanel grille = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
        grille.setBackground(BG_PANEL);

        Inventaire inv = Monde.getInstance().getJoueur().getInventaire();
        for (Objet o : inv.getObjets()) {
            grille.add(creerCarteObjet(o, false));
        }

        JScrollPane scroll = new JScrollPane(grille);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_PANEL);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /** Crée une carte cliquable pour un objet (achat ou vente) */
    private JPanel creerCarteObjet(Objet o, boolean achat) {
        JPanel carte = new JPanel(new BorderLayout(4, 4));
        carte.setBackground(new Color(40, 32, 20));
        carte.setBorder(BorderFactory.createLineBorder(new Color(80, 65, 35), 1));
        carte.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icône
        JLabel icone = new JLabel(construireIcon(o.getImagePath(), ICON, ICON), SwingConstants.CENTER);
        icone.setPreferredSize(new Dimension(ICON + 4, ICON + 4));

        // Texte
        int prixAffiche = achat ? o.getPrix() : Math.max(1, o.getPrix() / 2);
        JLabel info = new JLabel(
            "<html><center><b>" + o.getNom() + "</b><br/>"
            + "[" + o.getType() + "]<br/>"
            + "<font color='#FFD700'>" + prixAffiche + " 🪙</font></center></html>",
            SwingConstants.CENTER);
        info.setForeground(TEXT);
        info.setFont(FONT_NORMAL);

        // Bouton
        JButton btn = achat
            ? bouton("Acheter", BTN_BUY)
            : bouton("Vendre",  BTN_SELL);

        btn.addActionListener(e -> {
            if (achat) {
                Monde.getInstance().achatObjet(marchand, o.getNom());
            } else {
                Monde.getInstance().venteObjet(marchand, o.getNom());
            }
            majOr();
            parent.miseAJour();   // rafraîchit la vue principale
            rafraichir();          // rafraîchit la bo dialog
        });

        carte.add(icone, BorderLayout.NORTH);
        carte.add(info,  BorderLayout.CENTER);
        carte.add(btn,   BorderLayout.SOUTH);
        return carte;
    }

    /** Recharge le contenu de la boîte de dialogue */
    private void rafraichir() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(6, 6));
        construireUI();
        revalidate();
        repaint();
    }

    private void majOr() {
        Joueur j = Monde.getInstance().getJoueur();
        labelOr.setText("💰 Or : " + j.getOr() + " 🪙");
    }

    // =========================================================================
    // UTILITAIRES
    // =========================================================================
    private JButton bouton(String texte, Color bg) {
        JButton b = new JButton(texte);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(4, 8, 4, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private TitledBorder creerBordure(String titre) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT),
            titre, 0, 0,
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
        Graphics2D g2 = buf.createGraphics();
        g2.setColor(new Color(60, 50, 20));
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.drawString("?", w / 2 - 3, h / 2 + 4);
        g2.dispose();
        return new ImageIcon(buf);
    }

    // WrapLayout inline (identique à celui de VueGraphique)
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container t) { return layoutSize(t, true); }
        @Override public Dimension minimumLayoutSize(Container t) {
            Dimension d = layoutSize(t, false);
            d.width -= (getHgap() + 1);
            return d;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int tw = target.getSize().width;
                if (tw == 0) tw = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets ins = target.getInsets();
                int maxW = tw - ins.left - ins.right - hgap * 2;
                Dimension dim = new Dimension(0, 0);
                int rowW = 0, rowH = 0;
                for (Component m : target.getComponents()) {
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowW + d.width > maxW) {
                            dim.width = Math.max(dim.width, rowW);
                            dim.height += rowH + vgap;
                            rowW = 0; rowH = 0;
                        }
                        rowW += d.width + hgap;
                        rowH = Math.max(rowH, d.height);
                    }
                }
                dim.width = Math.max(dim.width, rowW);
                dim.height += rowH + ins.top + ins.bottom + vgap * 2;
                return dim;
            }
        }
    }
}
