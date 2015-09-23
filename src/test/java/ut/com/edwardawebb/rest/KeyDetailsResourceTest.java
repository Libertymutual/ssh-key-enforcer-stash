package ut.com.edwardawebb.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.edwardawebb.stash.ssh.rest.KeyDetailsResource;
import com.edwardawebb.stash.ssh.rest.KeyDetailsResourceModel;

public class KeyDetailsResourceTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        KeyDetailsResource resource = new KeyDetailsResource(null, null);

        Response response = resource.getMessage();
        final KeyDetailsResourceModel message = (KeyDetailsResourceModel) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
