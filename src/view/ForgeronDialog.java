package view;

import model.Inventaire;
import model.Joueur;
import model.Monde;
import model.Objet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Fenêtre de forge du Forgeron.
 * Affiche les armes de l'inventaire du joueur et propose de les améliorer.
 */
public class ForgeronDialog extends JDialog {

    private static final Color BG         = new Color(18, 12, 8);
    private static final Color BG_PANEL   = new Color(28, 20, 12);
    private static final Color TEXT       = new Color(235, 210, 170);
    private static final Color ACCENT     = new Color(200, 100, 20);   // orange forge
    private static final Color BTN_FORGE  = new Color(120, 55, 10);
    private static final Color BTN_CLOSE  = new Color(55, 55, 55);
    private static final Font  FONT_TITLE = new Font("Serif", Font.BOLD, 16);
    private static final Font  FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private static final int   ICON       = 64;

    private final VueGraphique parent;
    private JLabel labelOr;
    private JPanel panelArmes;

    // — Coût selon niveau — coût = 20 × (niveau_actuel + 1)
    private static int coutUpgrade(Objet arme) {
        return 20 * (arme.getNiveau() + 1);
    }

    public ForgeronDialog(Frame owner, VueGraphique parent) {
        super(owner, "⚒ Forge du Forgeron", true);
        this.parent = parent;
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(6, 6));

        construireUI();
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(owner);
    }

    // =========================================================================
    // UI
    // =========================================================================
    private void construireUI() {
        add(construireEntete(), BorderLayout.NORTH);
        add(construireCorps(),  BorderLayout.CENTER);
        add(construirePied(),   BorderLayout.SOUTH);
    }

    private JPanel construireEntete() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(10, 7, 4));
        p.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel titre = new JLabel("⚒ Services du Forgeron");
        titre.setForeground(ACCENT);
        titre.setFont(FONT_TITLE);

        labelOr = new JLabel();
        labelOr.setForeground(new Color(255, 215, 0));
        labelOr.setFont(new Font("SansSerif", Font.BOLD, 13));
        majOr();

        p.add(titre,   BorderLayout.WEST);
        p.add(labelOr, BorderLayout.EAST);
        return p;
    }

    private JPanel construireCorps() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(8, 8, 0, 8));

        // Explication
        JLabel info = new JLabel(
            "<html><i>Le forgeron améliore vos armes. Chaque amélioration coûte "
            + "20 × (niveau+1) 🪙 et ajoute +5 dégâts. Niveau maximum : +5.</i></html>");
        info.setForeground(TEXT);
        info.setFont(FONT_SMALL);
        info.setBorder(new EmptyBorder(0, 0, 8, 0));
        outer.add(info, BorderLayout.NORTH);

        // Grille des armes
        panelArmes = new JPanel();
        panelArmes.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        panelArmes.setBackground(BG_PANEL);
        remplirArmes();

        JScrollPane scroll = new JScrollPane(panelArmes);
        scroll.setBorder(creerBordure("Armes dans votre inventaire"));
        scroll.getViewport().setBackground(BG_PANEL);
        outer.add(scroll, BorderLayout.CENTER);

        return outer;
    }

    private JPanel construirePied() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(BG);
        JButton fermer = bouton("Fermer", BTN_CLOSE);
        fermer.addActionListener(e -> dispose());
        p.add(fermer);
        return p;
    }

    // =========================================================================
    // REMPLISSAGE DES ARMES
    // =========================================================================
    private void remplirArmes() {
        panelArmes.removeAll();

        List<Objet> armes = new ArrayList<>();
        for (Objet o : Monde.getInstance().getJoueur().getInventaire().getObjets()) {
            if (o.estArme()) armes.add(o);
        }

        if (armes.isEmpty()) {
            JLabel vide = new JLabel("Vous n'avez aucune arme dans votre inventaire.");
            vide.setForeground(TEXT);
            vide.setFont(FONT_SMALL);
            vide.setBorder(new EmptyBorder(20, 20, 20, 20));
            panelArmes.add(vide);
        } else {
            for (Objet arme : armes) {
                panelArmes.add(creerCarteArme(arme));
            }
        }
        panelArmes.revalidate();
        panelArmes.repaint();
    }

    private JPanel creerCarteArme(Objet arme) {
        JPanel carte = new JPanel(new BorderLayout(6, 4));
        carte.setBackground(new Color(38, 26, 14));
        carte.setBorder(BorderFactory.createLineBorder(ACCENT.darker(), 1));
        carte.setPreferredSize(new Dimension(170, 210));

        // Icône
        JLabel icone = new JLabel(construireIcon(arme.getImagePath(), ICON, ICON), SwingConstants.CENTER);
        icone.setPreferredSize(new Dimension(ICON + 4, ICON + 4));
        icone.setBorder(new EmptyBorder(6, 0, 0, 0));

        // Infos
        boolean maxLevel = arme.getNiveau() >= 5;
        int cout = coutUpgrade(arme);
        String couleurNiveau = arme.getNiveau() == 0 ? "#AAAAAA"
                             : arme.getNiveau() < 3  ? "#88DD88"
                                                     : "#FFAA44";

        JLabel info = new JLabel(
            "<html><center>"
            + "<b>" + arme.getNom() + "</b><br/>"
            + "<font color='" + couleurNiveau + "'>Niveau : +" + arme.getNiveau() + "</font><br/>"
            + "⚔ " + arme.getDegats() + " dégâts<br/>"
            + (maxLevel
                ? "<font color='#FF6666'>Niveau MAX</font>"
                : "<font color='#FFD700'>Coût : " + cout + " 🪙</font>"
                + "<br/><font color='#88DD88'>→ ⚔ " + (arme.getDegats() + 5) + " dégâts</font>")
            + "</center></html>",
            SwingConstants.CENTER);
        info.setForeground(TEXT);
        info.setFont(FONT_SMALL);

        // Bouton Améliorer
        JButton btn = bouton(maxLevel ? "MAX" : "Améliorer ⚒", maxLevel ? BTN_CLOSE : BTN_FORGE);
        btn.setEnabled(!maxLevel);
        btn.addActionListener(e -> {
            Monde.getInstance().ameliorerArme(arme.getNom());
            majOr();
            remplirArmes(); // Rafraîchit les cartes
            parent.miseAJour();
        });

        carte.add(icone, BorderLayout.NORTH);
        carte.add(info,  BorderLayout.CENTER);
        carte.add(btn,   BorderLayout.SOUTH);
        return carte;
    }

    // =========================================================================
    // UTILITAIRES
    // =========================================================================
    private void majOr() {
        Joueur j = Monde.getInstance().getJoueur();
        labelOr.setText("💰 Or : " + j.getOr() + " 🪙");
    }

    private JButton bouton(String texte, Color bg) {
        JButton b = new JButton(texte);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(5, 10, 5, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
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
        g2.setColor(new Color(70, 40, 10));
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("⚔", w / 2 - 10, h / 2 + 8);
        g2.dispose();
        return new ImageIcon(buf);
    }

    // WrapLayout inline
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container t) { return layoutSize(t, true); }
        @Override public Dimension minimumLayoutSize(Container t) {
            Dimension d = layoutSize(t, false); d.width -= (getHgap() + 1); return d;
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
                        if (rowW + d.width > maxW) { dim.width = Math.max(dim.width, rowW); dim.height += rowH + vgap; rowW = 0; rowH = 0; }
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
