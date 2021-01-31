package com.taskadapter.webui.pages;

import com.taskadapter.web.PopupDialog;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.Schedule;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Route(value = Navigator.SCHEDULES_LIST, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class SchedulesListPage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(SchedulesListPage.class);
    private static final int configRowsToShowInListSelect = 15;

    private ConfigOperations configOps = SessionController.buildConfigOperations();
    private Preservices services = SessionController.getServices();

    private Grid<ScheduleListItem> grid = new Grid<>();

    private Component listLayout;
    private VerticalLayout configsListLayout = new VerticalLayout();
    private Grid.Column<ScheduleListItem> configLabelColumn;

    public SchedulesListPage() {
        buildUi();
    }

    private void buildUi() {
        listLayout = createListLayout();
        showInPage(listLayout);
    }

    private void showInPage(Component component) {
        removeAll();
        add(LayoutsUtil.centered(Sizes.mainWidth(), component));
    }

    @Override
    protected void beforeEnter() {
        showSchedules(services.schedulesStorage.getSchedules());
    }

    private void saveSchedule(Schedule schedule) {
        services.schedulesStorage.store(schedule);
        showSchedules();
    }

    private void deleteSchedule(Schedule schedule) {
        services.schedulesStorage.delete(schedule.getId());
        showSchedules();
    }

    private void showSchedule(final Schedule schedule) {
        UISyncConfig config = getConfigForSchedule(schedule)
                .orElseThrow(() -> new RuntimeException("cannot open schedule " + schedule
                        + " because its config is not found"));
        EditSchedulePage editSchedulePage = new EditSchedulePage(
                config.label(),
                config.getConnector1().getLabel(),
                config.getConnector2().getLabel(),
                schedule,
                saveSchedule -> {
                    saveSchedule(saveSchedule);
                    return null;
                },
                () -> showSchedules(),
                deleteSchedule -> {
                    PopupDialog.confirm("Do you want to delete this scheduled item?",
                            () -> {
                                deleteSchedule(deleteSchedule);
                                return null;
                            });
                    return null;
                }
        );
        showInPage(editSchedulePage.ui());
    }

    private Optional<UISyncConfig> getConfigForSchedule(Schedule schedule) {
        return configOps.getOwnedConfigs()
                .stream().filter(c -> c.configId().equals(schedule.getConfigId()))
                .findFirst();
    }

    private void showSchedule(String scheduleId) {
        Schedule schedule = services.schedulesStorage.get(scheduleId).get();
        showSchedule(schedule);
    }

    private void showNewScheduleEditor(ConfigId configId) {
        Schedule schedule = new Schedule(UUID.randomUUID().toString(), configId, 60, false, false);
        showSchedule(schedule);
    }

    private void showSchedules() {
        showSchedules(services.schedulesStorage.getSchedules());
    }

    private void showSelectConfig() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        Label label = new Label(Page.message("schedules.selectConfig.title"));
        layout.add(label);
        layout.add(configsListLayout);

        showConfigsInList();
        showInPage(layout);
    }

    private Component createListLayout() {

        Button addButton = new Button(Page.message("schedules.newButton"),
                event -> showSelectConfig());

        Checkbox checkbox = new Checkbox(Page.message("schedules.scheduledEnabled"));
        checkbox.setValue(services.settingsManager.schedulerEnabled());
        checkbox.addValueChangeListener(value ->
                services.settingsManager.setSchedulerEnabled(checkbox.getValue()));

        grid.setWidth("980px");
        configLabelColumn = grid.addColumn(ScheduleListItem::getConfigLabel)
                .setHeader(Page.message("schedules.column.label"))
                .setFlexGrow(1);

        grid.addColumn(ScheduleListItem::getIntervalMin)
                .setHeader(Page.message("schedules.column.interval"))
                .setFlexGrow(1);

        grid.addSelectionListener((SelectionListener<Grid<ScheduleListItem>, ScheduleListItem>) event ->
                event.getFirstSelectedItem()
                        .ifPresent(item -> showSchedule(item.id)));

        Label label = new Label(Page.message("schedules.intro"));

        HorizontalLayout controlsLayout = new HorizontalLayout(addButton, checkbox);
        controlsLayout.setWidth("100%");
        controlsLayout.setSpacing(true);

        VerticalLayout listLayout = new VerticalLayout();
        listLayout.add(label);
        listLayout.add(controlsLayout);
        listLayout.add(grid);
        return listLayout;
    }

    private void showConfigsInList() {
        List<UISyncConfig> configsList = configOps.getOwnedConfigs();

        configsList.stream().forEach(c -> {
            Button button = new Button(c.getLabel());
            button.setWidth("200px");
            button.addClickListener(e -> showNewScheduleEditor(c.configId()));
            configsListLayout.add(button);
        });
    }

    private void showSchedules(List<Schedule> results) {
        List<ScheduleListItem> items = results.stream().filter(schedule -> {
            Optional<UISyncConfig> maybeConfig = getConfigForSchedule(schedule);
            if (!maybeConfig.isPresent()) {
                log.error("cannot find config for schedule " + schedule);
            }
            return maybeConfig.isPresent();
        }).map(schedule -> {
            // TODO 14 do not load the configs twice!
            Optional<UISyncConfig> maybeConfig = getConfigForSchedule(schedule);
            return new ScheduleListItem(schedule.getId(), schedule.getConfigId(), maybeConfig.get().label(),
                    schedule.getIntervalInMinutes(), maybeConfig.get().getConnector2().getLabel());
        }).collect(Collectors.toList());

        grid.sort(Arrays.asList(new GridSortOrder(configLabelColumn, SortDirection.ASCENDING)));

        grid.setItems(items);
        showInPage(listLayout);
    }

    private static class ScheduleListItem {
        String id;
        ConfigId configId;
        String configLabel;
        int intervalMin;
        String to;

        public ScheduleListItem(String id, ConfigId configId, String configLabel, int intervalMin, String to) {
            this.id = id;
            this.configId = configId;
            this.configLabel = configLabel;
            this.intervalMin = intervalMin;
            this.to = to;
        }

        public String getId() {
            return id;
        }

        public ConfigId getConfigId() {
            return configId;
        }

        public String getConfigLabel() {
            return configLabel;
        }

        public int getIntervalMin() {
            return intervalMin;
        }

        public String getTo() {
            return to;
        }
    }
}

