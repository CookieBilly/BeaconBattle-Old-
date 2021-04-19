

package org.apache.commons.io.output;

import java.io.IOException;
import org.apache.commons.io.TaggedIOException;
import java.util.UUID;
import java.io.OutputStream;
import java.io.Serializable;

public class TaggedOutputStream extends ProxyOutputStream
{
    private final Serializable tag;
    
    public TaggedOutputStream(final OutputStream outputStream) {
        super(outputStream);
        this.tag = UUID.randomUUID();
    }
    
    public boolean isCauseOf(final Exception ex) {
        return TaggedIOException.isTaggedWith(ex, this.tag);
    }
    
    public void throwIfCauseOf(final Exception ex) {
        TaggedIOException.throwCauseIfTaggedWith(ex, this.tag);
    }
    
    @Override
    protected void handleIOException(final IOException ex) {
        throw new TaggedIOException(ex, this.tag);
    }
}
