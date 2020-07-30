package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that deletes Match data */
@WebServlet("/delete-match-data")
public class DeleteMatchDataServlet extends HttpServlet {

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String revieweeEmail = userService.getCurrentUser().getEmail();

    Query query = new Query("Match");
    Filter revieweeFilter = new FilterPredicate("reviewee", FilterOperator.EQUAL, revieweeEmail);
    query.setFilter(revieweeFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Entity entity = results.asSingleEntity();
    String resumeBlobKey = (String) entity.getProperty("resumeBlobKey");
    String matchID = (String) entity.getProperty("uuid");

    // Deletes Match entity
    datastore.delete(entity.getKey());

    // Deletes matchID from user entities
    query = new Query("User");
    Filter matchIDFilter = new FilterPredicate("matchID", FilterOperator.EQUAL, matchID);
    query.setFilter(matchIDFilter);
    PreparedQuery usersResult = datastore.prepare(query);
    for (Entity user : usersResult.asIterable()) {
      user.setProperty("matchID", "");
      datastore.put(user);
    }

    // Deletes all comments written on match's review page
    query = new Query("Review-comments");
    query.setFilter(matchIDFilter);
    PreparedQuery commentsResult = datastore.prepare(query);
    List<Key> commentKeys = new ArrayList<>();
    for (Entity commentEntity : commentsResult.asIterable()) {
      commentKeys.add(commentEntity.getKey());
    }
    datastore.delete(commentKeys);

    // Deletes blob
    int status = deleteBlob(resumeBlobKey);

    response.setStatus(status);
    response.sendRedirect("/index.html");
  }

  public int deleteBlob(String resumeBlobKey) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    boolean isBlobDeleted = false;

    BlobKey blobKey = new BlobKey(resumeBlobKey);
    blobstoreService.delete(blobKey);

    BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
    isBlobDeleted = blobInfoFactory.loadBlobInfo(blobKey) == null;

    if (isBlobDeleted) {
      return 200; // 200: OK - The request is OK
    } else {
      return 400; // 400: Bad Request - The request cannot be fulfilled
    }
  }
}
