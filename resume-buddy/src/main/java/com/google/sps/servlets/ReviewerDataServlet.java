package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.ServletHelpers;
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

    String fname = ServletHelpers.getParameter(request, "fname", "");
    String lname = ServletHelpers.getParameter(request, "lname", "");
    String email = ServletHelpers.getParameter(request, "email", "");
    String degree = ServletHelpers.getParameter(request, "education-level", "");
    String school = ServletHelpers.getParameter(request, "school", "");
    String career = ServletHelpers.getParameter(request, "work-field", "");

    String company = ServletHelpers.getParameter(request, "company", "");
    String numYears = ServletHelpers.getParameter(request, "years-experience", "");
    reviewer = new Reviewer(fname, lname, email, degree, school, career, company, numYears);

    if (career.equals("other")) {
      career = ServletHelpers.getParameter(request, "other", "");
    }
    Entity reviewerEntity = new Entity("Reviewer");
    reviewerEntity.setProperty("first-name", fname);
    reviewerEntity.setProperty("last-name", lname);
    reviewerEntity.setProperty("email", email);
    reviewerEntity.setProperty("degree", degree);
    reviewerEntity.setProperty("school", school);
    reviewerEntity.setProperty("career", career);
    reviewerEntity.setProperty("company", company);
    reviewerEntity.setProperty("years-experience", numYears);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reviewerEntity);

    response.sendRedirect("/index.html");
  }
}
