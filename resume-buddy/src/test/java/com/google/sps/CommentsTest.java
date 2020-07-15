package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Comment;
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
  private Entity MATCH;
  private Entity COMMENT_1;
  private Entity COMMENT_2;
  private Entity COMMENT_3;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  public void setUp() throws ParseException {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    MATCH = new Entity("Match");
    MATCH.setProperty("reviewee", "shreyabarua@google.com");
    MATCH.setProperty("reviewer", "sundar@google.com");

    COMMENT_1 = new Entity("Review-comments");
    COMMENT_1.setProperty("reviewer", "sundar@google.com");
    COMMENT_1.setProperty("reviewee", "shreyabarua@google.com");
    COMMENT_1.setProperty("text", "this is the first comment");

    COMMENT_2 = new Entity("Review-comments");
    COMMENT_2.setProperty("reviewer", "sundar@google.com");
    COMMENT_2.setProperty("reviewee", "shreyabarua@google.com");
    COMMENT_2.setProperty("text", "this is the second comment");

    COMMENT_3 = new Entity("Review-comments");
    COMMENT_3.setProperty("reviewer", "sundar@google.com");
    COMMENT_3.setProperty("reviewee", "shreyabarua@google.com");
    COMMENT_3.setProperty("text", "this is the third comment");
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getMatch1() throws ParseException {
    datastore.put(MATCH);
    String match = CommentServlet.getMatch("reviewee", "shreyabarua@google.com");
    Assert.assertEquals("sundar@google.com", match);
  }

  @Test
  public void getMatch2() throws ParseException {
    datastore.put(MATCH);
    String match = CommentServlet.getMatch("reviewer", "sundar@google.com");
    Assert.assertEquals("shreyabarua@google.com", match);
  }

  @Test
  public void getAllComments() throws ParseException {
    datastore.put(MATCH);
    datastore.put(COMMENT_1);
    datastore.put(COMMENT_2);
    datastore.put(COMMENT_3);
    List<String> expected = new ArrayList<>();
    expected.add("this is the first comment");
    expected.add("this is the second comment");
    expected.add("this is the third comment");
    List<Comment> comments = new ArrayList<>();

    int index = 0;
    CommentServlet.addComments("reviewee", "shreyabarua@google.com", comments);

    for (Comment c : comments) {
      Assert.assertEquals(c.getText(), expected.get(index));
      index++;
    }
  }
}
