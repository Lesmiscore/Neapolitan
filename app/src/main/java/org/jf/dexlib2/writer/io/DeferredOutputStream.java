package org.jf.dexlib2.writer.io;

import java.io.*;

public abstract class DeferredOutputStream extends OutputStream {
    public abstract void writeTo(OutputStream output) throws IOException;
}
