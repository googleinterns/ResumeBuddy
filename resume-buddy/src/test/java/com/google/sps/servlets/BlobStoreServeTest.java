package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.servlets.LoginServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for LoginServlet */
@RunWith(JUnit4.class)
public class BlobStoreServeTest {

  private static final String USER_EMAIL = "";
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private BlobStoreServeServlet blobStoreServeServlet;
  private DatastoreService datastore;
  private String uuid;
  private Entity match1;
  private Entity match2;

  private LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig(),
              new LocalURLFetchServiceTestConfig(),
              new LocalBlobStoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    blobStoreServeServlet = new BlobStoreServeServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();
    
    match1 = new Entity("Match");
    match1.setProperty("reviewee", "alexham@google.com");
    match1.setProperty("reviewer", "sesexton@google.com");
    match1.setProperty("resumeBlobKey",  //gotta figure out something for here);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testLogedInUser() throws ServletException, IOException {

    JsonObject response = getLoginServletResponse();
    String loginUrl = response.get("login_url").getAsString();
    String logoutUrl = response.get("logout_url").getAsString();
    String email = response.get("email").getAsString();
    String status = response.get("status").getAsString();

    Assert.assertTrue(loginUrl.contains("login"));
    Assert.assertTrue(logoutUrl.contains("logout"));
    Assert.assertTrue(email.equals("animachaidze@gmail.com"));
    Assert.assertTrue(status.equals("true"));
  }
}
