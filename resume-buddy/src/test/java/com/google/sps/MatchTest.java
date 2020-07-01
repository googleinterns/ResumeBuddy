package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;src

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

    List<Entity> reviewees = Arrays.asList(reviewee1, reviewee2);
    List<Entity> reviewers = Arrays.asList(reviewer1, reviewer2);

    List<Pair<String, String>> actualMatches = Match.match(reviewees, reviewers);

    List<Pair<String, String>> expectedMatches = new ArrayList<>();
    expectedMatches.add(new Pair("Ani", "Shreya"));
    expectedMatches.add(new Pair("Olivia", "Shayla"));

    System.out.println(expectedMatches);
    Pair<String, String> test = new Pair("Ani", "Shreya");
    Pair<String, String> test2 = new Pair("Ani", "Shreya");

    Assert.assertEquals(expectedMatches, actualMatches);
  }
}
