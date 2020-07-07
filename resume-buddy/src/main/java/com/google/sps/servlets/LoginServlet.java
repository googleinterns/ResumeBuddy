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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    boolean status;
    if (userService.isUserLoggedIn()) {
      String email = userService.getCurrentUser().getEmail();
      status = validEmail("Reviewer", email) || validEmail("Reviewee", email);
    } else {
      status = false;
    }
    String jsonLogin;
    String urlToRedirectToAfterUserLogsIn = "/index.html";
    String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
    String urlToRedirectToAfterUserLogsOut = "/index.html";
    String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

    if (status) {
      jsonLogin = "{\"status\": true, ";

    } else {
      jsonLogin = "{\"status\": false, ";
    }
    jsonLogin += "\"login_url\": \"" + loginUrl + "\", ";
    jsonLogin += "\"logout_url\": \"" + logoutUrl + "\"}";
    // send the json as the response
    response.setContentType("application/json;");
    response.getWriter().println(jsonLogin);
  }

  public boolean validEmail(String queryType, String email_key) {
    // search through the respective database and make sure that user is signed up on the website
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(queryType);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String email = (String) entity.getProperty("email");
      if (email.equals(email_key)) return true;
    }
    return false;
  }
}
