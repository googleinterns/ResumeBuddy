package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Career;
import com.google.sps.data.Degree;
import com.google.sps.data.NumYears;
import com.google.sps.data.Pair;
import com.google.sps.servlets.MatchServlet;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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

/** Tests for matching algorithm */
@RunWith(JUnit4.class)
public class MatchServletTest {

  private Entity revieweeA;
  private Entity reviewerE;
  private DatastoreService datastore;
  private MatchServlet matchServlet;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    matchServlet = new MatchServlet();
    MockitoAnnotations.initMocks(this);

    revieweeA = new Entity("Reviewee");
    reviewerE = new Entity("Reviewer");

    revieweeA.setProperty("email", "A");
    revieweeA.setProperty("school", "Penn");
    revieweeA.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    revieweeA.setProperty("preferred-degree", Degree.BACHELOR.toString());
    revieweeA.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    reviewerE.setProperty("email", "E");
    reviewerE.setProperty("school", "Penn");
    reviewerE.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    reviewerE.setProperty("degree", Degree.BACHELOR.toString());
    reviewerE.setProperty("preferred-experience", NumYears.GREATER_THAN_5.toString());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void callMatchAlgFromServlet() throws ParseException, IOException {
    datastore.put(revieweeA);
    datastore.put(reviewerE);

    matchServlet.doGet(request, response);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    expectedMatches.add(new Pair("A", "E"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  private List<Pair<String, String>> getActualMatches() {
    List<Pair<String, String>> actualMatches = new ArrayList<>();

    Query query = new Query("Match");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      actualMatches.add(new Pair(entity.getProperty("reviewee"), entity.getProperty("reviewer")));
    }

    return actualMatches;
  }
}
