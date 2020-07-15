// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.ServletHelpers;
import com.google.sps.data.UserType;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private static UserType userType;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    boolean isValidUser;
    String email = "";
    if (userService.isUserLoggedIn()) {
      email = userService.getCurrentUser().getEmail();
      isValidUser = validEmail(userType, email);
    } else {
      isValidUser = false;
    }
    String jsonLogin;
    String urlToRedirectToAfterUserLogsIn = "/index.html";
    String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
    String urlToRedirectToAfterUserLogsOut = "/index.html";
    String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

    jsonLogin = "{\"isValidUser\": " + String.valueOf(isValidUser) + ", ";
    jsonLogin += "\"login_url\": \"" + loginUrl + "\", ";
    jsonLogin += "\"logout_url\": \"" + logoutUrl + "\", ";
    jsonLogin += "\"email\": \"" + email + "\"}";

    // send the json as the response
    response.setContentType("application/json;");
    response.getWriter().println(jsonLogin);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userTypeString = ServletHelpers.getParameter(request, "user-type", "");
    this.userType = userTypeString.equals("Reviewer") ? UserType.REVIEWER : UserType.REVIEWEE;
    response.sendRedirect("/index.html");
  }

  /* checks if email_key exists in the database of userType */
  public boolean validEmail(UserType userType, String email_key) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query;
    if (userType.toString().equals("REVIEWEE")) {
      query = new Query("Reviewee");
    } else {
      query = new Query("Reviewer");
    }
    Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email_key);
    query.setFilter(emailFilter);
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults()) != 0;
  }

  public static UserType getUserType() {
    return userType;
  }
}
