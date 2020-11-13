package ru.deeplink.plugin;

import kotlin.Unit;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

public class DeeplinkView {
    private JPanel background;
    private JPanel commandsPanel;

    private JButton addDeeplinkButton;
    private JButton buttonOpen;
    private JButton buttonRun;
    private JButton buttonSave;
    private JButton buttonRunSingle;
    private JButton buttonRunSingleFromList;
    private JButton buttonClearCommands;

    private JList<Item> listView;
    private JTextField argumentValueTextField;
    private JTextField textFieldUserName;
    private JLabel argumentLabel;
    private JList<CommandItemVo> commandList;
    private JLabel applicationId;
    private JButton buttonConfig;
    private JLabel configLink;


    private static final String MSG_CHOOSE_COMMAND = "Выберите команду из списка";
    private final DeeplinkPresenter presenter;

    DeeplinkView(final DeeplinkPresenter presenter) {
        this.presenter = presenter;
        presenter.setView(this);

        Item selectedItem = listView.getSelectedValue();
        if (selectedItem != null) {
            presenter.updateSelectedItem(selectedItem);
        }

        listView.addListSelectionListener( e -> presenter.updateSelectedItem(listView.getSelectedValue()));
        addDeeplinkButton.addActionListener(e -> presenter.onAddClicked(argumentValueTextField.getText(), textFieldUserName.getText()));
        buttonRunSingle.addActionListener(e -> executeCommand());
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeCommand();
                }
            }
        });

        buttonRun.addActionListener(e -> presenter.onRunClicked());
        buttonOpen.addActionListener(e -> presenter.onOpenClicked());
        buttonConfig.addActionListener(e -> presenter.onOpenConfigClicked());
        buttonSave.addActionListener(e -> presenter.onSaveClicked());
        buttonClearCommands.addActionListener(e -> presenter.onClearCommandsClicked());
        buttonRunSingleFromList.addActionListener(e -> {
            System.out.println("buttonRunSingleFromList: " + selectedItem);
            CommandItemVo selectedValue = commandList.getSelectedValue();
            if (selectedValue != null) {
                presenter.onRunSingleFromListCliecked(selectedValue);
            } else {
                showMsg(MSG_CHOOSE_COMMAND);
            }
        });
        commandList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (listView.getWidth() - e.getX() < 32) {
                        presenter.removeCommandAt(listView.locationToIndex(e.getPoint()));
                    }
                } else if (e.getClickCount() == 2) {
                    int index = commandList.locationToIndex(e.getPoint());
                    CommandItemVo selectedValue =commandList.getModel().getElementAt(index);
                    presenter.onRunSingleFromListCliecked(selectedValue);
                }
            }
        });

        configLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openWebPage(URI.create("https://ya.ru"));
            }
        });
    }

    private void openWebPage(URI uri) {
        com.intellij.ide.BrowserUtil.browse(uri);
    }

    private void executeCommand() {
        String argument = argumentValueTextField.getText();
        String inputName = textFieldUserName.getText();
        presenter.onRunSingleClicked(argument, inputName);
        presenter.onAddClicked(argument, inputName);
    }

    public void showItems(@NotNull List<Item> items) {
        listView.setListData(items.toArray(new Item[0]));
    }

    public void updateCommandView(@NotNull CommandViewVo vo) {
        argumentValueTextField.setText("");
        argumentValueTextField.setEnabled(vo.isArgumentLabelEnabled());
        argumentLabel.setText(vo.getArgumentLabelText());
        textFieldUserName.setText(vo.getTextFieldUserName());
    }

    public void showMsg(@NotNull String text) {
        JOptionPane.showMessageDialog(background, text);
    }

    public void configureCommandList(@NotNull ListModel<CommandItemVo> itemsModel) {
        commandList.setModel(itemsModel);
        commandList.setCellRenderer(new CommandRenderer());
    }

    public void showAppId(@NotNull String id) {
        applicationId.setText(id);
    }

    JPanel getBackground() {
        return background;
    }
}
