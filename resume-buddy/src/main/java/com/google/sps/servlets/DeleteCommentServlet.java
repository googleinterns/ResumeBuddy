package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String currentUser = userService.getCurrentUser().getEmail();
    currentUser = currentUser.substring(0, currentUser.indexOf('@'));

    String id = request.getParameter("id");
    String author = request.getParameter("author");
    Query query = new Query("Review-comments");

    if (author.equals(currentUser)) {
      if (!id.equals("undefined")) {
        Filter uuidPropertyFilter = new FilterPredicate("uuid", FilterOperator.EQUAL, id);
        query.setFilter(uuidPropertyFilter);
      }
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      List<Key> keys = new ArrayList<>();
      for (Entity entity : results.asIterable()) {
        keys.add(entity.getKey());
      }
      datastore.delete(keys);
    }

    response.setContentType("text/plain");
    response.getWriter().println("");
  }
}
