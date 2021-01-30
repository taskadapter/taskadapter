package com.taskadapter.webui.results;

import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.pages.Navigator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@Route(value = Navigator.RESULT, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class ExportResultPage extends BasePage implements HasUrlParameter<String> {

    @Override
    protected void beforeEnter() {
    }

    @Override
    public void setParameter(BeforeEvent event, String resultId) {
        ExportResultStorage resultStorage = SessionController.getServices().exportResultStorage;
        Optional<ExportResultFormat> result = resultStorage.getResult(resultId);
        if (!result.isPresent()) {
            throw new RuntimeException("Cannot find result with id");
        }
        showResult(result.get());
    }


    private void showResult(ExportResultFormat result) {
        removeAll();

        ExportResultsFragment fragment = new ExportResultsFragment(
                SessionController.getServices().settingsManager.isTAWorkingOnLocalMachine());
        Component component = fragment.showExportResult(result);

        add(component);
    }
}
