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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.ServletHelpers;
import com.google.sps.data.Reviewer;
import com.google.sps.data.User;
import java.io.IOException;
import java.util.Date;
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
    UserService userService = UserServiceFactory.getUserService();
    String reviewerEmail = userService.getCurrentUser().getEmail();

    Query query = new Query("User");
    Filter reviewerFilter = new FilterPredicate("reviewer", FilterOperator.EQUAL, reviewerEmail);
    query.setFilter(reviewerFilter);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    User user = null;
    if (results.countEntities(FetchOptions.Builder.withLimit(1)) == 0) {
      user = null;
    } else {

      Entity userEntity = results.asSingleEntity();
      String fname = (String) userEntity.getProperty("first-name");
      String lname = (String) userEntity.getProperty("last-name");
      String email = (String) userEntity.getProperty("email");
      String school = (String) userEntity.getProperty("school");
      String career = (String) userEntity.getProperty("career");
      String degree = (String) userEntity.getProperty("education-level");

      user = new User(fname, lname, email, school, career, degree);
    }

    // Send the JSON as the response
    response.setContentType("application/json");
    String json = new Gson().toJson(user);
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
    String career = ServletHelpers.getParameter(request, "career", "");
    String company = ServletHelpers.getParameter(request, "company", "");
    String numYears = ServletHelpers.getParameter(request, "years-experience", "");
    reviewer = new Reviewer(fname, lname, email, degree, school, career, company, numYears);

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
    reviewerEntity.setProperty("education-level", degree);
    reviewerEntity.setProperty("school", school);
    reviewerEntity.setProperty("career", career);
    reviewerEntity.setProperty("company", company);
    reviewerEntity.setProperty("years-experience", numYears);
    reviewerEntity.setProperty("submit-date", new Date());

    Entity userEntity = new Entity("User");
    userEntity.setProperty("first-name", fname);
    userEntity.setProperty("last-name", lname);
    userEntity.setProperty("email", email);
    userEntity.setProperty("degree", degree);
    userEntity.setProperty("school", school);
    userEntity.setProperty("career", career);
    userEntity.setProperty("company", company);
    userEntity.setProperty("years-experience", numYears);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reviewerEntity);
    datastore.put(userEntity);

    response.sendRedirect("/index.html");
  }
}
