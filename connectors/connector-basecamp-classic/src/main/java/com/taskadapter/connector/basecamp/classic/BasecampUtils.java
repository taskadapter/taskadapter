package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.exceptions.InternalException;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BasecampUtils {

    public static List<BasecampProject> loadProjects(ObjectAPIFactory factory,
                                                     WebConnectorSetup setup) throws ConnectorException {
        final ObjectAPI objApi = factory.createObjectAPI(setup);
        final Element objects = objApi.getObject("projects.xml");
        final List<Element> projects = XmlUtils.getDirectAncestors(objects,
                "project");
        final List<BasecampProject> result = new ArrayList<>(
                projects.size());
        for (Element project : projects) {
            result.add(parseProjectFromList(project));
        }
        return result;
    }

    public static BasecampProject loadProject(ObjectAPIFactory factory,
                                              BasecampClassicConfig config, WebConnectorSetup setup) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);
        final ObjectAPI objApi = factory.createObjectAPI(setup);
        String objectURL = "projects/" + config.getProjectKey() + ".xml";
        final Element object = objApi.getObject(objectURL);
        return parseFullProject(object);
    }

    // POST /projects/#{project_id}/todo_lists.xml
    public static TodoList createTodoList(ObjectAPIFactory factory,
                                          BasecampClassicConfig config, WebConnectorSetup setup, String todoListName,
                                          String todoListDescription) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);
        BasecampConfigValidator.validateProjectKey(config);
        String todoListXmlRepr = buildTodoListXmlObject(todoListName,
                todoListDescription);
        Element result = factory.createObjectAPI(setup).post(
                "projects/" + config.getProjectKey() + "/todo_lists.xml",
                todoListXmlRepr);
        return parseTodoList(result);
    }

    public static void deleteTodoList(ObjectAPIFactory factory,
                                      BasecampClassicConfig config, WebConnectorSetup setup) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);
        // TODO BUG: for Maxim - I don't think Basecamp Classic supports JSON
        factory.createObjectAPI(setup).delete(
                "/todo_lists/" + config.getTodoKey() + ".json");
    }

    public static String buildTodoListXmlObject(String todoListName,
                                                String todoListDescription) {
        final Document d = newXDoc();

        final Element root = d.createElement("todo-list");
        d.appendChild(root);

        XmlUtils.setString(d, root, "description", todoListDescription);
        final Element milestone = d.createElement("milestone-id");
        milestone.setAttribute("nil", "true");
        root.appendChild(milestone);
        XmlUtils.setString(d, root, "name", todoListName);

        final Element pvt = d.createElement("private");
        root.appendChild(pvt);
        pvt.appendChild(d.createTextNode("true"));
        pvt.setAttribute("type", "boolean");

        final Element track = d.createElement("tracked");
        root.appendChild(track);
        track.appendChild(d.createTextNode("true"));
        track.setAttribute("type", "boolean");

        final Element templateId = d.createElement("todo-list-template-id");
        templateId.setAttribute("type", "integer");
        templateId.setAttribute("nil", "true");
        root.appendChild(templateId);

        return stringify(d);
    }

    public static String stringify(Document d) {
        try {
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer t = tf.newTransformer();

            final StringWriter sw = new StringWriter();

            t.transform(new DOMSource(d), new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException e) {
            throw new InternalException();
        }

    }

    public static Document newXDoc() {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document d = db.newDocument();
            d.setXmlStandalone(true);
            return d;
        } catch (ParserConfigurationException e) {
            throw new InternalException();
        }

    }

    public static List<TodoList> loadTodoLists(ObjectAPIFactory factory,
                                               BasecampClassicConfig config, WebConnectorSetup setup) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);
        final ObjectAPI objApi = factory.createObjectAPI(setup);
        final String suffix;
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty())
            suffix = "todo_lists.xml";
        else
            suffix = "projects/" + config.getProjectKey() + "/todo_lists.xml";
        final List<Element> objects = XmlUtils.getDirectAncestors(
                objApi.getObject(suffix), "todo-list");
        final List<TodoList> result = new ArrayList<>(objects.size());
        for (Element elt : objects)
            result.add(parseTodoList(elt));
        return result;
    }

    public static TodoList loadTodoList(ObjectAPIFactory factory,
                                        BasecampClassicConfig config, WebConnectorSetup setup) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);
        BasecampConfigValidator.validateTodoList(config);
        final ObjectAPI objApi = factory.createObjectAPI(setup);
        final Element object = objApi.getObject("todo_lists/"
                + config.getTodoKey() + ".xml");
        return parseTodoList(object);
    }

    /*
     * private static boolean isNum(String str) { if (str.length() == 0) {
     * return false; } for (int i = 0; i < str.length(); i++) { final char chr =
     * str.charAt(i); if (chr < '0' || '9' < chr) { return false; } } return
     * true; }
     */

    private static TodoList parseTodoList(Element elt)
            throws CommunicationException {
        final TodoList res = new TodoList();
        res.setKey(XmlUtils.getStringElt(elt, "id"));
        res.setDescription(XmlUtils.getStringElt(elt, "description"));
        res.setName(XmlUtils.getStringElt(elt, "name"));
        res.setCompletedCount(XmlUtils.getIntElt(elt, "completed-count"));
        res.setRemainingCount(XmlUtils.getIntElt(elt, "uncompleted-count"));
        return res;
    }

    private static BasecampProject parseProjectFromList(Element elt)
            throws CommunicationException {
        final BasecampProject project = new BasecampProject();
        project.setKey(XmlUtils.getStringElt(elt, "id"));
        project.setName(XmlUtils.getStringElt(elt, "name"));
        return project;
    }

    private static BasecampProject parseFullProject(Element elt)
            throws CommunicationException {
        final BasecampProject project = new BasecampProject();
        project.setKey(XmlUtils.getStringElt(elt, "id"));
        project.setName(XmlUtils.getStringElt(elt, "name"));
        return project;
    }


    public static GUser parseUser(Element assObj) throws CommunicationException {
        final GUser result = new GUser();
        result.setId(XmlUtils.getIntElt(assObj, "id"));
        String name = "";
        final String fname = XmlUtils.getOptString(assObj, "first-name");
        if (fname != null && !fname.isEmpty())
            name += fname;
        final String ffname = XmlUtils.getOptString(assObj, "last-name");
        if (ffname != null && !ffname.isEmpty())
            if (name.isEmpty())
                name = ffname;
            else
                name += " " + ffname;
        result.setDisplayName(name);
        return result;
    }

}
