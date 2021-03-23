package com.taskadapter.connector.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.TList;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TrelloConnector implements NewConnector {
    private static final Logger logger = LoggerFactory.getLogger(TrelloConnector.class);
    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "Trello";
    private final Trello trelloApi;
    private TrelloConfig config;
    private WebConnectorSetup setup;

    public TrelloConnector(TrelloConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
        trelloApi = TrelloApiFactory.createApi(setup.getPassword(), setup.getApiKey());
    }

    @Override
    public GTask loadTaskByKey(TaskId key, Iterable<FieldRow<?>> rows) {
        var loader = new TrelloTaskLoader(trelloApi);
        return loader.loadTask(config, key.getKey());
    }

    @Override
    public List<GTask> loadData() throws ConnectorException {
        var loader = new TrelloTaskLoader(trelloApi);
        return loader.loadTasks(config);
    }

    private List<TList> loadLists(String boardId) {
        return trelloApi.getBoardLists(boardId);
    }

    @Override
    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                               List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> rows) {
        var lists = loadLists(config.getBoardId());
        var converter = new GTaskToTrello(config, new ListCache(lists));
        var saver = new TrelloTaskSaver(trelloApi);
        var rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
                setup.getHost());
        return rb.getResult();
    }

}
