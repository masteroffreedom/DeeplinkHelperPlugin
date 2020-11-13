package ru.deeplink.plugin

interface Item

class CommandItem(
        val command: String,
        val name: String
) : Item {
    override fun toString(): String {
        return name
    }
}

class DeeplinkItem(
        val deeplink: String,
        val hasArgument: Boolean,
        val argumentName: String?,
        val name: String
) : Item {
    override fun toString(): String {
        return name
    }
}

data class CommandItemVo(val command: String, val commandName: String?) {
    override fun toString(): String {
        return commandName?.let { it } ?: command
    }
}


data class CommandViewVo(
        val argumentLabelText: String,
        val isArgumentLabelEnabled: Boolean,
        val textFieldUserName: String
)