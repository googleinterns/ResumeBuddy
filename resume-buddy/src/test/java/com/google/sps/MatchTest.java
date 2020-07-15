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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

  private DatastoreService datastore;
  private Entity REVIEWEE_A;
  private Entity REVIEWEE_B;
  private Entity REVIEWEE_C;
  private Entity REVIEWER_D;
  private Entity REVIEWER_E;
  private Entity REVIEWER_F;
  private Entity REVIEWER_G;
  private final long DAY_IN_MS = 1000 * 60 * 60 * 24;
  private final Date FOUR_DAYS_AGO = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));
  private final Date ONE_DAY_AGO = new Date(System.currentTimeMillis() - (1 * DAY_IN_MS));

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    REVIEWEE_A = new Entity("Reviewee");
    REVIEWEE_B = new Entity("Reviewee");
    REVIEWEE_C = new Entity("Reviewee");
    REVIEWER_D = new Entity("Reviewer");
    REVIEWER_E = new Entity("Reviewer");
    REVIEWER_F = new Entity("Reviewer");
    REVIEWER_G = new Entity("Reviewer");

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
    REVIEWEE_B.setProperty("submit-date", FOUR_DAYS_AGO);

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
    REVIEWER_F.setProperty("submit-date", FOUR_DAYS_AGO);

    REVIEWER_G.setProperty("email", "G");
    REVIEWER_G.setProperty("school", "A&T");
    REVIEWER_G.setProperty("career", Career.HEALTHCARE.toString());
    REVIEWER_G.setProperty("degree", Degree.MASTER.toString());
    REVIEWER_G.setProperty("preferred-experience", NumYears.GREATER_THAN_10.toString());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void findsBestMatches() throws ParseException {
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
    List<Pair<String, String>> actualMatches = getActualMatches();

    expectedMatches.add(new Pair("A", "E"));
    expectedMatches.add(new Pair("C", "D"));
    expectedMatches.add(new Pair("B", "F"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  @Test
  public void noReviewers() {
    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWEE_B);
    datastore.put(REVIEWEE_C);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  @Test
  public void moreReviewees() {
    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWEE_B);
    datastore.put(REVIEWEE_C);
    datastore.put(REVIEWER_D);
    datastore.put(REVIEWER_E);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    expectedMatches.add(new Pair("A", "E"));
    expectedMatches.add(new Pair("C", "D"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  @Test
  public void noOneRegistered() {
    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  /** A and G don't have in common but A submitted 4 days ago so needs to be matched */
  @Test
  public void notAGoodMatchButTimeExpired() throws ParseException {
    REVIEWEE_A.setProperty("submit-date", FOUR_DAYS_AGO);
    REVIEWER_G.setProperty("submit-date", new Date());

    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWER_G);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();
    expectedMatches.add(new Pair("A", "G"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  /** A and G are not simmilar and they just submitted forms so they are not matched */
  @Test
  public void isNotMatchAndStillHaveTime() throws ParseException {
    REVIEWEE_A.setProperty("submit-date", ONE_DAY_AGO);
    REVIEWER_G.setProperty("submit-date", new Date());

    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWER_G);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  /** Gets matches from db and puts as a list of Pairs */
  private List<Pair<String, String>> getActualMatches() {
    List<Pair<String, String>> actualMatches = new ArrayList<>();

    Query query = new Query("Match");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      actualMatches.add(new Pair(entity.getProperty("reviewee"), entity.getProperty("reviewer")));
    }

    return actualMatches;
  }

  /** Test sending emails when they are matched */
  @Test
  public void testSendingEmailWhenMatched() throws ParseException {
    // Uncomment when needed to test. Otherwise, will send too much emails.
    //sendingEmailsWhenMatched();
  }

  private void sendingEmailsWhenMatched() {
    REVIEWEE_A.setProperty("email", "animachaidze@gmail.com");
    REVIEWER_E.setProperty("email", "animach@google.com");

    datastore.put(REVIEWEE_A);
    datastore.put(REVIEWER_E);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();
    expectedMatches.add(new Pair("animachaidze@gmail.com", "animach@google.com"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }
}
