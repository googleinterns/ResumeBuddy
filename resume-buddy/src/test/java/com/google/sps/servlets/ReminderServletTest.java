package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.ReviewStatus;
import com.google.sps.servlets.ReminderServlet;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for Reminder Servlet */
@RunWith(JUnit4.class)
public class ReminderServletTest {

  private Entity match;
  private DatastoreService datastore;
  private ReminderServlet reminderServlet;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
  private static final Date THREE_DAYS_AGO = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));
  private static final Date ONE_DAY_AGO = new Date(System.currentTimeMillis() - (1 * DAY_IN_MS));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    reminderServlet = new ReminderServlet();
    MockitoAnnotations.initMocks(this);

    match = new Entity("Match");
    match.setProperty("reviewee", "animach@google.com");
    match.setProperty("reviewer", "animachaidze@gmail.com");
    match.setProperty("status", ReviewStatus.IN_PROCESS.toString());
    match.setProperty("matchDate", THREE_DAYS_AGO);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void reminderServletSendsEmail() throws ParseException, IOException {
    match.setProperty("matchDate", THREE_DAYS_AGO);
    datastore.put(match);
    // Uncomment to test sending email
    // reminderServlet.doGet(request, response);
  }

  @Test
  public void reminderServletDoesNotSendEmail() throws ParseException, IOException {
    match.setProperty("matchDate", ONE_DAY_AGO);
    datastore.put(match);
    reminderServlet.doGet(request, response);
  }
}
