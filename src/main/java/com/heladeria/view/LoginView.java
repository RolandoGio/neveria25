package com.heladeria.view;

import com.heladeria.dao.UsuarioDAO;
import com.heladeria.model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión con fondo degradado y tarjeta centrada.
 * Contiene únicamente los campos de acceso.
 */
public class LoginView extends JFrame {

    // ─────────────────────── Paleta de colores ───────────────────────
    private static final Color COLOR_GRADIENT_TOP    = new Color(236, 72, 153);
    private static final Color COLOR_GRADIENT_BOTTOM = new Color(56, 189, 248);
    private static final Color COLOR_TEXT_DARK       = new Color(33, 37, 41);
    private static final Color COLOR_INPUT_BORDER    = new Color(222, 226, 230);
    private static final Color COLOR_PRIMARY         = new Color(220, 53, 69);
    private static final Color COLOR_PRIMARY_DARK    = new Color(176, 42, 55);
    private static final Color COLOR_TEXT_LIGHT      = Color.WHITE;

    // Tamaño fijo de los campos para que no se peguen a los bordes
    private static final Dimension FIELD_SIZE = new Dimension(320, 40);

    // ─────────────────────── Componentes ─────────────────────────────
    private final JTextField     txtCodigo     = new JTextField();
    private final JPasswordField txtClave      = new JPasswordField();
    private final JCheckBox      chkRecordarme = new JCheckBox("Recordarme");
    private final JButton        btnEntrar     = new JButton("Entrar");
    private final JLabel         lblMensaje    = new JLabel(" ", SwingConstants.CENTER);

    // ─────────────────────── Constructor ─────────────────────────────
    public LoginView() {
        setTitle("Sistema Heladería · Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        initEvents();
    }

    // ─────────────────────── Interfaz ────────────────────────────────
    private void initUI() {
        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);

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
        card.setMaximumSize(new Dimension(420, 400));

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
        styleTextField(txtClave);
        card.add(txtClave);

        chkRecordarme.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkRecordarme.setForeground(COLOR_TEXT_DARK);
        chkRecordarme.setOpaque(false);
        chkRecordarme.setFont(chkRecordarme.getFont().deriveFont(Font.PLAIN, 14f));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(chkRecordarme);

        stylePrimaryButton(btnEntrar);
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
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
        // Centrar el propio componente dentro del BoxLayout
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        // Tamaño fijo y centrado dentro del BoxLayout
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

    // ─────────────────────── Fondo degradado ────────────────────────
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, COLOR_GRADIENT_TOP,
                    0, getHeight(), COLOR_GRADIENT_BOTTOM
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
