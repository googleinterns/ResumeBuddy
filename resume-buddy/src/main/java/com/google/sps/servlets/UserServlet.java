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
import com.google.gson.Gson;
import com.google.sps.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets user data and fills out forms */
@WebServlet("/user-data")
public class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (userService.isUserLoggedIn()) {
      String email = userService.getCurrentUser().getEmail();

      email = "animach@google.com";
      Query query = new Query("User");
      Filter userFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
      query.setFilter(userFilter);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      User user = null;

      Entity userEntity = results.asSingleEntity();
      String fname = (String) userEntity.getProperty("first-name");
      String lname = (String) userEntity.getProperty("last-name");
      String school = (String) userEntity.getProperty("school");
      String career = (String) userEntity.getProperty("career");
      String degree = (String) userEntity.getProperty("education-level");
      if (degree.equals("")) {
        degree = "other";
      }
      String schoolYear = (String) userEntity.getProperty("school-year");
      if (degree.equals("")) {
        degree = "other";
      }

      user = new User(fname, lname, email, school, career, degree, schoolYear);
      System.out.println("aigoooo " + user);

      Gson gson = new Gson();
      // Send the JSON as the response
      response.setContentType("application/json");
      String json = gson.toJson(user);
      response.getWriter().println(json);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {}
}
