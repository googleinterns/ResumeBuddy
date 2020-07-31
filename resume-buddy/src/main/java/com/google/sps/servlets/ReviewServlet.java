package com.google.sps.servlets;

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
import com.google.sps.api.Email;
import com.google.sps.data.EmailTemplates;
import com.google.sps.data.ReviewStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servelt that updates reviewing status */
@WebServlet("/review-page")
public class ReviewServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String matchId = request.getParameter("matchId");
    Query query = new Query("Match");
    Filter matchIdFilter = new FilterPredicate("uuid", FilterOperator.EQUAL, matchId);
    query.setFilter(matchIdFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    String reviewer = (String) entity.getProperty("reviewer");
    String reviewee = (String) entity.getProperty("reviewee");

    String jsonMatch =
        "{ \"reviewer\": \"" + reviewer + "\", " + "\"reviewee\": \"" + reviewee + "\" }";

    response.setContentType("application/json");
    response.getWriter().println(jsonMatch);
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String reviewerEmail = userService.getCurrentUser().getEmail();

    Query query = new Query("Match");
    Filter reviewerFilter = new FilterPredicate("reviewer", FilterOperator.EQUAL, reviewerEmail);
    query.setFilter(reviewerFilter);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Currently, we assume that reviewer only has one reviewee
    Entity entity = results.asSingleEntity();
    entity.setProperty("status", ReviewStatus.REVIEW_DONE.toString());
    datastore.put(entity);
    String resumeFileName = (String) entity.getProperty("resumeFileName");
    String revieweeEmail = (String) entity.getProperty("reviewee");
    String matchBlobKeyString = (String) entity.getProperty("resumeBlobKey");

    // Creates File object from blobKey
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    BlobKey matchBlobKey = new BlobKey(matchBlobKeyString);
    byte[] resumeFileBytes =
        blobstoreService.fetchData(matchBlobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE - 1);
    File file = new File(resumeFileName);
    OutputStream os = new FileOutputStream(file);
    os.write(resumeFileBytes);
    os.close();

    Email.sendEmail(
        revieweeEmail,
        EmailTemplates.RESUME_REVIEWED_SUBJECT_LINE,
        EmailTemplates.RESUME_REVIEWED_BODY,
        file);

    response.sendRedirect("/index.html");
  }
}
