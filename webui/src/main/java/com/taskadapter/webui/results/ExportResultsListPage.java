package com.taskadapter.webui.results;

import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.pages.Navigator;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.function.Function;

@Route(value = Navigator.RESULTS_LIST, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class ExportResultsListPage extends BasePage {
    @Override
    protected void beforeEnter() {
        EventTracker.trackPage(Navigator.RESULTS_LIST);
        refreshPage();
    }

    private void refreshPage() {
        removeAll();
        ExportResultStorage resultStorage = SessionController.getServices().exportResultStorage;
        List<ExportResultFormat> results = resultStorage.getSaveResults();
        ExportResultsLayout layout = new ExportResultsLayout(showExportResultsJava());
        layout.showResults(results);

        add(layout);
    }

    private Function<ExportResultFormat, Void> showExportResultsJava() {
        return (result) -> {
            Navigator.result(result.resultId());
            return null;
        };
    }
}
