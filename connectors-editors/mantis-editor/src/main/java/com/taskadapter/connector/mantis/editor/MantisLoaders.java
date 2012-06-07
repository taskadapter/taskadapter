package com.taskadapter.connector.mantis.editor;

import java.util.List;

import org.mantis.ta.MantisManager;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.mantis.MantisManagerFactory;
import com.taskadapter.connector.mantis.MantisProjectConverter;
import com.taskadapter.model.GProject;

/**
 * Mantis data loaders.
 * 
 * @author maxkar
 * 
 */
public class MantisLoaders {
	public static List<GProject> getProjects(WebServerInfo serverInfo)
			throws ValidationException {
		validate(serverInfo);

		MantisManager mgr = MantisManagerFactory
				.createMantisManager(serverInfo);
		List<org.mantis.ta.beans.ProjectData> mntProjects;

		try {
			mntProjects = mgr.getProjects();
		} catch (Exception e) {
			throw new RuntimeException(e.toString(), e);
		}

		return new MantisProjectConverter().toGProjects(mntProjects);
	}

	private static void validate(WebServerInfo serverInfo)
			throws ValidationException {
		if (!serverInfo.isHostSet()) {
			throw new ValidationException("Host URL is not set");
		}
	}

}
