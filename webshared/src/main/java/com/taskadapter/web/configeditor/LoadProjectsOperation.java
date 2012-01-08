package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LoadProjectsOperation extends LookupOperation {

    public LoadProjectsOperation(ConfigEditor editor, Descriptor descriptor) {
        super(editor, descriptor);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws Exception {
        WebServerInfo serverInfo = config.getServerInfo();
        if ((serverInfo.getHost() == null)
                || (serverInfo.getHost().isEmpty())) {
            throw new ValidationException("Host URL is not set");
        }

        return descriptor.getProjectLoader().getProjects(serverInfo);
    }
}

