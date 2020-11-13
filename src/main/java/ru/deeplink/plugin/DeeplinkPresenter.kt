package ru.deeplink.plugin

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.IOUtils
import ru.deeplink.plugin.dto.ConfigDto
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.DefaultListModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


class DeeplinkPresenter(
        private val commandExecutor: CommandExecutor,
        private val configStoreService: PluginConfigStoreService?
) {

    private lateinit var deeplinkView: DeeplinkView
    private var selectedItem: Item? = null
    private val commands = DefaultListModel<CommandItemVo>()

    private var config: ConfigDto = loadConfig()
    private val applicationId: String
            get() = getAppId(config)

    fun setView(deeplinkView: DeeplinkView) {
        this.deeplinkView = deeplinkView
        initView()
    }

    private fun initView() {
        deeplinkView.showAppId(applicationId)
        deeplinkView.showItems(mapDeeplinkItems(config) + mapCommandItems(config))
        deeplinkView.configureCommandList(commands)
    }

    private fun loadConfig(): ConfigDto {
        val configContent = configStoreService?.stateValue ?: loadDefaultConfig()
        return parseConfig(configContent)
    }

    private fun loadDefaultConfig(): String {
        val configInputStream = this.javaClass.classLoader.getResourceAsStream("json/config.json")
        return  IOUtils.toString(configInputStream, StandardCharsets.UTF_8)
    }

    private fun updateConfigInStore(configContent: String) {
        configStoreService?.stateValue = configContent
    }

    private fun parseConfig(configContent: String): ConfigDto {
        updateConfigInStore(configContent)
        return Gson().fromJson(configContent, ConfigDto::class.java)
    }

    private fun getAppId(configDto: ConfigDto): String {
        val appId = configDto.applicationId
        return if (appId.isNullOrBlank()) {
            deeplinkView.showMsg(MSG_APP_ID_ERROR)
             ""
        } else {
            appId!!
        }
    }

    private fun mapDeeplinkItems(configDto: ConfigDto): List<DeeplinkItem> {
        return configDto.deeplinks
                ?.mapNotNull {
                    DeeplinkItem(
                            deeplink = it.deeplink ?: return@mapNotNull null,
                            hasArgument = it.hasArgument ?: return@mapNotNull null,
                            argumentName = it.argumentName,
                            name = it.name ?: return@mapNotNull null
                    )
                } ?: emptyList()
    }

    private fun mapCommandItems(configDto: ConfigDto): List<CommandItem> {
        return configDto.commands
                ?.mapNotNull {
                    CommandItem(
                            command = it.command ?: return@mapNotNull null,
                            name = it.name ?: return@mapNotNull null
                    )
                } ?: emptyList()
    }

    fun onRunClicked() {
        for (command in commands.elements()) {
            runCommand(command.command)
        }
    }

    fun onOpenClicked() {
        showOpenDialog { selectedFile ->
            commands.clear()
            loadCommandsFromFile(selectedFile)
        }
    }

    fun onOpenConfigClicked() {
        showOpenDialog { selectedFile ->
            val content = readFileAsString(selectedFile)
            if (content.isNotEmpty()) {
                config = parseConfig(content)
                initView()
            }
        }
    }

    fun onSaveClicked() {
        showSaveDialog()
    }

    fun onClearCommandsClicked() {
        commands.clear()
    }

    fun removeCommandAt(position: Int) {
        commands.remove(position)
    }

    fun onRunSingleFromListCliecked(selectedValue: CommandItemVo) {
        runCommand(selectedValue.command)
    }

    fun onAddClicked(argument: String, userCommandName: String) {
        selectedItem?.let {
            try {
                val command = formatCommand(it, argument, userCommandName)
                commands.addElement(command)
            } catch (e: ArgumentNotFoundException) {
                deeplinkView.showMsg(MSG_NEED_ARGUMENT)
            }
        }
    }

    fun onRunSingleClicked(argument: String, userCommandName: String) {
        selectedItem?.let {
            try {
                val command = formatCommand(it, argument, userCommandName)
                runCommand(command.command)
            } catch (e: ArgumentNotFoundException) {
                deeplinkView.showMsg(MSG_NEED_ARGUMENT)
            }
        }
    }

    fun updateSelectedItem(item: Item) {
        selectedItem = item
        println("updateSelectedItem: $selectedItem")

        when (item) {
            is DeeplinkItem -> {
                deeplinkView.updateCommandView(
                        CommandViewVo(
                                argumentLabelText = if (item.hasArgument) item.argumentName ?: "" else "",
                                isArgumentLabelEnabled = item.hasArgument,
                                textFieldUserName = ""
                        )
                )
            }
            is CommandItem -> {
                deeplinkView.updateCommandView(
                        CommandViewVo(
                                argumentLabelText = "",
                                isArgumentLabelEnabled = false,
                                textFieldUserName = ""
                        )
                )
            }
        }
    }

    private fun showOpenDialog(onFileOpened: (File) -> Unit) {
        val chooser = JFileChooser()
        val result = chooser.showDialog(null, "Открыть файл")
        if (result == JFileChooser.APPROVE_OPTION) {
            onFileOpened(chooser.selectedFile)
        }
    }

    private fun showSaveDialog() {
        val chooser = JFileChooser()
        chooser.fileFilter = FileNameExtensionFilter("*.txt", "*.*")
        val result = chooser.showSaveDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            saveCommandsToFile(chooser.selectedFile)
        }
    }

    private fun readFileAsString(file: File): String {
        try {
            val stringBuilder = StringBuilder()
            val lines = Files.lines(Paths.get(file.absolutePath), StandardCharsets.UTF_8)
            lines.forEach {
                it?.let {
                    stringBuilder.append(it).append(System.lineSeparator())
                }
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    private fun loadCommandsFromFile(file: File) {
        val lines = readFileAsString(file)
        if (lines.isNotEmpty()) {
            val items: List<CommandItemVo> = Gson().fromJson(lines, object : TypeToken<ArrayList<CommandItemVo>>() {}.type)
            items.forEach { commands.addElement(it) }
        }
    }

    private fun saveCommandsToFile(file: File) {
        try {
            val json = Gson().toJson(commands)
            val fileWriter = FileWriter(file)
            fileWriter.write(json)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun runCommand(command: String) {
        commandExecutor.runCommand(command) { deeplinkView.showMsg(MSG_ADB_ERROR) }
    }

    private fun formatCommand(item: Item, argument: String, commandUserName: String): CommandItemVo {
        return when (item) {
            is DeeplinkItem -> {
                val deeplink = if (item.hasArgument) {
                    if (argument.isNotEmpty()) {
                        item.deeplink + argument
                    } else {
                        throw ArgumentNotFoundException()
                    }
                } else {
                    item.deeplink
                }
                return CommandItemVo(
                        "am start -W -a android.intent.action.VIEW -d $deeplink $applicationId",
                        if (commandUserName.isNotEmpty()) commandUserName else "${item.name} $argument"
                )
            }
            is CommandItem -> {
                CommandItemVo(item.command, if (commandUserName.isNotEmpty()) commandUserName else item.name)
            }
            else -> throw IllegalArgumentException("item is unsupported")
        }

    }

    class ArgumentNotFoundException : IllegalArgumentException()

    companion object {
        const val MSG_NEED_ARGUMENT = "Команде необходимо задать аргумент"
        const val MSG_ADB_ERROR = "ADB error"
        const val MSG_APP_ID_ERROR = "Invalid application id"
    }

}
