package com.cool.request.action;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.NotifyUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an action to clean cache in the application.
 * It extends the AnAction class provided by IntelliJ IDEA's action system.
 */
public class CleanCacheAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final MainTopTreeView mainTopTreeView;

    /**
     * Constructor for the CleanCacheAnAction class.
     *
     * @param mainTopTreeView The main view of the application.
     */
    public CleanCacheAnAction(MainTopTreeView mainTopTreeView) {
        super(ResourceBundleUtils.getString("clear.request.cache"));
        getTemplatePresentation().setIcon(CoolRequestIcons.CLEAN);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }

    /**
     * This method is called when the action is performed.
     * It clears the cache based on the selected node in the tree view.
     *
     * @param e The event object associated with the action.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        List<String> deleteIds = new ArrayList<>();
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne != null &&
                (selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.FeaturesModuleNode)) {
            String data = ((MainTopTreeView.FeaturesModuleNode) selectedPathIfOne.getLastPathComponent()).getData();
            if ("Controller".equalsIgnoreCase(data)) {
                ProviderManager.findAndConsumerProvider(UserProjectManager.class, project, userProjectManager -> {
                    for (Controller controller : userProjectManager.getController()) {
                        ComponentCacheManager.removeCache(controller.getId());
                        deleteIds.add(controller.getId());
                    }
                });

            }
        }
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RootNode) {
            ComponentCacheManager.removeAllCache();

        }
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.ClassNameNode) {
            MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) selectedPathIfOne.getLastPathComponent();
            ProviderManager.findAndConsumerProvider(MainTopTreeViewManager.class, project, mainTopTreeViewManager -> {
                for (MainTopTreeView.RequestMappingNode requestMappingNode : mainTopTreeViewManager.getRequestMappingNodeMap().
                        getOrDefault(classNameNode, List.of())) {
                    ComponentCacheManager.removeCache(requestMappingNode.getData().getId());
                    deleteIds.add(requestMappingNode.getData().getId());
                }
            });

        }
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            ComponentCacheManager.removeCache(requestMappingNode.getData().getId());
            deleteIds.add(requestMappingNode.getData().getId());
        }
        if (deleteIds.isEmpty()) {
            mainTopTreeView.getProject().getMessageBus().syncPublisher(CoolRequestIdeaTopic.CLEAR_REQUEST_CACHE).onClearAllEvent();
        } else {
            mainTopTreeView.getProject().getMessageBus().syncPublisher(CoolRequestIdeaTopic.CLEAR_REQUEST_CACHE).onClearEvent(deleteIds);
        }
        NotifyUtils.notification(mainTopTreeView.getProject(), "Clear Success");
    }
}
