package ru.deeplink.plugin;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MyToolWindowFactory implements ToolWindowFactory {

    final PluginConfigStoreService configStoreService = ServiceManager.getService(PluginConfigStoreService.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        String sdkPath = Optional.ofNullable(ProjectRootManager.getInstance(project).getProjectSdk())
                .map(Sdk::getHomePath)
                .orElse(null);

        DeeplinkView view = new DeeplinkView(
                new DeeplinkPresenter(
                        new PluginCommandExecutor(project),
                        configStoreService
                ));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view.getBackground(), "", false);
        toolWindow.getContentManager().addContent(content);

    }

    @Override
    public void init(ToolWindow window) {

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }
}
