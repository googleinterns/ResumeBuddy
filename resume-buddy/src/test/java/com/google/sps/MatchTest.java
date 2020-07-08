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
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for matching algorithm */
@RunWith(JUnit4.class)
public class MatchTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void findsBestMatches() {

    final Entity REVIEWEE_A = new Entity("Reviewee");
    final Entity REVIEWEE_B = new Entity("Reviewee");
    final Entity REVIEWEE_C = new Entity("Reviewee");
    final Entity REVIEWER_D = new Entity("Reviewer");
    final Entity REVIEWER_F = new Entity("Reviewer");
    final Entity REVIEWER_E = new Entity("Reviewer");

    datastore = DatastoreServiceFactory.getDatastoreService();

    REVIEWEE_A.setProperty("email", "A");
    REVIEWEE_A.setProperty("school", "Penn");
    REVIEWEE_A.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    REVIEWEE_A.setProperty("preferred-degree", Degree.BACHELOR.toString());
    REVIEWEE_A.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    REVIEWEE_B.setProperty("email", "B");
    REVIEWEE_B.setProperty("school", "Cornell");
    REVIEWEE_B.setProperty("career", Career.ENGINEERING.toString());
    REVIEWEE_B.setProperty("preferred-degree", Degree.MASTER.toString());
    REVIEWEE_B.setProperty("preferred-experience", NumYears.NO_PREFERENCE.toString());

    REVIEWEE_C.setProperty("email", "C");
    REVIEWEE_C.setProperty("school", "RPI");
    REVIEWEE_C.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    REVIEWEE_C.setProperty("preferred-degree", Degree.DOCTORATE.toString());
    REVIEWEE_C.setProperty("preferred-experience", NumYears.GREATER_THAN_10.toString());

    REVIEWER_D.setProperty("email", "D");
    REVIEWER_D.setProperty("school", "A&T");
    REVIEWER_D.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    REVIEWER_D.setProperty("degree", Degree.BACHELOR.toString());
    REVIEWER_D.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    REVIEWER_E.setProperty("email", "E");
    REVIEWER_E.setProperty("school", "Penn");
    REVIEWER_E.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    REVIEWER_E.setProperty("degree", Degree.BACHELOR.toString());
    REVIEWER_E.setProperty("preferred-experience", NumYears.GREATER_THAN_5.toString());

    REVIEWER_F.setProperty("email", "F");
    REVIEWER_F.setProperty("school", "RPI");
    REVIEWER_F.setProperty("career", Career.BUSINESS.toString());
    REVIEWER_F.setProperty("degree", Degree.MASTER.toString());
    REVIEWER_F.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWEE_B);
    datastore.put(REVIEWEE_C);
    datastore.put(REVIEWER_D);
    datastore.put(REVIEWER_E);
    datastore.put(REVIEWER_F);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = new ArrayList<>();

    expectedMatches.add(new Pair("A", "E"));
    expectedMatches.add(new Pair("C", "D"));
    expectedMatches.add(new Pair("B", "F"));

    Query query = new Query("Match");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {

    Assert.assertEquals(expectedMatches, actualMatches);
  }
}
