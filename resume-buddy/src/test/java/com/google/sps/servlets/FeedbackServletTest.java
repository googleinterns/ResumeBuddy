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
import com.google.sps.servlets.FeedbackServlet;
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

@RunWith(JUnit4.class)
public class FeedbackServletTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private FeedbackServlet feedbackServlet;
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
    feedbackServlet = new FeedbackServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void test() throws ServletException, IOException {
    when(ServletHelpers.getParameter(request, "user-type", "")).thenReturn("Tester");
    when(ServletHelpers.getParameter(request, "message", "")).thenReturn("random feedback");

    feedbackServlet.doPost(request, response);

    // check the database
    Query query = new Query("Feedback");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter userTypeFilter = new FilterPredicate("userType", FilterOperator.EQUAL, "Tester");
    query.setFilter(userTypeFilter);
    PreparedQuery results = datastore.prepare(query);
    Entity feedbackEntity = results.asSingleEntity();

    Assert.assertTrue(feedbackEntity.getProperty("userType").equals("Tester"));
    Assert.assertTrue(feedbackEntity.getProperty("feedback").equals("random feedback"));
  }
}
