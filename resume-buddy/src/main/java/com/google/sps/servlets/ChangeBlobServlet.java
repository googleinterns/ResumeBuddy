package com.google.sps.servlets;

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
import com.google.sps.ServletHelpers;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/change-blob")
public class ChangeBlobServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String bolbKey = ServletHelpers.getBlobstoreKey(request, response, "resume");
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    Query query = new Query("Match");
    Filter userFilter = new FilterPredicate("reviewer", FilterOperator.EQUAL, email);
    query.setFilter(userFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Entity matchEntity = results.asSingleEntity();
    String oldBlobKey = (String) matchEntity.getProperty("resumeBlobKey");
    ServletHelpers.deleteBlob(oldBlobKey);
    matchEntity.setProperty("resumeBlobKey", bolbKey);
    datastore.put(matchEntity);

    response.setContentType("text/html");
    response.getWriter().println(bolbKey);
  }
}
