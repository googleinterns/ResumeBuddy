package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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
  public void matchesInOrder() {
    datastore = DatastoreServiceFactory.getDatastoreService();

    Entity reviewee1 = new Entity("Reviewee");
    Entity reviewee2 = new Entity("Reviewee");
    reviewee1.setProperty("email", "Ani");
    reviewee2.setProperty("email", "Olivia");

    Entity reviewer1 = new Entity("Reviewer");
    Entity reviewer2 = new Entity("Reviewer");
    reviewer1.setProperty("email", "Shreya");
    reviewer2.setProperty("email", "Shayla");

    datastore.put(reviewee1);
    datastore.put(reviewee2);
    datastore.put(reviewer1);
    datastore.put(reviewer2);

    List<Entity> reviewees = Match.getNotMatchedUsers("Reviewee");
    List<Entity> reviewers = Match.getNotMatchedUsers("Reviewer");

    Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    List<Pair<String, String>> actualMatches = new ArrayList<>();

    expectedMatches.add(new Pair("Ani", "Shreya"));
    expectedMatches.add(new Pair("Olivia", "Shayla"));

    Query query = new Query("Match");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      actualMatches.add(
          new Pair(
              (String) entity.getProperty("reviewee"), (String) entity.getProperty("reviewer")));
    }

    Assert.assertEquals(expectedMatches, actualMatches);
  }
}
