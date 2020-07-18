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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
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

    // is this the right place to put this code?
    UserService userService = UserServiceFactory.getUserService();
    boolean status = userService.isUserLoggedIn();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String email = "";
    if (status) {
      email = userService.getCurrentUser().getEmail();
      if (newUser(email)) {
        System.out.println("new user");
        Entity userEntity = new Entity("User");
        userEntity.setProperty("email", email);
        userEntity.setProperty("matchID", "");
        datastore.put(userEntity);
      }
    }
    String jsonLogin;
    String urlToRedirectToAfterUserLogsIn = "/index.html";
    String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
    String urlToRedirectToAfterUserLogsOut = "/index.html";
    String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
    if (userService.isUserLoggedIn()) {
      jsonLogin = "{\"status\": true, ";
    } else {
      jsonLogin = "{\"status\": false, ";
    }
    jsonLogin += "\"login_url\": \"" + loginUrl + "\", ";
    jsonLogin += "\"logout_url\": \"" + logoutUrl + "\", ";
    jsonLogin += "\"email\": \"" + email + "\"}";

    // send the json as the response
    response.setContentType("application/json;");
    response.getWriter().println(jsonLogin);
  }

  /* return true if this user does not exist in the User db yet*/
  public static boolean newUser(String email) {
    Query query = new Query("User");
    Filter emailFilter;
    emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    query.setFilter(emailFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults()) == 0;
  }
}
