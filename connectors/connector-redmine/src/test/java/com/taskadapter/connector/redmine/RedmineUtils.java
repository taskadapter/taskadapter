package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RedmineUtils {

    public static GTask generateTaskWithPrecedesRelations(RedmineConnector redmine,
                                                          Integer childCount) throws Exception {
        List<GTask> list = new ArrayList<GTask>();

        GTask task = TestUtils.generateTask();
        task.setId(1);
        list.add(task);

        for (int i = 0; i < childCount; i++) {
            GTask task1 = TestUtils.generateTask();
            task1.setId(i + 2);

            task.getRelations().add(new GRelation(task.getId().toString(), task1.getId().toString(), GRelation.TYPE.precedes));
            list.add(task1);
        }

        List<GTask> loadedList = TestUtils.saveAndLoadList(redmine, list);

        return TestUtils.findTaskBySummary(loadedList, task.getSummary());
    }
}
