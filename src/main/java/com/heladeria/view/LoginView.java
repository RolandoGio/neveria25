package com.heladeria.view;

import com.heladeria.dao.UsuarioDAO;
import com.heladeria.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

public class LoginView extends JFrame {

    // ─────────────────────── Paleta de colores ───────────────────────
    private static final Color COLOR_GRADIENT_TOP    = new Color(236, 72, 153);
    private static final Color COLOR_GRADIENT_BOTTOM = new Color(56, 189, 248);
    private static final Color COLOR_TEXT_DARK       = new Color(33, 37, 41);
    private static final Color COLOR_INPUT_BORDER    = new Color(222, 226, 230);
    private static final Color COLOR_PRIMARY         = new Color(220, 53, 69);   // rojo hover X
    private static final Color COLOR_PRIMARY_DARK    = new Color(176, 42, 55);
    private static final Color COLOR_TEXT_LIGHT      = Color.WHITE;

    // Negro del marco/entorno
    private static final Color COLOR_FRAME_BG        = Color.BLACK;
    private static final Color TITLE_BTN_BG          = new Color(32,32,32);
    private static final Color TITLE_BTN_HOVER       = new Color(64,64,64);

    // Tamaño base de los campos
    private static final Dimension FIELD_SIZE = new Dimension(320, 40);

    // ─────────────────────── Componentes ─────────────────────────────
    private final JTextField     txtCodigo     = new JTextField();
    private final JPasswordField txtClave      = new JPasswordField();
    private final JButton        btnEntrar     = new JButton("Entrar");
    private final JLabel         lblMensaje    = new JLabel(" ", SwingConstants.CENTER);

    // Botón para mostrar/ocultar contraseña
    private final JButton        btnToggleClave = new JButton("Mostrar");

    // Para restaurar el echoChar por defecto
    private char defaultEchoChar;

    // Preferences node (donde se guardan últimas credenciales)
    private static final Preferences PREFS = Preferences.userRoot().node("com.heladeria.loginPrefs");

    // Contenedor raíz custom
    private JPanel root;           // fondo negro
    private TitleBar titleBar;     // barra de título custom

    // ─────────────────────── Constructor ─────────────────────────────
    public LoginView() {
        setUndecorated(true);
        setBackground(COLOR_FRAME_BG);
        setResizable(false); // ← no redimensionable

        setTitle("Sistema Heladería · Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        initEvents();

        defaultEchoChar = txtClave.getEchoChar();
        cargarPreferencias();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    // ─────────────────────── Interfaz ────────────────────────────────
    private void initUI() {
        root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_FRAME_BG);
        setContentPane(root);

        titleBar = new TitleBar(getTitle());
        root.add(titleBar, BorderLayout.NORTH);

        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());
        background.setOpaque(true);
        root.add(background, BorderLayout.CENTER);

        JPanel card = buildCard();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        background.add(card, gbc);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 30)),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        card.setMaximumSize(new Dimension(420, 430));

        JLabel formTitle = new JLabel("Iniciar sesión");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 26f));
        formTitle.setForeground(COLOR_TEXT_DARK);

        card.add(formTitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel lblCodigo = createFormLabel("Código");
        card.add(lblCodigo);
        styleTextField(txtCodigo);
        card.add(txtCodigo);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblClave = createFormLabel("Clave");
        card.add(lblClave);

        JPanel clavePanel = new JPanel(new BorderLayout());
        clavePanel.setOpaque(false);
        clavePanel.setPreferredSize(FIELD_SIZE);
        clavePanel.setMaximumSize(FIELD_SIZE);

        styleTextField(txtClave);
        txtClave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 0, COLOR_INPUT_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtClave.setPreferredSize(new Dimension(FIELD_SIZE.width - 90, FIELD_SIZE.height));

        btnToggleClave.setFocusable(false);
        btnToggleClave.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, COLOR_INPUT_BORDER));
        btnToggleClave.setBackground(Color.WHITE);
        btnToggleClave.setForeground(COLOR_TEXT_DARK);
        btnToggleClave.setFont(btnToggleClave.getFont().deriveFont(Font.PLAIN, 12f));
        btnToggleClave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToggleClave.setPreferredSize(new Dimension(90, FIELD_SIZE.height));

        clavePanel.add(txtClave, BorderLayout.CENTER);
        clavePanel.add(btnToggleClave, BorderLayout.EAST);
        card.add(clavePanel);

        card.add(Box.createRigidArea(new Dimension(0, 20)));
        stylePrimaryButton(btnEntrar);
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnEntrar);

        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setForeground(COLOR_PRIMARY_DARK);
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.BOLD, 14f));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblMensaje);

        return card;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(COLOR_TEXT_DARK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(FIELD_SIZE);
        field.setMaximumSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_INPUT_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(COLOR_TEXT_DARK);
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 16f));
        field.setCaretColor(COLOR_PRIMARY);
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ─────────────────────── Eventos ────────────────────────────────
    private void initEvents() {
        btnEntrar.addActionListener(e -> autenticar());
        txtClave.addActionListener(e -> autenticar());

        btnToggleClave.addActionListener(e -> {
            if ("Mostrar".equals(btnToggleClave.getText())) {
                txtClave.setEchoChar((char) 0);
                btnToggleClave.setText("Ocultar");
            } else {
                txtClave.setEchoChar(defaultEchoChar);
                btnToggleClave.setText("Mostrar");
            }
        });
    }

    // ─────────────────────── Autenticación ──────────────────────────
    private void autenticar() {
        String codigo = txtCodigo.getText().trim();
        String clave  = new String(txtClave.getPassword()).trim();

        if (codigo.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Ingresa código y clave.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = dao.buscarPorCodigo(codigo);

        if (u == null) {
            lblMensaje.setText("Código no encontrado o usuario inactivo.");
            return;
        }

        if (!u.getClaveAcceso().equals(clave)) {
            lblMensaje.setText("Clave incorrecta.");
            return;
        }

        guardarPreferencias(codigo);
        abrirDashboard(u);
    }

    // ─────────────────────── Navegación ─────────────────────────────
    private void abrirDashboard(Usuario u) {
        dispose();
        switch (u.getRol()) {
            case "SU" -> new DashboardSuperusuario(u).setVisible(true);
            case "AD" -> new DashboardAdministrador(u).setVisible(true);
            case "CJ" -> new DashboardCajero(u).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Rol desconocido: " + u.getRol());
        }
    }

    // ─────────────────────── Preferencias (Recordado) ───────────────
    private void guardarPreferencias(String codigo) {
        try {
            PREFS.put("lastCodigo", codigo);
        } catch (Exception ex) {
            System.err.println("No se pudieron guardar preferencias: " + ex.getMessage());
        }
    }

    private void cargarPreferencias() {
        try {
            String savedCodigo = PREFS.get("lastCodigo", "");
            if (!savedCodigo.isEmpty()) {
                txtCodigo.setText(savedCodigo);
            }
        } catch (Exception ex) {
            System.err.println("No se pudieron cargar preferencias: " + ex.getMessage());
        }
    }

    // ─────────────────────── Fondo degradado ────────────────────────
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, COLOR_GRADIENT_TOP,
                    0, getHeight(), COLOR_GRADIENT_BOTTOM
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    // ─────────────────────── Barra de título custom ─────────────────
    private class TitleBar extends JPanel {
        private Point dragOffset = null;
        private final JLabel titleLabel = new JLabel("NEVERIA"); // fijo

        TitleBar(String ignoredTitle) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 36));
            setBackground(COLOR_FRAME_BG);

            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttons.setOpaque(false);

            JButton btnMin   = makeTitleButton("—", "Minimizar", false);
            JButton btnClose = makeTitleButton("×", "Cerrar", true);

            btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));

            btnClose.addActionListener(e -> {
                int option = DarkConfirmDialog.showConfirm(
                        SwingUtilities.getWindowAncestor(LoginView.this),
                        "Confirmar salida",
                        "¿Seguro que quieres salir?"
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            });

            buttons.add(wrap(btnMin));
            buttons.add(wrap(btnClose));

            add(titleLabel, BorderLayout.WEST);
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
            titleLabel.addMouseListener(drag);
            titleLabel.addMouseMotionListener(drag);
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
                    b.setBackground(isClose ? COLOR_PRIMARY : TITLE_BTN_HOVER); // ← rojo para cerrar
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

    // ──────────────── Diálogo custom negro/blanco (undecorated) ────────────────
    private static class DarkConfirmDialog extends JDialog {
        private int result = JOptionPane.CLOSED_OPTION;

        DarkConfirmDialog(Window parent, String title, String message) {
            super(parent);
            setModal(true);
            setUndecorated(true);
            setResizable(false); // ← no redimensionable
            setBackground(Color.BLACK);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(Color.BLACK);
            setContentPane(root);

            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(Color.BLACK);
            titleBar.setPreferredSize(new Dimension(0, 36));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

            JButton btnClose = new JButton("×");
            btnClose.setFocusable(false);
            btnClose.setForeground(Color.WHITE);
            btnClose.setBackground(TITLE_BTN_BG);
            btnClose.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            btnClose.setPreferredSize(new Dimension(48, 36));
            btnClose.setOpaque(true);
            btnClose.setBorderPainted(false);
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btnClose.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){ btnClose.setBackground(COLOR_PRIMARY); } // ← rojo hover
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
            center.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));

            JLabel lblMsg = new JLabel(message);
            lblMsg.setForeground(Color.WHITE);
            lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblMsg.setBorder(BorderFactory.createEmptyBorder(4, 0, 12, 0));
            center.add(lblMsg);

            // Botones Yes/No con hover
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttons.setOpaque(false);

            JButton yes = createDialogButton("Sí");
            JButton no  = createDialogButton("No");


            yes.addActionListener(e -> { result = JOptionPane.YES_OPTION; dispose(); });
            no.addActionListener (e -> { result = JOptionPane.NO_OPTION;  dispose(); });

            buttons.add(yes);
            buttons.add(no);

            center.add(buttons);
            root.add(center, BorderLayout.CENTER);

            // Teclas
            getRootPane().setDefaultButton(yes); // Enter = Yes
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
