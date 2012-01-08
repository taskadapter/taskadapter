package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import org.mantis.ta.MantisManager;
import org.mantis.ta.beans.AccountData;
import org.mantis.ta.beans.IssueData;
import org.mantis.ta.beans.ProjectData;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MantisTaskSaver extends AbstractTaskSaver<MantisConfig> {

    private MantisManager mgr;
	private ProjectData mntProject;
	private MantisDataConverter converter;

	public MantisTaskSaver(MantisConfig config) {
		super(config);
	}

	@Override
	public void beforeSave() {
        this.mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());
        try {
            mntProject = mgr.getProjectById(new BigInteger(config.getProjectKey()));
            converter = new MantisDataConverter(config);
            converter.setUsers(loadUsers());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
	}

    private List<AccountData> loadUsers() {
        List<AccountData> users;
        if (config.getFindUserByName()) {
            try {
                users = mgr.getUsers();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            users = new ArrayList<AccountData>();
        }
        return users;
    }


	@Override
	protected Object convertToNativeTask(GTask task) {
		return converter.convertToMantisIssue(mntProject, task);
	}

	@Override
	protected GTask createTask(Object nativeTask) {
		try {
			BigInteger issueId = mgr.createIssue((IssueData) nativeTask);
            IssueData createdIssue = mgr.getIssueById(issueId);
			return MantisDataConverter.convertToGenericTask(createdIssue);
		} catch (Exception e) {
			throw new RuntimeException(e);
        }
    }

	@Override
	protected void updateTask(String taskId, Object nativeTask) {
        IssueData mntIssue = (IssueData) nativeTask;

        try {
            mgr.updateIssue(new BigInteger(taskId), mntIssue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	protected void saveRelations(List<GRelation> relations) {
		System.out.println("saveRelations: not implemented for Mantis");
	}
}
