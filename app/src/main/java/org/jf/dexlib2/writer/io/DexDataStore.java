package org.jf.dexlib2.writer.io;

import java.io.*;

import javax.annotation.*;

public interface DexDataStore {
    @Nonnull
    OutputStream outputAt(int offset);

    @Nonnull
    InputStream readAt(int offset);

    void close() throws IOException;
}
