package com.taskadapter.web.configeditor;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

/**
 * Common place to instantiate editors.
 * 
 */
public class Editors {
	public static CheckBox createFindUsersElement(Property property) {
		final CheckBox findUserByName = new CheckBox(
				"Find users based on assignee's name");
		findUserByName
				.setDescription("This option can be useful when you need to export a new MSP project file to Redmine/Jira/Mantis/....\n"
						+ "Task Adapter can load the system's users by resource names specified in the MSP file\n"
						+ "and assign the new tasks to them.\n"
						+ "Note: this operation usually requires 'Admin' permission in the system.");
		findUserByName.setPropertyDataSource(property);
		return findUserByName;
	}

}
