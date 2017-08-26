package com.taskadapter.connector.basecamp.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import com.taskadapter.connector.basecamp.exceptions.ExternalInterlalFailure;
import com.taskadapter.connector.basecamp.exceptions.FatalMisunderstaningException;
import com.taskadapter.connector.basecamp.transport.throttling.ThrottlingException;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

final class BasecampContentReader {
    static BasicHttpResponse processContent(HttpResponse httpResponse)
            throws ConnectorException {
        final int responseCode = httpResponse.getStatusLine().getStatusCode();
        if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new NotAuthorizedException();
        }
        if (responseCode == 400 || responseCode == 415) {
            throw new FatalMisunderstaningException(
                    "Something is wrong with a comm protocol, code "
                            + responseCode);
        }
        if (responseCode == 429) {
            final Header[] headers = httpResponse.getHeaders("Retry-After");
            int timeout;
            if (headers == null || headers.length != 1) {
                timeout = -1;
            } else {
                try {
                    timeout = Integer.parseInt(headers[0].getValue());
                } catch (NumberFormatException e) {
                    timeout = -1;
                }
            }
            throw new ThrottlingException(timeout);
        }

        if (500 <= responseCode && responseCode < 600) {
            throw new ExternalInterlalFailure("External failure, code "
                    + responseCode + ", " + getContent(httpResponse));
        }

        return new BasicHttpResponse(responseCode, getContent(httpResponse), null);
    }

    private static String getContent(HttpResponse content)
            throws ConnectorException {
        final HttpEntity entity = content.getEntity();
        if (entity == null) {
            return "";
        }
        final String charset = HttpUtil.getCharset(entity);
        final String encoding = HttpUtil.getEntityEncoding(entity);
        try {
            final InputStream initialStream = entity.getContent();
            final InputStream decodedStream = decodeStream(encoding,
                    initialStream);
            final Reader reader = new InputStreamReader(decodedStream, charset);
            return readAll(reader);
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }

    private static String readAll(Reader r) throws ConnectorException {
        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[4096];
        int readed;
        try {
            while ((readed = r.read(buffer)) > 0) {
                writer.write(buffer, 0, readed);
            }
            r.close();
            writer.close();
            return writer.toString();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }

    /**
     * Decodes a transport stream.
     * 
     * @param encoding
     *            stream encoding.
     * @param initialStream
     *            initial stream.
     * @return decoding stream.
     * @throws IOException
     */
    private static InputStream decodeStream(String encoding,
            InputStream initialStream) throws IOException {
        if (encoding == null)
            return initialStream;
        if ("gzip".equals(encoding))
            return new GZIPInputStream(initialStream);
        if ("deflate".equals(encoding))
            return new InflaterInputStream(initialStream);
        throw new IOException("Unsupported transport encoding " + encoding);
    }

}
