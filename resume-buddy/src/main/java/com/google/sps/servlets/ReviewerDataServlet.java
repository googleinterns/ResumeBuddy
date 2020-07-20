package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.ServletHelpers;
import com.google.sps.data.Reviewer;
import java.io.IOException;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/reviewer-data")
public class ReviewerDataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.

    String fname = ServletHelpers.getParameter(request, "fname", "");
    String lname = ServletHelpers.getParameter(request, "lname", "");
    String email = ServletHelpers.getParameter(request, "email", "");
    String degree = ServletHelpers.getParameter(request, "degree", "");
    String school = ServletHelpers.getParameter(request, "school", "");
    String career = ServletHelpers.getParameter(request, "career", "");
    String company = ServletHelpers.getParameter(request, "company", "");
    String numYears = ServletHelpers.getParameter(request, "years-experience", "");
    Reviewer reviewer =
        new Reviewer(fname, lname, email, degree, school, career, company, numYears);

    if (school.equals("Other")) {
      school = ServletHelpers.getParameter(request, "other-school", "");
    }
    if (career.equals("Other")) {
      career = ServletHelpers.getParameter(request, "other-career", "");
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
    reviewerEntity.setProperty("submit-date", new Date());

    // Gets user entity from User db and updates fields
    Query query = new Query("User");
    Filter userFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    query.setFilter(userFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();

    userEntity.setProperty("first-name", fname);
    userEntity.setProperty("last-name", lname);
    userEntity.setProperty("email", email);
    userEntity.setProperty("degree", degree);
    userEntity.setProperty("school", school);
    userEntity.setProperty("career", career);
    userEntity.setProperty("isReviewer", true);

    datastore.put(reviewerEntity);
    datastore.put(userEntity);

    response.sendRedirect("/index.html");
  }
}
