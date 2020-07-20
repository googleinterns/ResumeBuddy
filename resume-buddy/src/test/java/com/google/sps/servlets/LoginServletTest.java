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
public class LoginServletTest {
  // TODO: Modify test cases accordingly when LoginServlet is changed

  private static final String USER_EMAIL = "animachaidze@gmail.com";
  private Entity reviewee;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private LoginServlet loginServlet;
  private DatastoreService datastore;

  private LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig(),
              new LocalURLFetchServiceTestConfig())
          .setEnvEmail(USER_EMAIL)
          .setEnvAuthDomain("gmail.com");

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    loginServlet = new LoginServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();

    reviewee = new Entity("Reviewee");
    reviewee.setProperty("email", "animachaidze@gmail.com");
    datastore.put(reviewee);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testLogedInUser() throws ServletException, IOException {
    helper.setEnvIsLoggedIn(true);

    JsonObject response = getLoginServletResponse();
    String loginUrl = response.get("login_url").getAsString();
    String logoutUrl = response.get("logout_url").getAsString();
    String email = response.get("email").getAsString();
    String isValidUser = response.get("isValidUser").getAsString();

    Assert.assertTrue(loginUrl.contains("login"));
    Assert.assertTrue(logoutUrl.contains("logout"));
    Assert.assertTrue(email.equals("animachaidze@gmail.com"));
    Assert.assertTrue(isValidUser.equals("true"));
  }

  @Test
  public void testLogedOutUser() throws ServletException, IOException {
    helper.setEnvIsLoggedIn(false);

    JsonObject response = getLoginServletResponse();
    String loginUrl = response.get("login_url").getAsString();
    String logoutUrl = response.get("logout_url").getAsString();
    String email = response.get("email").getAsString();
    String isValidUser = response.get("isValidUser").getAsString();

    Assert.assertTrue(loginUrl.contains("login"));
    Assert.assertTrue(logoutUrl.contains("logout"));
    Assert.assertTrue(email.isEmpty());
    Assert.assertTrue(isValidUser.equals("false"));
  }

  /** Sets up mock returns and gets response json object */
  private JsonObject getLoginServletResponse() throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(request.getParameter("user-type")).thenReturn("Reviewee");
    when(response.getWriter()).thenReturn(printWriter);

    loginServlet.doPost(request, response);
    loginServlet.doGet(request, response);

    String responseStr = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(responseStr);
    JsonObject responseJson = responseJsonElement.getAsJsonObject();

    return responseJson;
  }
}
