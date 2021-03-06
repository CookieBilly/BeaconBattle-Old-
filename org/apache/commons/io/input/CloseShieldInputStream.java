

package org.apache.commons.io.input;

import java.io.InputStream;

public class CloseShieldInputStream extends ProxyInputStream
{
    public CloseShieldInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    @Override
    public void close() {
        this.in = new ClosedInputStream();
    }
}
