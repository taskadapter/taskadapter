package com.taskadapter.webui.results;

import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ExportResultsLayout extends VerticalLayout {
    private static String dateFormat = "yyyy-MM-dd HH:mm";

    private Grid<ExportResultFormat> grid = new Grid<>();
    private Label label = new Label(Page.message("exportResults.intro"));

    private Function<ExportResultFormat, Void> showResultCommand;
    Grid.Column dateStartedColumn;

    public ExportResultsLayout(Function<ExportResultFormat, Void> showResultCommand) {
        this.showResultCommand = showResultCommand;
        buildUi();
    }

    public void buildUi() {
        grid.addColumn(ExportResultFormat::getConfigLabel)
                .setHeader(Page.message("exportResults.column.configName"))
                .setWidth("120px")
                .setFlexGrow(2);

        dateStartedColumn = grid.addColumn(ExportResultFormat::getDateStarted)
                .setHeader(Page.message("exportResults.column.startedOn"))
                .setWidth("150px")
                .setFlexGrow(2);

        grid.addColumn(ExportResultFormat::getTo)
                .setHeader(Page.message("exportResults.column.to"))
                .setWidth("150px")
                .setFlexGrow(2);

        grid.addColumn(new ComponentRenderer<>(r -> {
            if (r.isSuccess()) {
                return new Label(Page.message("exportResults.column.status.success"));
            }
            return new HtmlLabel("<font color= 'red'>" + Page.message("exportResults.column.status.errors") + "</font>");
        }))
                .setHeader(Page.message("exportResults.column.status"))
                .setWidth("70px")
                .setFlexGrow(1);

        grid.addColumn(ExportResultFormat::getCreatedTasksNumber)
                .setHeader(Page.message("exportResults.column.tasksCreated"))
                .setWidth("50px")
                .setFlexGrow(1);

        grid.addColumn(ExportResultFormat::getUpdatedTasksNumber)
                .setHeader(Page.message("exportResults.column.tasksUpdated"))
                .setWidth("50px")
                .setFlexGrow(1);

        grid.addSelectionListener((SelectionListener<Grid<ExportResultFormat>, ExportResultFormat>) event ->
                event.getFirstSelectedItem()
                        .ifPresent(item -> showResultCommand.apply(item)));

        add(label, grid);
    }

    public void showResults(List<ExportResultFormat> results) {
        grid.setItems(results);
        grid.sort(Arrays.asList(new GridSortOrder(dateStartedColumn, SortDirection.DESCENDING)));
    }
}
