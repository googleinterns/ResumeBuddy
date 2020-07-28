package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.servlets.ReviewerDataServlet;
import java.io.IOException;
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
public class ReviewerDataServletTest {
  private Entity newUser;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private ReviewerDataServlet reviewerDataServlet;
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
    datastore = DatastoreServiceFactory.getDatastoreService();
    newUser = new Entity("User");
    newUser.setProperty("email", "shreyabarua@google.com");
    datastore.put(newUser);
    reviewerDataServlet = new ReviewerDataServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** test reviewer who has filled out sign up form with all their information */
  @Test
  public void test() throws ServletException, IOException {
    helper
        .setEnvEmail("shreyabarua@google.com")
        .setEnvAuthDomain("google.com")
        .setEnvIsLoggedIn(true);
    when(ServletHelpers.getParameter(request, "fname", "")).thenReturn("Shreya");
    when(ServletHelpers.getParameter(request, "lname", "")).thenReturn("Barua");
    when(ServletHelpers.getParameter(request, "email", "")).thenReturn("shreyabarua2@gmail.com");
    when(ServletHelpers.getParameter(request, "school", "")).thenReturn("RPI");
    when(ServletHelpers.getParameter(request, "company", "")).thenReturn("Google");
    when(ServletHelpers.getParameter(request, "career", ""))
        .thenReturn("Computer Software/Engineering");
    when(ServletHelpers.getParameter(request, "degree", "")).thenReturn("bachelor");
    when(ServletHelpers.getParameter(request, "years-experience", "")).thenReturn("less_than_5");

    reviewerDataServlet.doPost(request, response);

    // check the database
    Query query = new Query("Reviewer");
    Filter emailFilter =
        new FilterPredicate("email", FilterOperator.EQUAL, "shreyabarua@google.com");
    query.setFilter(emailFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Entity reviewerEntity = results.asSingleEntity();

    Assert.assertTrue(reviewerEntity.getProperty("first-name").equals("Shreya"));
    Assert.assertTrue(reviewerEntity.getProperty("last-name").equals("Barua"));
    Assert.assertTrue(reviewerEntity.getProperty("email").equals("shreyabarua@google.com"));
    Assert.assertTrue(reviewerEntity.getProperty("school").equals("RPI"));
    Assert.assertTrue(reviewerEntity.getProperty("career").equals("Computer Software/Engineering"));
    Assert.assertTrue(reviewerEntity.getProperty("company").equals("Google"));
    Assert.assertTrue(reviewerEntity.getProperty("degree").equals("bachelor"));
    Assert.assertTrue(reviewerEntity.getProperty("years-experience").equals("less_than_5"));

    query = new Query("User");
    emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, "shreyabarua@google.com");
    query.setFilter(emailFilter);
    results = datastore.prepare(query);
    newUser = results.asSingleEntity();

    // check if the corresponding user entity has been updated as well
    Assert.assertTrue(newUser.getProperty("first-name").equals("Shreya"));
    Assert.assertTrue(newUser.getProperty("last-name").equals("Barua"));
    Assert.assertTrue(newUser.getProperty("email").equals("shreyabarua@google.com"));
    Assert.assertTrue(newUser.getProperty("school").equals("RPI"));
    Assert.assertTrue(newUser.getProperty("career").equals("Computer Software/Engineering"));
    Assert.assertTrue(newUser.getProperty("degree").equals("bachelor"));
    Assert.assertTrue(newUser.getProperty("isReviewer").equals(true));
  }
}
