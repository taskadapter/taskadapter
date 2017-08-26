package com.taskadapter.connector.basecamp.classic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taskadapter.connector.basecamp.ThreadLocalDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

/**
 * Xml utilities.
 * 
 */
public final class XmlUtils {
    private static final ThreadLocalDateFormat LONG_DATE = new ThreadLocalDateFormat(
            "yyyy-MM-dd HH:mm:ssZ");
    
    public static void setString(Document d, Element e, String name,
            String value) {
        if (name == null)
            return;
        final Element elt = d.createElement(name);
        elt.appendChild(d.createTextNode(value));
        e.appendChild(elt);
    }
    
    public static void setLong(Document d, Element e, String name,
            Date value) {
        if (value == null || name == null)
            return;
        final Element elt = d.createElement(name);
        elt.appendChild(d.createTextNode(LONG_DATE.get().format(value).replace(' ', 'T')));
        e.appendChild(elt);
    }

    public static int getIntElt(Element elt, String name)
            throws CommunicationException {
        final String val = getStringElt(elt, name);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new CommunicationException("Bad number value " + val);
        }
    }

    public static String getStringElt(Element elt, String name)
            throws CommunicationException {
        final List<Element> nl = getDirectAncestors(elt, name);
        if (nl.size() == 0)
            throw new CommunicationException("Cannot find required element "
                    + name + " in " + elt);
        if (nl.size() > 1)
            throw new CommunicationException("Too many ( " + nl.size()
                    + ") elements of" + name + " in " + elt);
        return nl.get(0).getTextContent();
    }

    public static String getOptString(Element elt, String name)
            throws CommunicationException {
        final List<Element> nl = getDirectAncestors(elt, name);
        if (nl.size() == 0)
            return null;
        if (nl.size() > 1)
            throw new CommunicationException("Too many ( " + nl.size()
                    + ") elements of" + name + " in " + elt);
        return nl.get(0).getTextContent();
    }
    
    public static Element getOptElt(Element elt, String name)
            throws CommunicationException {
        final List<Element> nl = getDirectAncestors(elt, name);
        if (nl.size() == 0)
            return null;
        if (nl.size() > 1)
            throw new CommunicationException("Too many ( " + nl.size()
                    + ") elements of" + name + " in " + elt);
        return nl.get(0);
    }
    
    public static boolean getOptBool(Element elt, String name)
            throws CommunicationException {
        final List<Element> nl = getDirectAncestors(elt, name);
        if (nl.size() == 0)
            return false;
        if (nl.size() > 1)
            throw new CommunicationException("Too many ( " + nl.size()
                    + ") elements of" + name + " in " + elt);
        return "true".equalsIgnoreCase(nl.get(0).getTextContent());
    }

    public static List<Element> getDirectAncestors(Element elt, String name) {
        final NodeList nl = elt.getChildNodes();
        final List<Element> res = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node n = nl.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            final Element ee = (Element) n;
            if (!name.equals(ee.getTagName()))
                continue;
            res.add(ee);
        }
        return res;
    }

    public static Date getOptLongDate(Element elt, String field)
            throws CommunicationException {
        final Element eee = getOptElt(elt, field);
        if (eee == null)
            return null;
        if (eee.hasAttribute("nil") && "true".equalsIgnoreCase(eee.getAttribute("nil")))
            return null;
        String str = eee.getTextContent();
        if (str == null) {
            return null;
        }
        str = str.replaceFirst("T", " ");
        if (str.endsWith("Z"))
            str = str.substring(0, str.length() - 1) + "+0000";
        try {
            return LONG_DATE.get().parse(str);
        } catch (ParseException e) {
            throw new CommunicationException(e);
        }
    }
    
}
