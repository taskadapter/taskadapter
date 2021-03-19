package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
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

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class MantisConnector implements NewConnector {

    public static final String ID = "Mantis";

    private MantisConfig config;
    private WebConnectorSetup setup;

    public MantisConnector(MantisConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    @Override
    public GTask loadTaskByKey(TaskId key, Iterable<FieldRow<?>> rows) throws ConnectorException {
        MantisManager mgr = MantisManagerFactory.createMantisManager(setup);
        try {
            var issue = mgr.getIssueById(BigInteger.valueOf(key.getId()));
            return MantisToGTask.convertToGenericTask(issue);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        }
    }

    @Override
    public List<GTask> loadData() throws ConnectorException {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(setup);

            var queryId = config.getQueryId();
            final BigInteger pkey = config.getProjectKey() == null ? null
                    : new BigInteger(config.getProjectKey());

            List<IssueData> issues = queryId == null ? mgr.getIssuesByProject(pkey)
                    : mgr.getIssuesByFilter(pkey, BigInteger.valueOf(queryId));
            return convertToGenericTasks(issues);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        }
    }

    private List<GTask> convertToGenericTasks(List<IssueData> issues) {
        List<GTask> result = new ArrayList<>(issues.size());
        for (IssueData issue : issues) {
            GTask task = MantisToGTask.convertToGenericTask(issue);
            result.add(task);
        }
        return result;
    }

    @Override
    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                               List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> rows) {
        var mgr = MantisManagerFactory.createMantisManager(setup);
        try {
            var mntProject = mgr.getProjectById(new BigInteger(
                    config.getProjectKey()));
            List<AccountData> users = config.isFindUserByName() ? mgr.getUsers() : new ArrayList<>();
            var converter = new GTaskToMantis(mntProject, users);

            var saver = new MantisTaskSaver(mgr);

            var rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor,
                    rows, setup.getHost());
            return rb.getResult();
        } catch (RemoteException e) {
            return new SaveResult("", 0, 0,
                    List.of(),
                    List.of(e),
                    List.of());
        }
    }
}
