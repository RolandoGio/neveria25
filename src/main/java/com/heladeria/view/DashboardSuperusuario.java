package com.heladeria.view;

import com.heladeria.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * Dashboard Superusuario (SOLO UI)
 * - Pantalla completa, tema negro.
 * - Ventana undecorated con barra custom (NEVERIA).
 * - Cierre con confirmación en diálogo custom negro/blanco.
 */
public class DashboardSuperusuario extends JFrame {

    // ───────── Paleta (Dark / Negro) ─────────
    private static final Color BG_BLACK        = Color.BLACK;               // fondo absoluto
    private static final Color BG_DARK         = new Color(12, 14, 18);     // fondo app
    private static final Color BG_PANEL        = new Color(18, 21, 26);     // paneles
    private static final Color BG_SOFT         = new Color(26, 30, 36);     // panel suave
    private static final Color TEXT_PRIMARY    = new Color(238, 242, 246);
    private static final Color TEXT_SECONDARY  = new Color(160, 167, 175);
    private static final Color BORDER_SOFT     = new Color(50, 56, 64);
    private static final Color ACCENT          = new Color(216, 70, 90);    // acento
    private static final Color OK              = new Color(80, 200, 120);   // verde
    private static final Color WARN            = new Color(240, 200, 90);   // amarillo
    private static final Color DANGER          = new Color(235, 87, 87);    // rojo

    // Título/ventana
    private static final Color TITLE_BTN_BG    = new Color(32,32,32);
    private static final Color TITLE_BTN_HOVER = new Color(64,64,64);
    private static final Color TITLE_CLOSE_HOVER = new Color(220,53,69);     // rojo hover X
    private static final int RADIUS = 16;
    private static final Dimension SIDE_W = new Dimension(240, 0);

    // Barra de título
    private TitleBar titleBar;

    public DashboardSuperusuario(Usuario u) {
        // Ventana custom (sin decoración nativa)
        setUndecorated(true);
        setBackground(BG_BLACK);
        setResizable(false); // no redimensionable

        setTitle("Superusuario · " + u.getNombreCompleto());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ventana negra + pantalla completa siempre
        getContentPane().setBackground(BG_BLACK);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // abre maximizada
        setMinimumSize(new Dimension(1100, 700));

        // layout raíz
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        // Barra de título custom
        titleBar = new TitleBar();
        root.add(titleBar, BorderLayout.NORTH);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(u),   BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    // ───────────────── Sidebar ─────────────────
    private JComponent buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(BG_PANEL);
        side.setPreferredSize(SIDE_W);
        side.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logo = new JLabel("Heladería · SU");
        logo.setForeground(TEXT_PRIMARY);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 18f));
        side.add(logo, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(24, 0, 0, 0));

        String[] items = {
                "Inicio",
                "Ventas",
                "Inventario",
                "Descargas técnicas",
                "Pedidos",
                "Usuarios",
                "Auditoría",
                "Mantenimiento",
                "Backups",
                "Reportes",
                "Configuración"
        };
        ButtonGroup group = new ButtonGroup();
        for (String it : items) {
            JToggleButton btn = makeNavButton(it);
            if (it.equals("Inicio")) btn.setSelected(true);
            group.add(btn);
            nav.add(btn);
            nav.add(Box.createVerticalStrut(8));
        }
        side.add(nav, BorderLayout.CENTER);

        RoundedPanel about = new RoundedPanel(BG_SOFT);
        about.setLayout(new BoxLayout(about, BoxLayout.Y_AXIS));
        about.setBorder(new EmptyBorder(14, 14, 14, 14));
        JLabel l1 = small("By Heladería Inc.");
        JLabel l2 = small("Soporte Técnico");
        l1.setForeground(TEXT_SECONDARY);
        l2.setForeground(TEXT_SECONDARY);
        about.add(l1); about.add(l2);
        side.add(about, BorderLayout.SOUTH);

        return side;
    }

    private JToggleButton makeNavButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(BG_PANEL);
        btn.setForeground(TEXT_SECONDARY);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 14f));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SOFT),
                new EmptyBorder(10, 14, 10, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(BG_SOFT);
                btn.setForeground(TEXT_PRIMARY);
            } else {
                btn.setBackground(BG_PANEL);
                btn.setForeground(TEXT_SECONDARY);
            }
        });
        return btn;
    }

    // ───────────────── Main ─────────────────
    private JComponent buildMain(Usuario u) {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        main.add(buildTopbar(u), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        // Fila 1: KPIs (ocupa 2 columnas)
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.weighty = 0;
        content.add(buildKpiRow(), c);

        // Fila 2: Ventas 7 días (izq) + Donut stock (der)
        c.gridwidth = 1; c.weighty = 0.6;
        c.gridx = 0; c.gridy = 1;
        content.add(buildSales7Days(), c);
        c.gridx = 1; c.gridy = 1;
        content.add(buildStockDonut(), c);

        // Fila 3: Consumo por cubeta (izq) + Bitácora (der)
        c.gridx = 0; c.gridy = 2; c.weighty = 0.7;
        content.add(buildCubetaLine(), c);
        c.gridx = 1; c.gridy = 2;
        content.add(buildLogPanel(), c);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    // ─────────── Topbar ───────────
    private JComponent buildTopbar(Usuario u) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 20, 0, 20));

        JLabel title = new JLabel("Inicio · Resumen general");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        bar.add(title, BorderLayout.WEST);

        // derecha: perfil
        RoundedPanel profile = new RoundedPanel(BG_PANEL);
        profile.setBorder(new EmptyBorder(8, 14, 8, 14));
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        JLabel p1 = label(u.getNombreCompleto());
        JLabel p2 = small("Superusuario");
        p2.setForeground(TEXT_SECONDARY);
        profile.add(p1); profile.add(p2);
        bar.add(profile, BorderLayout.EAST);

        return bar;
    }

    // ─────────── KPIs ───────────
    private JComponent buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 6, 12, 12));
        row.setOpaque(false);
        row.add(kpi("$ 1,250.00", "Ventas del día", ACCENT));
        row.add(kpi("$ 32,400.00", "Ventas del mes", new Color(90, 160, 255)));
        row.add(kpi("4", "Pedidos pendientes", new Color(180, 140, 255)));
        row.add(kpi("7", "Stock bajo", WARN));
        row.add(kpi("2", "Cubetas por cerrar", new Color(255, 140, 120)));
        row.add(kpi("3", "Alertas", DANGER));
        return row;
    }

    private JComponent kpi(String value, String label, Color accent) {
        RoundedPanel p = new RoundedPanel(BG_SOFT);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel v = label(value);
        v.setForeground(accent);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 18f));
        JLabel s = small(label);
        s.setForeground(TEXT_SECONDARY);

        p.add(v);
        p.add(Box.createVerticalStrut(6));
        p.add(s);
        return p;
    }

    // ─────────── Ventas últimos 7 días (barras) ───────────
    private JComponent buildSales7Days() {
        RoundedPanel p = titled("Ventas últimos 7 días");
        double[] values = {420, 680, 520, 910, 760, 630, 540}; // mock
        String[] labels = {"Dom","Lun","Mar","Mié","Jue","Vie","Sáb"};
        BarChart bars = new BarChart(labels, values, 3, ACCENT);
        p.add(bars);
        return p;
    }

    // ─────────── Donut stock estado ───────────
    private JComponent buildStockDonut() {
        RoundedPanel p = titled("Estado de stock (OK/Bajo/Agotado)");
        // mock: ok 72, bajo 18, agotado 10
        DonutChart donut = new DonutChart(
                new double[]{72, 18, 10},
                new Color[]{OK, WARN, DANGER},
                new String[]{"OK", "Bajo", "Agotado"}
        );
        donut.setPreferredSize(new Dimension(380, 280));
        p.add(donut);
        return p;
    }

    // ─────────── Consumo por cubeta (línea) ───────────
    private JComponent buildCubetaLine() {
        RoundedPanel p = titled("Consumo promedio por cubeta (objetivo 150)");
        double[] valores = {130, 142, 155, 148, 151, 146, 152}; // mock
        String[] labels = {"Lote A","Lote B","Lote C","Lote D","Lote E","Lote F","Lote G"};
        LineChart lc = new LineChart(labels, valores, 150, ACCENT);
        p.add(lc);
        return p;
    }

    // ─────────── Bitácora reciente ───────────
    private JComponent buildLogPanel() {
        RoundedPanel p = titled("Bitácora reciente");
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        addLog(list, "CJ0004 cerró cubeta Fresa antes de 30 días", "18 Oct 2023 · 10:22", WARN);
        addLog(list, "AD0002 validó pedido a Proveedor Lácteos", "18 Oct 2023 · 09:10", OK);
        addLog(list, "SU reabrió cubeta Chocolate por error de cierre", "17 Oct 2023 · 18:40", ACCENT);
        addLog(list, "Stock bajo: Conos (quedan 8)", "17 Oct 2023 · 16:05", WARN);
        addLog(list, "Stock agotado: Paletas de mora", "17 Oct 2023 · 11:50", DANGER);

        p.add(list);
        return p;
    }

    private void addLog(JPanel parent, String text, String when, Color tag) {
        RoundedPanel item = new RoundedPanel(BG_PANEL);
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel left = label(text);
        JLabel right = small(when); right.setForeground(TEXT_SECONDARY);

        RoundedDot dot = new RoundedDot(tag);
        dot.setPreferredSize(new Dimension(12, 12));

        JPanel west = new JPanel();
        west.setOpaque(false);
        west.setLayout(new BoxLayout(west, BoxLayout.X_AXIS));
        west.add(dot);
        west.add(Box.createHorizontalStrut(8));
        west.add(left);

        item.add(west, BorderLayout.WEST);
        item.add(right, BorderLayout.EAST);

        parent.add(item);
        parent.add(Box.createVerticalStrut(8));
    }

    // ─────────── Helpers generales ───────────
    private RoundedPanel titled(String title) {
        RoundedPanel p = new RoundedPanel(BG_SOFT);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel t = label(title);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        return p;
    }
    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(TEXT_PRIMARY);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
        return l;
    }
    private JLabel small(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(TEXT_PRIMARY);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 12f));
        return l;
    }

    // ─────────── Componentes personalizados ───────────
    private static class RoundedPanel extends JPanel {
        private final Color bg;
        public RoundedPanel(Color bg) { this.bg = bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
            g2.setColor(new Color(0,0,0,40));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, RADIUS, RADIUS);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public Insets getInsets() { return new Insets(6,6,6,6); }
    }

    private static class RoundedDot extends JComponent {
        private final Color color;
        public RoundedDot(Color c) { this.color = c; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class BarChart extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final int highlightIndex;
        private final Color highlightColor;
        public BarChart(String[] labels, double[] values, int highlightIndex, Color highlightColor) {
            this.labels = Arrays.copyOf(labels, labels.length);
            this.values = Arrays.copyOf(values, values.length);
            this.highlightIndex = highlightIndex;
            this.highlightColor = highlightColor;
            setOpaque(false);
            setPreferredSize(new Dimension(480, 280));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int pad = 36, base = h - 40;
            double max = Arrays.stream(values).max().orElse(1);

            int barW = Math.max(18, (w - pad*2) / (values.length*2));
            int gap  = barW;

            // eje
            g2.setColor(BORDER_SOFT);
            g2.drawLine(pad, base, w-pad, base);

            for (int i=0; i<values.length; i++) {
                int x = pad + i*(barW+gap) + gap/2;
                int barH = (int) Math.round((values[i]/max) * (h - 80));
                int y = base - barH;

                g2.setColor(i==highlightIndex ? highlightColor : BG_PANEL.brighter());
                g2.fillRoundRect(x, y, barW, barH, 10, 10);

                g2.setColor(TEXT_SECONDARY);
                String lab = labels[i];
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(lab);
                g2.drawString(lab, x + (barW - tw)/2, base + 18);

                String val = String.format("$%.0f", values[i]);
                int vtw = fm.stringWidth(val);
                g2.setColor(TEXT_PRIMARY);
                g2.drawString(val, x + (barW - vtw)/2, y - 6);
            }
            g2.dispose();
        }
    }

    private static class DonutChart extends JPanel {
        private final double[] values;
        private final Color[] colors;
        private final String[] labels;
        public DonutChart(double[] values, Color[] colors, String[] labels) {
            this.values = values; this.colors = colors; this.labels = labels;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth()-size)/2;
            int y = (getHeight()-size)/2;

            double sum = 0; for (double v: values) sum += v;
            double angle = 0;
            for (int i=0;i<values.length;i++) {
                g2.setColor(colors[i]);
                double sweep = values[i] / sum * 360.0;
                g2.fillArc(x, y, size, size, (int)Math.round(angle), (int)Math.round(sweep));
                angle += sweep;
            }

            // agujero
            int hole = (int) (size * 0.58);
            g2.setColor(BG_SOFT);
            g2.fillOval(x + (size-hole)/2, y + (size-hole)/2, hole, hole);

            // leyenda
            int lx = 16, ly = 16;
            for (int i=0;i<labels.length;i++) {
                g2.setColor(colors[i]); g2.fillRect(lx, ly + i*22, 14, 14);
                g2.setColor(TEXT_PRIMARY);
                g2.drawString(labels[i] + " (" + (int)values[i] + "%)", lx + 20, ly + 12 + i*22);
            }
            g2.dispose();
        }
    }

    private static class LineChart extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final double target;
        private final Color accent;
        public LineChart(String[] labels, double[] values, double target, Color accent) {
            this.labels = labels; this.values = values; this.target = target; this.accent = accent;
            setOpaque(false);
            setPreferredSize(new Dimension(480, 280));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pad = 40;
            double max = Math.max(Arrays.stream(values).max().orElse(1), target);

            // ejes
            g2.setColor(BORDER_SOFT);
            g2.drawLine(pad, h - pad, w - pad, h - pad);
            g2.drawLine(pad, pad, pad, h - pad);

            // línea objetivo
            int yTarget = (int) (h - pad - (target / max) * (h - pad*2));
            g2.setColor(WARN);
            g2.drawLine(pad, yTarget, w - pad, yTarget);
            g2.drawString("Objetivo 150", w - pad - 90, yTarget - 6);

            // línea de valores
            g2.setColor(ACCENT);
            int n = values.length;
            int dx = (w - pad*2) / Math.max(1, n - 1);
            int prevX = pad, prevY = h - pad - (int)((values[0]/max) * (h - pad*2));
            for (int i=1;i<n;i++) {
                int x = pad + i*dx;
                int y = h - pad - (int)((values[i]/max) * (h - pad*2));
                g2.drawLine(prevX, prevY, x, y);
                prevX = x; prevY = y;
            }

            // puntos + etiquetas
            g2.setColor(TEXT_PRIMARY);
            for (int i=0;i<n;i++) {
                int x = pad + i*dx;
                int y = h - pad - (int)((values[i]/max) * (h - pad*2));
                g2.fillOval(x-3, y-3, 6, 6);
                String lab = labels[i];
                g2.setColor(TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lab, x - fm.stringWidth(lab)/2, h - pad + 16);
                g2.setColor(TEXT_PRIMARY);
                String vv = String.valueOf((int)Math.round(values[i]));
                g2.drawString(vv, x - fm.stringWidth(vv)/2, y - 8);
            }
            g2.dispose();
        }
    }

    // ───────────────────── Barra de título custom ─────────────────────
    private class TitleBar extends JPanel {
        private Point dragOffset = null;

        TitleBar() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 36));
            setBackground(BG_BLACK);

            JLabel lbl = new JLabel("NEVERIA");
            lbl.setForeground(Color.WHITE);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
            lbl.setBorder(new EmptyBorder(0, 12, 0, 0));

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttons.setOpaque(false);

            JButton btnMin   = makeTitleButton("—", "Minimizar", false);
            JButton btnClose = makeTitleButton("×", "Cerrar", true);

            btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
            btnClose.addActionListener(e -> {
                int option = DarkConfirmDialog.showConfirm(
                        SwingUtilities.getWindowAncestor(DashboardSuperusuario.this),
                        "Confirmar salida",
                        "¿Seguro que quieres salir?"
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            });

            buttons.add(wrap(btnMin));
            buttons.add(wrap(btnClose));

            add(lbl, BorderLayout.WEST);
            add(buttons, BorderLayout.EAST);

            MouseAdapter drag = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { dragOffset = e.getPoint(); }
                @Override public void mouseDragged(MouseEvent e) {
                    if (dragOffset != null && (getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                        Point p = e.getLocationOnScreen();
                        setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
                    }
                }
                @Override public void mouseReleased(MouseEvent e) { dragOffset = null; }
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
                            setExtendedState(JFrame.NORMAL);
                        } else {
                            setExtendedState(JFrame.MAXIMIZED_BOTH);
                        }
                    }
                }
            };
            addMouseListener(drag);
            addMouseMotionListener(drag);
            lbl.addMouseListener(drag);
            lbl.addMouseMotionListener(drag);
        }

        private JButton makeTitleButton(String text, String tooltip, boolean isClose) {
            JButton b = new JButton(text);
            b.setFocusable(false);
            b.setToolTipText(tooltip);
            b.setForeground(Color.WHITE);
            b.setBackground(TITLE_BTN_BG);
            b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            b.setPreferredSize(new Dimension(48, 36));
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    b.setBackground(isClose ? TITLE_CLOSE_HOVER : TITLE_BTN_HOVER);
                }
                @Override public void mouseExited(MouseEvent e)  { b.setBackground(TITLE_BTN_BG); }
            });
            return b;
        }
        private JPanel wrap(JButton b) {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            p.add(b, BorderLayout.CENTER);
            return p;
        }
    }

    // ─────────────── Diálogo custom negro/blanco (undecorated) ───────────────
    private static class DarkConfirmDialog extends JDialog {
        private int result = JOptionPane.CLOSED_OPTION;

        DarkConfirmDialog(Window parent, String title, String message) {
            super(parent);
            setModal(true);
            setUndecorated(true);
            setResizable(false);
            setBackground(Color.BLACK);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(Color.BLACK);
            setContentPane(root);

            // Barra superior
            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(Color.BLACK);
            titleBar.setPreferredSize(new Dimension(0, 36));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
            lblTitle.setBorder(new EmptyBorder(0, 12, 0, 0));

            JButton btnClose = new JButton("×");
            btnClose.setFocusable(false);
            btnClose.setForeground(Color.WHITE);
            btnClose.setBackground(TITLE_BTN_BG);
            btnClose.setBorder(new EmptyBorder(0, 16, 0, 16));
            btnClose.setPreferredSize(new Dimension(48, 36));
            btnClose.setOpaque(true);
            btnClose.setBorderPainted(false);
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnClose.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){ btnClose.setBackground(TITLE_CLOSE_HOVER); }
                @Override public void mouseExited (MouseEvent e){ btnClose.setBackground(TITLE_BTN_BG); }
            });
            // Cerrar = NO
            btnClose.addActionListener(e -> { result = JOptionPane.NO_OPTION; dispose(); });

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setOpaque(false);
            right.add(btnClose);

            titleBar.add(lblTitle, BorderLayout.WEST);
            titleBar.add(right, BorderLayout.EAST);
            root.add(titleBar, BorderLayout.NORTH);

            // Arrastre
            MouseAdapter drag = new MouseAdapter() {
                Point offset;
                @Override public void mousePressed(MouseEvent e){ offset = e.getPoint(); }
                @Override public void mouseDragged(MouseEvent e){
                    if (offset != null) {
                        Point p = e.getLocationOnScreen();
                        setLocation(p.x - offset.x, p.y - offset.y);
                    }
                }
            };
            titleBar.addMouseListener(drag);
            titleBar.addMouseMotionListener(drag);
            lblTitle.addMouseListener(drag);
            lblTitle.addMouseMotionListener(drag);

            // Contenido
            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBackground(Color.BLACK);
            center.setBorder(new EmptyBorder(18, 22, 14, 22));

            JLabel lblMsg = new JLabel(message);
            lblMsg.setForeground(Color.WHITE);
            lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblMsg.setBorder(new EmptyBorder(4, 0, 12, 0));
            center.add(lblMsg);

            // Botones Sí/No con hover
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttons.setOpaque(false);

            JButton yes = createDialogButton("Sí");
            JButton no  = createDialogButton("No");
            yes.setMnemonic('S');
            no.setMnemonic('N');

            yes.addActionListener(e -> { result = JOptionPane.YES_OPTION; dispose(); });
            no.addActionListener (e -> { result = JOptionPane.NO_OPTION;  dispose(); });

            buttons.add(yes);
            buttons.add(no);

            center.add(buttons);
            root.add(center, BorderLayout.CENTER);

            // Teclas
            getRootPane().setDefaultButton(yes); // Enter = Sí
            getRootPane().registerKeyboardAction(
                    e -> { result = JOptionPane.NO_OPTION; dispose(); }, // Esc = No
                    KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            pack();
            setSize(new Dimension(360, getHeight()));
            setLocationRelativeTo(parent);
        }

        private JButton createDialogButton(String text) {
            JButton b = new JButton(text);
            b.setBackground(Color.BLACK);
            b.setForeground(Color.WHITE);
            b.setFocusable(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
            b.setPreferredSize(new Dimension(84, 34));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    b.setBackground(new Color(24,24,24));
                    b.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // hover
                }
                @Override public void mouseExited(MouseEvent e) {
                    b.setBackground(Color.BLACK);
                    b.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
                }
            });
            return b;
        }

        static int showConfirm(Window parent, String title, String message) {
            DarkConfirmDialog d = new DarkConfirmDialog(parent, title, message);
            d.setVisible(true); // modal
            return d.result;
        }
    }
}
