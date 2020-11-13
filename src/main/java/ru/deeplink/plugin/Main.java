package ru.deeplink.plugin;

import java.awt.Dimension;

import javax.swing.JFrame;


public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("DeeplinkApp");
        frame.setContentPane(new DeeplinkView(new DeeplinkPresenter(new SimpleCommandExecutor(), null)).getBackground());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 700));
        frame.pack();
        frame.setVisible(true);
    }
}
