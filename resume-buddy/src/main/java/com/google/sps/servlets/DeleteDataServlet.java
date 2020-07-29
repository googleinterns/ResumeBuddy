package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that deletes Match data */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String revieweeEmail = userService.getCurrentUser().getEmail();

    Query query = new Query("Match");
    Filter revieweeFilter = new FilterPredicate("reviewee", FilterOperator.EQUAL, revieweeEmail);
    query.setFilter(revieweeFilter);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Entity entity = results.asSingleEntity();
    String resumeBlobKey = (String) entity.getProperty("resumeBlobKey");
    int status = deleteBlob(resumeBlobKey);
    datastore.delete(entity.getKey());

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
