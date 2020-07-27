package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.api.Email;
import com.google.sps.data.EmailTemplates;
import com.google.sps.data.ReviewStatus;
import java.io.IOException;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that reminds reviewers to review resume */
@WebServlet("/reminder")
public class ReminderServlet extends HttpServlet {

  private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
  private static final Date THREE_DAYS_AGO = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));
  private static final Date SEVEN_DAYS_AGO = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));

  /** Needs to be called periodically to check if reviewers need to be reminded */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Match");
    Filter dateFilter = new FilterPredicate("matchDate", FilterOperator.LESS_THAN, THREE_DAYS_AGO);
    Filter statusFilter =
        new FilterPredicate("status", FilterOperator.EQUAL, ReviewStatus.IN_PROCESS.toString());

    Filter CompositeFilter = CompositeFilterOperator.and(dateFilter, statusFilter);
    query.setFilter(CompositeFilter);
    PreparedQuery results = datastore.prepare(query);

    for (Entity matchEntity : results.asIterable()) {
      String reviewerEmail = (String) matchEntity.getProperty("reviewer");
      Email.sendEmail(
          reviewerEmail,
          EmailTemplates.REVIEW_REMINDER_SUBJECT_LINE,
          EmailTemplates.REVIEW_REMINDER_BODY,
          null);
    }
  }

  private void sendReminderEmail(String email) {}
}
