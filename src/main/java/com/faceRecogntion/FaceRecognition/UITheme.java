package com.faceRecogntion.FaceRecognition;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

/**
 * UITheme — Centralized design system for the Face Recognition App.
 * Aesthetic: Dark surveillance / cinematic law-enforcement terminal.
 */
public class UITheme {

    // ── Color Palette ──────────────────────────────────────────────────────────
    public static final Color BG_DEEP        = new Color(0x08, 0x0C, 0x10); // near-black navy
    public static final Color BG_PANEL       = new Color(0x0D, 0x13, 0x1A); // dark panel
    public static final Color BG_CARD        = new Color(0x12, 0x1A, 0x24); // card surface
    public static final Color BG_HOVER       = new Color(0x1A, 0x25, 0x33);

    public static final Color ACCENT_CYAN    = new Color(0x00, 0xD4, 0xFF); // electric cyan
    public static final Color ACCENT_CYAN2   = new Color(0x00, 0x9B, 0xBF); // deeper cyan
    public static final Color ACCENT_RED     = new Color(0xFF, 0x2D, 0x55); // alert red
    public static final Color ACCENT_GREEN   = new Color(0x00, 0xFF, 0x88); // confirm green
    public static final Color ACCENT_AMBER   = new Color(0xFF, 0xB8, 0x00); // warning amber

    public static final Color TEXT_PRIMARY   = new Color(0xE8, 0xF4, 0xFF);
    public static final Color TEXT_SECONDARY = new Color(0x7A, 0x9B, 0xBD);
    public static final Color TEXT_MUTED     = new Color(0x3A, 0x54, 0x6A);
    public static final Color BORDER_SUBTLE  = new Color(0x1E, 0x2E, 0x3E);
    public static final Color BORDER_ACCENT  = new Color(0x00, 0x6A, 0x8A);

    // ── Typography ────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Courier New", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Courier New", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Courier New", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Courier New", Font.PLAIN, 10);
    public static final Font FONT_BADGE   = new Font("Courier New", Font.BOLD,  10);
    public static final Font FONT_MONO    = new Font("Courier New", Font.PLAIN, 11);

    // ── Global LAF Setup ──────────────────────────────────────────────────────
    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",           BG_PANEL);
        UIManager.put("Frame.background",            BG_DEEP);
        UIManager.put("Label.foreground",            TEXT_PRIMARY);
        UIManager.put("Label.background",            BG_PANEL);
        UIManager.put("Button.background",           BG_CARD);
        UIManager.put("Button.foreground",           TEXT_PRIMARY);
        UIManager.put("Button.border",               BorderFactory.createLineBorder(BORDER_ACCENT));
        UIManager.put("TextField.background",        BG_CARD);
        UIManager.put("TextField.foreground",        TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",   ACCENT_CYAN);
        UIManager.put("TextField.border",            BorderFactory.createCompoundBorder(
                           BorderFactory.createLineBorder(BORDER_ACCENT),
                           BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        UIManager.put("OptionPane.background",       BG_PANEL);
        UIManager.put("OptionPane.messageForeground",TEXT_PRIMARY);
        UIManager.put("ScrollPane.background",       BG_DEEP);
        UIManager.put("ScrollBar.background",        BG_PANEL);
        UIManager.put("ScrollBar.thumb",             BORDER_ACCENT);
        UIManager.put("ScrollBar.track",             BG_CARD);
        UIManager.put("FileChooser.background",      BG_PANEL);
        UIManager.put("List.background",             BG_CARD);
        UIManager.put("List.foreground",             TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",    BORDER_ACCENT);
        UIManager.put("List.selectionForeground",    ACCENT_CYAN);
        UIManager.put("Table.background",            BG_CARD);
        UIManager.put("Table.foreground",            TEXT_PRIMARY);
        UIManager.put("Table.selectionBackground",   BORDER_ACCENT);
        UIManager.put("ComboBox.background",         BG_CARD);
        UIManager.put("ComboBox.foreground",         TEXT_PRIMARY);
        UIManager.put("ToolTip.background",          BG_CARD);
        UIManager.put("ToolTip.foreground",          ACCENT_CYAN);
        UIManager.put("ToolTip.border",              BorderFactory.createLineBorder(BORDER_ACCENT));
    }

    // ── Reusable Component Factories ──────────────────────────────────────────

    /** Creates a styled primary action button (cyan glow). */
    public static JButton primaryButton(String text) {
        return new GlowButton(text, ACCENT_CYAN, BG_DEEP);
    }

    /** Creates a styled danger button (red glow). */
    public static JButton dangerButton(String text) {
        return new GlowButton(text, ACCENT_RED, BG_DEEP);
    }

    /** Creates a styled success button (green glow). */
    public static JButton successButton(String text) {
        return new GlowButton(text, ACCENT_GREEN, BG_DEEP);
    }

    /** Creates a styled warning button (amber glow). */
    public static JButton warningButton(String text) {
        return new GlowButton(text, ACCENT_AMBER, BG_DEEP);
    }

    /** Creates a ghost/secondary button. */
    public static JButton ghostButton(String text) {
        return new GlowButton(text, TEXT_SECONDARY, BG_CARD);
    }

    /** Creates a section header label with a cyan underline. */
    public static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(FONT_BADGE);
        lbl.setForeground(ACCENT_CYAN);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /** Creates a status badge label. */
    public static JLabel statusBadge(String text, Color color) {
        JLabel lbl = new JLabel("● " + text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(color);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_BADGE);
        lbl.setForeground(color);
        lbl.setOpaque(false);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return lbl;
    }

    /** Creates a styled card panel with border. */
    public static JPanel cardPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return p;
    }

    /** Wraps a component in a titled card panel. */
    public static JPanel titledCard(String title, JComponent content) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.add(sectionLabel(title), BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /** Creates a horizontal separator line. */
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_SUBTLE);
        sep.setBackground(BG_PANEL);
        return sep;
    }

    /** Creates a camera feed placeholder label. */
    public static JLabel cameraPlaceholder(String message) {
        JLabel lbl = new JLabel(message, JLabel.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark bg
                g2.setColor(new Color(0x05, 0x08, 0x0C));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Grid lines (surveillance feel)
                g2.setColor(new Color(0x1A, 0x2A, 0x35));
                g2.setStroke(new BasicStroke(0.5f));
                for (int x = 0; x < getWidth(); x += 40) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g2.drawLine(0, y, getWidth(), y);
                // Corner brackets
                g2.setColor(ACCENT_CYAN);
                g2.setStroke(new BasicStroke(2f));
                int m = 20, l = 25;
                g2.drawLine(m, m, m+l, m);   g2.drawLine(m, m, m, m+l);
                g2.drawLine(getWidth()-m, m, getWidth()-m-l, m); g2.drawLine(getWidth()-m, m, getWidth()-m, m+l);
                g2.drawLine(m, getHeight()-m, m+l, getHeight()-m); g2.drawLine(m, getHeight()-m, m, getHeight()-m-l);
                g2.drawLine(getWidth()-m, getHeight()-m, getWidth()-m-l, getHeight()-m); g2.drawLine(getWidth()-m, getHeight()-m, getWidth()-m, getHeight()-m-l);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_MUTED);
        lbl.setOpaque(false);
        return lbl;
    }

    /** Creates a top header bar panel. */
    public static JPanel headerBar(String title, String subtitle) {
        JPanel bar = new JPanel(new BorderLayout(0, 2)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_DEEP, getWidth(), 0, new Color(0x0A, 0x16, 0x22)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom border line (cyan)
                g2.setColor(BORDER_ACCENT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel left = new JPanel(new BorderLayout(0, 2));
        left.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(TEXT_SECONDARY);

        left.add(titleLbl, BorderLayout.NORTH);
        left.add(subLbl, BorderLayout.SOUTH);

        // Live clock (right side)
        JLabel clock = new JLabel();
        clock.setFont(FONT_MONO);
        clock.setForeground(ACCENT_CYAN);
        updateClock(clock);
        Timer timer = new Timer(1000, e -> updateClock(clock));
        timer.start();

        JLabel recIndicator = new JLabel("◉ REC");
        recIndicator.setFont(FONT_BADGE);
        recIndicator.setForeground(ACCENT_RED);

        JPanel right = new JPanel(new BorderLayout(0, 2));
        right.setOpaque(false);
        right.add(clock, BorderLayout.NORTH);
        right.add(recIndicator, BorderLayout.SOUTH);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private static void updateClock(JLabel label) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        label.setText(String.format("%02d:%02d:%02d  %04d-%02d-%02d",
            now.getHour(), now.getMinute(), now.getSecond(),
            now.getYear(), now.getMonthValue(), now.getDayOfMonth()));
    }

    // ── Inner Classes ─────────────────────────────────────────────────────────

    /** A custom button with glow/hover paint. */
    public static class GlowButton extends JButton {
        private final Color glowColor;
        private final Color baseBg;
        private float hoverAlpha = 0f;
        private javax.swing.Timer hoverTimer;

        public GlowButton(String text, Color glowColor, Color baseBg) {
            super(text);
            this.glowColor = glowColor;
            this.baseBg = baseBg;
            setFont(FONT_HEADING);
            setForeground(glowColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { animateHover(true); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)  { animateHover(false); }
            });
        }

        private void animateHover(boolean in) {
            if (hoverTimer != null) hoverTimer.stop();
            hoverTimer = new javax.swing.Timer(16, null);
            hoverTimer.addActionListener(e -> {
                hoverAlpha = in ? Math.min(1f, hoverAlpha + 0.1f) : Math.max(0f, hoverAlpha - 0.1f);
                repaint();
                if ((in && hoverAlpha >= 1f) || (!in && hoverAlpha <= 0f)) hoverTimer.stop();
            });
            hoverTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Base fill
            g2.setColor(baseBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            // Hover glow fill
            if (hoverAlpha > 0f) {
                Color fill = new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                    (int)(40 * hoverAlpha));
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            // Border
            Color border = new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                (int)(120 + 135 * hoverAlpha));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);

            // Glow shadow when hovered
            if (hoverAlpha > 0.3f) {
                g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                    (int)(60 * hoverAlpha)));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(-1, -1, getWidth()+1, getHeight()+1, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A log/console text area styled like a terminal. */
    public static JTextArea consoleArea() {
        JTextArea area = new JTextArea();
        area.setBackground(new Color(0x03, 0x06, 0x09));
        area.setForeground(ACCENT_GREEN);
        area.setCaretColor(ACCENT_GREEN);
        area.setFont(FONT_MONO);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return area;
    }

    /** A progress/stat row: label + value pair. */
    public static JPanel statRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(FONT_BADGE);
        val.setForeground(valueColor);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }
}