package com.taskadapter.connector.basecamp.classic.transport;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.taskadapter.connector.basecamp.classic.exceptions.FatalMisunderstaningException;
import com.taskadapter.connector.basecamp.classic.exceptions.InternalException;
import com.taskadapter.connector.basecamp.classic.exceptions.ObjectNotFoundException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPI {
    /**
     * Connection prefix.
     */
    private final String prefix;

    /**
     * User communicator.
     */
    private final Communicator communicator;

    public ObjectAPI(String prefix, Communicator communicator) {
        if (!prefix.endsWith("/")) {
            this.prefix = prefix + "/";
        } else {
            this.prefix = prefix;
        }
        this.communicator = communicator;
    }

    public Element getObject(String suffix) throws ConnectorException {
        return rawGet(prefix + suffix);
    }
    
    private Element rawGet(String url) throws ConnectorException {
        final HttpGet get = new HttpGet(url);
        final BasicHttpResponse response = communicator.sendRequest(get);
        if (response.getResponseCode() == 404) {
            throw new ObjectNotFoundException();
        }
        if (response.getResponseCode() != 200) {
            throw new CommunicationException("Unexpected error code "
                    + response.getResponseCode() + " : "
                    + response.getContent());
        }
        return parseXML(response.getContent());
    }

    public Element post(String suffix, String content)
            throws ConnectorException {
        final HttpPost post = new HttpPost(prefix + suffix);
        post.setEntity(new StringEntity(content, ContentType.APPLICATION_XML));
        final BasicHttpResponse resp = communicator.sendRequest(post);
        if (resp.getResponseCode() != 201) {
            throw new CommunicationException("Unexpected error code "
                    + resp.getResponseCode() + " : " + resp.getContent());
        }
        return rawGet(resp.getLocation() + ".xml");
    }

    public void put(String suffix, String content)
            throws ConnectorException {
        final HttpPut post = new HttpPut(prefix + suffix);
        post.setEntity(new StringEntity(content, ContentType.APPLICATION_XML));
        final BasicHttpResponse resp = communicator.sendRequest(post);
        if (resp.getResponseCode() != 200) {
            throw new CommunicationException("Unexpected error code "
                    + resp.getResponseCode() + " : " + resp.getContent());
        }
    }

    public void delete(String suffix) throws ConnectorException {
        final HttpDelete get = new HttpDelete(prefix + suffix);
        final BasicHttpResponse resp = communicator.sendRequest(get);
        if (resp.getResponseCode() != 200) {
            throw new CommunicationException("Unexpected error code "
                    + resp.getResponseCode() + " : " + resp.getContent());
        }
    }

    private Element parseXML(String docText)
            throws FatalMisunderstaningException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setValidating(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver((publicId, systemId) -> {
                throw new IOException("Entity resolution is not supported, bad XML");
            });
            final Document doc = db.parse(new InputSource(new StringReader(docText)));
            return doc.getDocumentElement();
        } catch (ParserConfigurationException | IOException e) {
            throw new InternalException();
        } catch (SAXException e) {
            throw new FatalMisunderstaningException("Can't parse response "+ docText, e);
        }
    }
}
