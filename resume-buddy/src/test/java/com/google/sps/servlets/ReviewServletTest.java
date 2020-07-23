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
import com.google.sps.servlets.ReviewServlet;
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

/** Tests for ReviewServlet */
@RunWith(JUnit4.class)
public class ReviewServletTest {

  private static final String REVIEWER_EMAIL = "animachaidze@gmail.com";
  private static final String REVIEWEE_EMAIL = "animach@google.com";
  private Entity match;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private ReviewServlet reviewServlet;
  private DatastoreService datastore;
  private static final String ID = UUID.randomUUID().toString();

  private LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
          new LocalUserServiceTestConfig(),
          new LocalURLFetchServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    reviewServlet = new ReviewServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();

    match = new Entity("Match");
    match.setProperty("reviewer", REVIEWER_EMAIL);
    match.setProperty("reviewee", REVIEWEE_EMAIL);
    match.setProperty("uuid", ID);
    datastore.put(match);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** Tests getting reviewer and reviewee emails using matchId from ReviewServlet*/
  @Test
  public void testGettingMatchUsersUsingId() throws ServletException, IOException {
    when(request.getParameter("matchId")).thenReturn(ID);
    JsonObject response = getServletResponse();

    String reviewer = response.get("reviewer").getAsString();
    String reviewee = response.get("reviewee").getAsString();

    Assert.assertTrue(reviewer.equals(REVIEWER_EMAIL));
    Assert.assertTrue(reviewee.equals(REVIEWEE_EMAIL));
  }

  /** Sets up mock returns and gets response json object */
  private JsonObject getServletResponse() throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    reviewServlet.doGet(request, response);

    String responseStr = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(responseStr);
    JsonObject responseJson = responseJsonElement.getAsJsonObject();

    return responseJson;
  }
}
