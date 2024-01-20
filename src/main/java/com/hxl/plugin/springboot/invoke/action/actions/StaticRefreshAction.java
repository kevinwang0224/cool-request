package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.scans.controller.SpringMvcControllerScan;
import com.hxl.plugin.springboot.invoke.scans.scheduled.SpringScheduledScan;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class StaticRefreshAction extends AnAction {
    private final Project project;
    private final SpringMvcControllerScan springMvcControllerScan = new SpringMvcControllerScan();
    private final SpringScheduledScan springScheduledScan =new SpringScheduledScan();
    private final IToolBarViewEvents iViewEvents;

    public StaticRefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super(ResourceBundleUtils.getString("refresh.static"), ResourceBundleUtils.getString("refresh.static"), MyIcons.SCAN);
        this.project = project;
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.clearAllData();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Scan...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    List<Controller> staticControllerScanResult = springMvcControllerScan.scan(project);
                    assert project != null;
                    if (staticControllerScanResult == null || staticControllerScanResult.isEmpty()) {
                        NotifyUtils.notification(project, "Not Found Controller");
                        return;
                    }
                    Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).addComponent(staticControllerScanResult);
                    Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).addComponent(  springScheduledScan.scan(project));
                });
            }
        });
    }
}
