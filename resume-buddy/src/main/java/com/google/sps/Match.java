package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.List;

/** Class that matches reviewers to reviewees */
public class Match {
  List<Entity> reviewees;
  List<Entity> reviewers;

  public Match() {
    getNotMatchedUsers();
    match();
  }

  /** Retrieves reviewers' and reviewees' data from the Datastore */
  private void getNotMatchedUsers() {
    Query revieweeQuery = new Query("Reviewee");
    Query reviewerQuery = new Query("Reviewer");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery revieweeResults = datastore.prepare(revieweeQuery);
    PreparedQuery reviewerResults = datastore.prepare(reviewerQuery);
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);

    reviewees = revieweeResults.asList(fetchOptions);
    reviewers = reviewerResults.asList(fetchOptions);
  }

  /** FCFS algorithm to match reviewers with reviewees */
  private void match() {
    // TODO: Update algorithm based on criteria 

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int numberOfMatched = Math.min(reviewees.size(), reviewers.size());

    for (int i = 0; i < numberOfMatched; i++) {
      Entity reviewer = reviewers.get(i);
      Entity reviewee = reviewees.get(i);

      Entity matchEntity = new Entity("Matches");
      matchEntity.setProperty("reviewer", reviewer.getProperty("email"));
      matchEntity.setProperty("reviewee", reviewee.getProperty("email"));
      datastore.put(matchEntity);

      // Delete reviewers and reviewees from Datastore once matched
      datastore.delete(reviewer.getKey());
      datastore.delete(reviewee.getKey());
    }
  }
}
