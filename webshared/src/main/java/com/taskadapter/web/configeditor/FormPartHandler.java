package com.taskadapter.web.configeditor;

/**
 * Handles "form" events. Receives notifications about new form data when there
 * is any event from a user. Usually this interface is implemented by form
 * components, which updates underlying model for future processing.
 * 
 * @author maxkar
 * 
 */
public interface FormPartHandler {
	/**
	 * Notifies handler about page update.
	 */
	public void pageUpdated();
}
