package com.taskadapter.webui.pages.config.wizard;

import com.taskadapter.webui.Page;
import com.taskadapter.webui.pages.WizardStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class WizardPanel extends VerticalLayout {
    private final Map<Integer, WizardStep<?>> steps = new HashMap<>();

    private final Label progressLabel = new Label();
    private final ProgressBar progressBar = new ProgressBar();
    private final HorizontalLayout stepLayout = new HorizontalLayout();

    public WizardPanel() {
        setMargin(true);

        progressBar.setWidth("500px");

        add(progressLabel, progressBar,
                stepLayout);
    }

    private int totalSteps() {
        return steps.size();
    }

    public void showStep(int step) {
        progressLabel.setText(Page.message("newConfig.step", step + "", totalSteps() + ""));
        progressBar.setValue((float) step / totalSteps());
        stepLayout.removeAll();
        var previousStep = steps.get(step - 1);
        var config = previousStep == null ? null: previousStep.getResult();
        var nextStep = steps.get(step);
        stepLayout.add(nextStep.ui(config));
    }

    public void registerStep(WizardStep<?> wizardStep) {
        var stepNumber = steps.size() + 1;
        steps.put(stepNumber, wizardStep);
    }
}
