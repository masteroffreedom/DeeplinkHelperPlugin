package ru.deeplink.plugin

import com.intellij.configurationStore.APP_CONFIG
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * More details here https://jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html
 */
//@State(name="deeplink_plugin_config")
@State(name = "deeplink_plugin_config",
//        storages = arrayOf(Storage(StoragePathMacros.WORKSPACE_FILE))
        storages = arrayOf(
                Storage(file = "$APP_CONFIG$/testpersist.xml")
        )
)

class PluginConfigStoreService() : PersistentStateComponent<PluginConfigStoreService> {

    var stateValue: String? = null

    override fun getState(): PluginConfigStoreService {
        return this
    }

    override fun loadState(state: PluginConfigStoreService) {
        XmlSerializerUtil.copyBean(state, this);
    }

}