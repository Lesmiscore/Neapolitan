package org.jf.dexlib2.writer.io;

import java.io.*;

public interface DeferredOutputStreamFactory {
    DeferredOutputStream makeDeferredOutputStream() throws IOException;
}
