package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.ServletHelpers;
import com.google.sps.data.Reviewee;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/reviewee-data")
public class RevieweeDataServlet extends HttpServlet {

  private Reviewee reviewee;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Send the JSON as the response
    response.setContentType("application/json");
    String json = new Gson().toJson(reviewee);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.

    String fname = ServletHelpers.getParameter(request, "fname", "");
    String lname = ServletHelpers.getParameter(request, "lname", "");
    String email = ServletHelpers.getParameter(request, "email", "");
    String school = ServletHelpers.getParameter(request, "school", "");
    String year = ServletHelpers.getParameter(request, "school-year", "");
    String career = ServletHelpers.getParameter(request, "career", "");
    String degreePref = ServletHelpers.getParameter(request, "degree-preference", "");
    String numYearsPref = ServletHelpers.getParameter(request, "experience-preference", "");

    reviewee = new Reviewee(fname, lname, email, school, year, career, degreePref, numYearsPref);

    if (year.equals("other")) {
      year = ServletHelpers.getParameter(request, "other_year", "");
    }
    if (career.equals("other")) {
      career = ServletHelpers.getParameter(request, "other_career", "");
    }
    Entity revieweeEntity = new Entity("Reviewee");
    revieweeEntity.setProperty("first-name", fname);
    revieweeEntity.setProperty("last-name", lname);
    revieweeEntity.setProperty("email", email);
    revieweeEntity.setProperty("school-year", year);
    revieweeEntity.setProperty("school", school);
    revieweeEntity.setProperty("career", career);
    revieweeEntity.setProperty("preferred-degree", degreePref);
    revieweeEntity.setProperty("preferred-experience", numYearsPref);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(revieweeEntity);

    response.sendRedirect("resume-review.html");
  }
}
