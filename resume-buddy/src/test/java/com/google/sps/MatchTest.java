package com.google.sps;

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
import com.google.sps.data.Career;
import com.google.sps.data.Degree;
import com.google.sps.data.NumYears;
import com.google.sps.data.Pair;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
  private Entity revieweeA;
  private Entity revieweeB;
  private Entity revieweeC;
  private Entity reviewerD;
  private Entity reviewerE;
  private Entity reviewerF;
  private Entity reviewerG;
  private Entity userA;
  private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
  private static final Date FOUR_DAYS_AGO = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));
  private static final Date ONE_DAY_AGO = new Date(System.currentTimeMillis() - (1 * DAY_IN_MS));

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    revieweeA = new Entity("Reviewee");
    revieweeB = new Entity("Reviewee");
    revieweeC = new Entity("Reviewee");
    reviewerD = new Entity("Reviewer");
    reviewerE = new Entity("Reviewer");
    reviewerF = new Entity("Reviewer");
    reviewerG = new Entity("Reviewer");
    userA = new Entity("User");

    userA.setProperty("email", "user@gmail.com");
    userA.setProperty("matchID", "");

    revieweeA.setProperty("email", "A");
    revieweeA.setProperty("school", "Penn");
    revieweeA.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    revieweeA.setProperty("preferred-degree", Degree.BACHELOR.toString());
    revieweeA.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    revieweeB.setProperty("email", "B");
    revieweeB.setProperty("school", "Cornell");
    revieweeB.setProperty("career", Career.ENGINEERING.toString());
    revieweeB.setProperty("preferred-degree", Degree.MASTER.toString());
    revieweeB.setProperty("preferred-experience", NumYears.NO_PREFERENCE.toString());
    revieweeB.setProperty("submit-date", FOUR_DAYS_AGO);

    revieweeC.setProperty("email", "C");
    revieweeC.setProperty("school", "RPI");
    revieweeC.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    revieweeC.setProperty("preferred-degree", Degree.DOCTORATE.toString());
    revieweeC.setProperty("preferred-experience", NumYears.GREATER_THAN_10.toString());

    reviewerD.setProperty("email", "D");
    reviewerD.setProperty("school", "A&T");
    reviewerD.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    reviewerD.setProperty("degree", Degree.BACHELOR.toString());
    reviewerD.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());

    reviewerE.setProperty("email", "E");
    reviewerE.setProperty("school", "Penn");
    reviewerE.setProperty("career", Career.COMPUTER_SCIENCE.toString());
    reviewerE.setProperty("degree", Degree.BACHELOR.toString());
    reviewerE.setProperty("preferred-experience", NumYears.GREATER_THAN_5.toString());

    reviewerF.setProperty("email", "F");
    reviewerF.setProperty("school", "RPI");
    reviewerF.setProperty("career", Career.BUSINESS.toString());
    reviewerF.setProperty("degree", Degree.MASTER.toString());
    reviewerF.setProperty("preferred-experience", NumYears.LESS_THAN_5.toString());
    reviewerF.setProperty("submit-date", FOUR_DAYS_AGO);

    reviewerG.setProperty("email", "G");
    reviewerG.setProperty("school", "A&T");
    reviewerG.setProperty("career", Career.HEALTHCARE.toString());
    reviewerG.setProperty("degree", Degree.MASTER.toString());
    reviewerG.setProperty("preferred-experience", NumYears.GREATER_THAN_10.toString());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void findsBestMatches() throws ParseException {
    datastore.put(revieweeA);
    datastore.put(revieweeB);
    datastore.put(revieweeC);
    datastore.put(reviewerD);
    datastore.put(reviewerE);
    datastore.put(reviewerF);

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
    datastore.put(revieweeA);
    datastore.put(revieweeB);
    datastore.put(revieweeC);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  @Test
  public void moreReviewees() {
    datastore.put(revieweeA);
    datastore.put(revieweeB);
    datastore.put(revieweeC);
    datastore.put(reviewerD);
    datastore.put(reviewerE);

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
    revieweeA.setProperty("submit-date", FOUR_DAYS_AGO);
    reviewerG.setProperty("submit-date", new Date());

    datastore.put(revieweeA);
    datastore.put(reviewerG);

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
    revieweeA.setProperty("submit-date", ONE_DAY_AGO);
    reviewerG.setProperty("submit-date", new Date());

    datastore.put(revieweeA);
    datastore.put(reviewerG);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();

    Assert.assertEquals(expectedMatches, actualMatches);
  }

  @Test
  public void testUpdateUserID() throws ParseException {
    datastore.put(userA);
    UUID id = UUID.randomUUID();
    Match.updateUserMatchID(id, "user@gmail.com");
    Query query = new Query("User");
    Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, "user@gmail.com");
    query.setFilter(emailFilter);
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    Assert.assertEquals(id.toString(), userEntity.getProperty("matchID"));
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
    // sendingEmailsWhenMatched();
  }

  private void sendingEmailsWhenMatched() {
    revieweeA.setProperty("email", "animachaidze@gmail.com");
    reviewerE.setProperty("email", "animach@google.com");

    datastore.put(revieweeA);
    datastore.put(reviewerE);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = getActualMatches();
    expectedMatches.add(new Pair("animachaidze@gmail.com", "animach@google.com"));

    Assert.assertEquals(expectedMatches, actualMatches);
  }
}
