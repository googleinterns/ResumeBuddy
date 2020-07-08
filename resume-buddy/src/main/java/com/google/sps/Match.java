package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Pair;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Class that matches reviewers to reviewees */
public class Match {

  private Match() {}

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

  /**
   * FCFS algorithm to match reviewers with reviewees. When two people are matched, their entities
   * are deleted ('Reviewee' and 'Reviewer') from datastore and new entity ('Match') for their pair
   * is created
   */
  public static void match(List<Entity> reviewees, List<Entity> reviewers) {
    // TODO: Update algorithm based on criteria

    List<Pair<Integer, Pair<Entity, Entity>>> rankedMatches =
        new ArrayList<Pair<Integer, Pair<Entity, Entity>>>();

    for (Entity reviewee : reviewees) {
      for (Entity reviewer : reviewers) {
        int matchPoint = 0;

        String revieweeCareer = (String) reviewee.getProperty("career");
        String reviewerCareer = (String) reviewer.getProperty("career");
        if (revieweeCareer.equals(reviewerCareer)) {
          matchPoint += 3;
        }

        String revieweeSchool = (String) reviewee.getProperty("school");
        String reviewerSchool = (String) reviewer.getProperty("school");
        if (revieweeSchool.equals(reviewerSchool)) {
          matchPoint += 2;
        }

        String revieweePrefExperience = (String) reviewee.getProperty("preferred-experience");
        String reviewerExperience = (String) reviewee.getProperty("years-experience");
        if (revieweePrefExperience.equals(reviewerExperience)) {
          matchPoint += 1;
        }

        String revieweePrefDegree = (String) reviewee.getProperty("preferred-degree");
        String reviewerDegree = (String) reviewee.getProperty("degree");
        if (revieweePrefDegree.equals(reviewerDegree)) {
          matchPoint += 1;
        }

        rankedMatches.add(new Pair(matchPoint, new Pair(reviewee, reviewer)));
      }
    }

    Collections.sort(rankedMatches, new SortByPoints());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Set<String> matchedReviewees = new HashSet<>();
    Set<String> matchedReviewers = new HashSet<>();
    for (Pair<Integer, Pair<Entity, Entity>> rankedMatch : rankedMatches) {

      Pair<Entity, Entity> match = rankedMatch.getVal();
      Entity reviewee = match.getKey();
      Entity reviewer = match.getVal();

      String revieweeEmail = (String) reviewee.getProperty("email");
      String reviewerEmail = (String) reviewer.getProperty("email");

      if (matchedReviewees.contains(revieweeEmail) || matchedReviewers.contains(reviewerEmail)) {
        continue;
      }

      matchedReviewees.add(revieweeEmail);
      matchedReviewers.add(reviewerEmail);

      Entity matchEntity = new Entity("Match");
      matchEntity.setProperty("reviewee", revieweeEmail);
      matchEntity.setProperty("reviewer", reviewerEmail);
      datastore.put(matchEntity);

      // Delete reviewers and reviewees from Datastore once matched
      datastore.delete(reviewer.getKey());
      datastore.delete(reviewee.getKey());
    }
  }

  public static class SortByPoints implements Comparator<Pair<Integer, Pair<Entity, Entity>>> {
    @Override
    public int compare(
        Pair<Integer, Pair<Entity, Entity>> match1, Pair<Integer, Pair<Entity, Entity>> match2) {
      return -((int) match1.getKey() - (int) match2.getKey());
    }
  }
}
