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
import com.google.sps.data.Degree;
import com.google.sps.data.SchoolYear;
import com.google.sps.servlets.UserDataServlet;
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

/** Tests for UserDataServlet */
@RunWith(JUnit4.class)
public class UserDataServletTest {

  private static final String NEW_USER_EMAIL = "animachaidze@gmail.com";
  private static final String OLD_USER_EMAIL = "animach@google.com";
  private Entity newUser;
  private Entity oldUser;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private UserDataServlet userDataServlet;
  private DatastoreService datastore;

  private LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
          new LocalUserServiceTestConfig(),
          new LocalURLFetchServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    userDataServlet = new UserDataServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();

    newUser = new Entity("User");
    newUser.setProperty("email", NEW_USER_EMAIL);
    datastore.put(newUser);

    oldUser = new Entity("User");
    oldUser.setProperty("email", OLD_USER_EMAIL);
    oldUser.setProperty("first-name", "Ani");
    oldUser.setProperty("last-name", "Machaidze");
    oldUser.setProperty("school", "University of Pennsylvania");
    oldUser.setProperty("career", "Software Engineer");
    oldUser.setProperty("degree", "Bachelor");
    datastore.put(oldUser);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Test for user who has just signed up and we only know their email */
  @Test
  public void testNewUser() throws ServletException, IOException {
    helper.setEnvEmail(NEW_USER_EMAIL).setEnvAuthDomain("gmail.com").setEnvIsLoggedIn(true);
    JsonObject response = getServletResponse();

    Object fname = response.get("firstName");
    Object lname = response.get("lastName");
    String email = response.get("email").getAsString();
    Object school = response.get("school");
    Object career = response.get("career");
    Degree degree = Degree.valueOf(response.get("degree").getAsString());
    SchoolYear schoolYear = SchoolYear.valueOf(response.get("schoolYear").getAsString());

    Assert.assertTrue(fname == null);
    Assert.assertTrue(lname == null);
    Assert.assertTrue(email.equals("animachaidze@gmail.com"));
    Assert.assertTrue(school == null);
    Assert.assertTrue(career == null);
    Assert.assertTrue(degree.equals(Degree.OTHER));
    Assert.assertTrue(schoolYear.equals(SchoolYear.OTHER));
  }

  /** test user who has filled out some form and we know more information */
  @Test
  public void testOldUser() throws ServletException, IOException {
    helper.setEnvEmail(OLD_USER_EMAIL).setEnvAuthDomain("google.com").setEnvIsLoggedIn(true);
    JsonObject response = getServletResponse();

    String fname = response.get("firstName").getAsString();
    String lname = response.get("lastName").getAsString();
    String email = response.get("email").getAsString();
    String school = response.get("school").getAsString();
    String career = response.get("career").getAsString();
    Degree degree = Degree.valueOf(response.get("degree").getAsString());
    SchoolYear schoolYear = SchoolYear.valueOf(response.get("schoolYear").getAsString());

    Assert.assertTrue(fname.equals("Ani"));
    Assert.assertTrue(lname.equals("Machaidze"));
    Assert.assertTrue(email.equals("animach@google.com"));
    Assert.assertTrue(school.equals("University of Pennsylvania"));
    Assert.assertTrue(career.equals("Software Engineer"));
    Assert.assertTrue(degree.equals(Degree.BACHELOR));
    Assert.assertTrue(schoolYear.equals(SchoolYear.OTHER));
  }

  /** test getting user information given their emal */
  @Test
  public void testRequestWithEmailParameter() throws ServletException, IOException {
    when(request.getParameter("email")).thenReturn(OLD_USER_EMAIL);
    JsonObject response = getServletResponse();

    String fname = response.get("firstName").getAsString();
    String lname = response.get("lastName").getAsString();
    String email = response.get("email").getAsString();
    String school = response.get("school").getAsString();
    String career = response.get("career").getAsString();
    Degree degree = Degree.valueOf(response.get("degree").getAsString());
    SchoolYear schoolYear = SchoolYear.valueOf(response.get("schoolYear").getAsString());

    Assert.assertTrue(fname.equals("Ani"));
    Assert.assertTrue(lname.equals("Machaidze"));
    Assert.assertTrue(email.equals("animach@google.com"));
    Assert.assertTrue(school.equals("University of Pennsylvania"));
    Assert.assertTrue(career.equals("Software Engineer"));
    Assert.assertTrue(degree.equals(Degree.BACHELOR));
    Assert.assertTrue(schoolYear.equals(SchoolYear.OTHER));
  }

  /** Sets up mock returns and gets response json object */
  private JsonObject getServletResponse() throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    userDataServlet.doGet(request, response);

    String responseStr = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(responseStr);
    JsonObject responseJson = responseJsonElement.getAsJsonObject();

    return responseJson;
  }
}
