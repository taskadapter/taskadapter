package com.taskadapter.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.common.TreeUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taskadapter.model.GTask;

public class TreeUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneTree() {
		List<GTask> tree = new ArrayList<GTask>();
		
		GTask genericTask = new GTask();
		genericTask.setSummary("genericTask");
		
		tree.add(genericTask);
		
		GTask sub1 = new GTask();
		sub1.setSummary("sub1");
		
		GTask sub2 = new GTask();
		sub2.setSummary("sub2");
		
		genericTask.getChildren().add(sub1);
		genericTask.getChildren().add(sub2);
		
		List<GTask> cloned = TreeUtils.cloneTree(tree);
		
		final String NEW_TEXT ="newtext"; 
		sub1.setSummary(NEW_TEXT);
		
		GTask clonedGenericTask = cloned.get(0);
		GTask clonedSub1 = clonedGenericTask.getChildren().get(0);
		
		Assert.assertEquals(NEW_TEXT, sub1.getSummary());
		Assert.assertEquals("sub1", clonedSub1.getSummary());
	}

}
