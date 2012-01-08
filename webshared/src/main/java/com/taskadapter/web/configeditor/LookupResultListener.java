package com.taskadapter.web.configeditor;

/**
 * @author Alexey Skorokhodov
 */

import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public interface LookupResultListener {
	public void notifyDone(List<? extends NamedKeyedObject> objects);
}
