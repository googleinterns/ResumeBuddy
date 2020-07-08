package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.ServletHelpers;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {

  private List<Comment> comments;
  static final int DEFAULT_COMMENTS_NUMBER = 5;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    Query query = new Query("Review-comments").addSort("date", SortDirection.DESCENDING);
    Filter emailFilter = new FilterPredicate("reviewee", FilterOperator.EQUAL, email);
    query.setFilter(emailFilter);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    comments = new ArrayList<>();
    String reviewer, reviewee, type, text, id;
    Date date;
    for (Entity entity : results.asIterable()) {
      try {
        reviewer = (String) entity.getProperty("reviewer");
        reviewee = (String) entity.getProperty("reviewee");
        type = (String) entity.getProperty("type");
        text = (String) entity.getProperty("text");
        date = (Date) entity.getProperty("date");
        id = (String) entity.getProperty("uuid");
      } catch (ClassCastException e) {
        System.err.println("Could not cast entry property");
        break;
      }

      comments.add(new Comment(reviewer, reviewee, text, type, date, id));
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String type = ServletHelpers.getParameter(request, "type", "");
    String text = ServletHelpers.getParameter(request, "text", "");
    Date date = new Date();

    UserService userService = UserServiceFactory.getUserService();

    // TODO: Get real reviewer and reviewee info when auth implemented
    /*Currently signed in user is assumed to be Reviewee
    TODO: add option for user to sign in as either a reviewer or reviewee*/
    String reviewee = userService.getCurrentUser().getEmail();
    String reviewer = "";

    UUID id = UUID.randomUUID();
    while (collides(id)) {
      id = UUID.randomUUID();
    }

    Entity commentEntity = new Entity("Review-comments");
    commentEntity.setProperty("reviewer", reviewer);
    commentEntity.setProperty("reviewee", reviewee);
    commentEntity.setProperty("type", type);
    commentEntity.setProperty("text", text);
    commentEntity.setProperty("date", date);
    commentEntity.setProperty("uuid", id.toString());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/resume-review.html");
  }

  /** Checks if the id collides with other ids in datastore */
  private boolean collides(UUID id) {
    Query query = new Query("Review-comments");

    Filter uuidPropertyFilter = new FilterPredicate("uuid", FilterOperator.EQUAL, id.toString());
    query.setFilter(uuidPropertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    return results.countEntities(FetchOptions.Builder.withDefaults()) != 0;
  }
}
