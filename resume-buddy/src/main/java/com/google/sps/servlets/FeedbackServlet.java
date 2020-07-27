package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.ServletHelpers;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String userType = ServletHelpers.getParameter(request, "user-type", "");
    String feedback = ServletHelpers.getParameter(request, "message", "");
    Entity feedbackEntity = new Entity("Feedback");
    feedbackEntity.setProperty("userType", userType);
    feedbackEntity.setProperty("feedback", feedback);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(feedbackEntity);
    response.sendRedirect("/index.html");
  }
}
