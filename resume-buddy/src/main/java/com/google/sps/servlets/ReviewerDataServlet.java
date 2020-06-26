package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.data.Reviewer;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/reviewer-data")
public class ReviewerDataServlet extends HttpServlet {

  private Reviewer reviewer;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Send the JSON as the response
    response.setContentType("application/json");
    String json = new Gson().toJson(reviewer);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String fname = getParameter(request, "fname", "");
    String lname = getParameter(request, "lname", "");
    String email = getParameter(request, "email", "");
    reviewer = new Reviewer(fname, lname, email);
    Entity reviewerEntity = new Entity("Reviewer");
    reviewerEntity.setProperty("first-name", fname);
    reviewerEntity.setProperty("last-name", lname);
    reviewerEntity.setProperty("email", email);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reviewerEntity);
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null) ? defaultValue : value;
  }
}
