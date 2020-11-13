package ru.deeplink.plugin

import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class CommandRenderer() : CommandView(), ListCellRenderer<CommandItemVo> {

    override fun getListCellRendererComponent(
            list: JList<out CommandItemVo>?,
            itemVo: CommandItemVo?,
            position: Int,
            isSelected: Boolean,
            hasFocus: Boolean): Component {
        setText(itemVo?.commandName ?: "")
        list?.let {
            setBackground(if (isSelected) { it.selectionBackground } else { it.background })
        }
        return this.rootPanel
    }
}