package ru.deeplink.plugin;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

public class CommandView {
    public JPanel rootPanel;
    private JLabel label;
    private JLabel delete;

    CommandView() {
        delete.setOpaque(true);
        delete.setBackground(new JBColor(
                new Color(0, 0, 0, 10),
                new Color(0, 0, 0, 30))
        );
    }

    public void setText(@NotNull final String text) {
        label.setText(text);
    }

    public void setBackground(@NotNull final Color color) {
        rootPanel.setBackground(color);
    }
}
