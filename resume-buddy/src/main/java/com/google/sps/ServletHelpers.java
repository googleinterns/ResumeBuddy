package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/* utility class dedicated to static helper functions for servlets*/
public class ServletHelpers {

  private ServletHelpers() {}

  /*
   * return the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null) ? defaultValue : value;
  }

  /** Checks if the id collides with other ids in datastore */
  public static boolean collides(UUID id, String entity) {
    Query query = new Query(entity);

    Filter uuidPropertyFilter = new FilterPredicate("uuid", FilterOperator.EQUAL, id.toString());
    query.setFilter(uuidPropertyFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    return results.countEntities(FetchOptions.Builder.withDefaults()) != 0;
  }
}

/* TODO add a test file for ServletHelpers */
