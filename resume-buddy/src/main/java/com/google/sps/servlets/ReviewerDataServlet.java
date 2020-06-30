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
    String degree = getParameter(request, "education-level", "");
    String school = getParameter(request, "school", "");
    String career = getParameter(request, "work-field", "");
    if (career.equals("Other")) {
        career = getParameter(request, "other", "");
    }
    String company = getParameter(request, "company", "");
    String numYears = getParameter(request, "years-experience", "");
    reviewer = new Reviewer(fname, lname, email, degree, school, career, company, numYears);
    Entity reviewerEntity = new Entity("Reviewer");
    reviewerEntity.setProperty("first-name", fname);
    reviewerEntity.setProperty("last-name", lname);
    reviewerEntity.setProperty("email", email);
    reviewerEntity.setProperty("degree", degree);
    reviewerEntity.setProperty("school", school);
    reviewerEntity.setProperty("career", career);
    reviewerEntity.setProperty("company", company);
    reviewerEntity.setProperty("numYears", numYears);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reviewerEntity);

    response.sendRedirect("resume-review.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   * was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null) ? defaultValue : value;
  }
}
