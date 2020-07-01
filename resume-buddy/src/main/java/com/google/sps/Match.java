package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Pair;
import java.util.ArrayList;
import java.util.List;

/** Class that matches reviewers to reviewees */
public class Match {

  /** Retrieves list of entities for specific query kind from the Datastore */
  public static List<Entity> getNotMatchedUsers(String queryKind) {
    List<Entity> entityList;
    Query query = new Query(queryKind);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);

    entityList = results.asList(fetchOptions);
    return entityList;
  }

  /** FCFS algorithm to match reviewers with reviewees */
  public static List<Pair<String, String>> match(List<Entity> reviewees, List<Entity> reviewers) {
    // TODO: Update algorithm based on criteria
    List<Pair<String, String>> matches = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int numberOfMatched = Math.min(reviewees.size(), reviewers.size());

    for (int i = 0; i < numberOfMatched; i++) {
      Entity reviewer = reviewers.get(i);
      Entity reviewee = reviewees.get(i);

      Entity matchEntity = new Entity("Matches");

      String reviewerEmail = (String) reviewer.getProperty("email");
      String revieweeEmail = (String) reviewee.getProperty("email");
      matchEntity.setProperty("reviewer", reviewer.getProperty("email"));
      matchEntity.setProperty("reviewee", reviewee.getProperty("email"));
      datastore.put(matchEntity);

      matches.add(new Pair(revieweeEmail, reviewerEmail));

      // Delete reviewers and reviewees from Datastore once matched
      datastore.delete(reviewer.getKey());
      datastore.delete(reviewee.getKey());
    }

    return matches;
  }
}
