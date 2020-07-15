package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Comment;
import com.google.sps.data.UserType;
import com.google.sps.servlets.CommentServlet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CommentsTest {
  private DatastoreService datastore;
  private Entity match;
  private Entity comment1;
  private Entity comment2;
  private Entity comment3;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    match = new Entity("Match");
    match.setProperty("reviewee", "shreyabarua@google.com");
    match.setProperty("reviewer", "sundar@google.com");

    comment1 = new Entity("Review-comments");
    comment1.setProperty("reviewer", "sundar@google.com");
    comment1.setProperty("reviewee", "shreyabarua@google.com");
    comment1.setProperty("text", "this is the first comment");

    comment2 = new Entity("Review-comments");
    comment2.setProperty("reviewer", "sundar@google.com");
    comment2.setProperty("reviewee", "shreyabarua@google.com");
    comment2.setProperty("text", "this is the second comment");

    comment3 = new Entity("Review-comments");
    comment3.setProperty("reviewer", "sundar@google.com");
    comment3.setProperty("reviewee", "shreyabarua@google.com");
    comment3.setProperty("text", "this is the third comment");
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getMatch1() throws ParseException {
    datastore.put(match);
    String matchUser = CommentServlet.getMatch(UserType.REVIEWEE, "shreyabarua@google.com");
    Assert.assertEquals("sundar@google.com", matchUser);
  }

  @Test
  public void getMatch2() throws ParseException {
    datastore.put(match);
    String matchUser = CommentServlet.getMatch(UserType.REVIEWER, "sundar@google.com");
    Assert.assertEquals("shreyabarua@google.com", matchUser);
  }

  @Test
  public void getAllComments() throws ParseException {
    datastore.put(match);
    datastore.put(comment1);
    datastore.put(comment2);
    datastore.put(comment3);
    List<String> expected = new ArrayList<>();
    expected.add("this is the first comment");
    expected.add("this is the second comment");
    expected.add("this is the third comment");
    List<Comment> comments = new ArrayList<>();

    int index = 0;
    CommentServlet.addComments(UserType.REVIEWEE, "shreyabarua@google.com", comments);

    for (Comment c : comments) {
      Assert.assertEquals(c.getText(), expected.get(index));
      index++;
    }
  }
}
