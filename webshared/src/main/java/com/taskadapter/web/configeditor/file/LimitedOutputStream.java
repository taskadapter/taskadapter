package com.taskadapter.web.configeditor.file;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

final class LimitedOutputStream extends FilterOutputStream {

    /**
     * File size limit.
     */
    private final long limit;

    /**
     * Current file size.
     */
    private long current;

    public LimitedOutputStream(OutputStream peer, long limit) {
        super(peer);
        if (limit <= 0)
            throw new IllegalArgumentException("Limit must be positive but is "
                    + limit);
        this.limit = limit;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len + current > limit)
            throw new IOException("Maximal file size is exceeded");
        current += len;
        super.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        if (current >= limit)
            throw new IOException("Maximal file size is exceeded");
        current += 1;
        super.write(b);
    }

}
