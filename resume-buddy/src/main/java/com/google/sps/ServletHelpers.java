package com.google.sps;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  /** Returns a Blobkey that points to the blobstore of the uploaded pdf resume */
  public static String getBlobstoreKey(
      HttpServletRequest request, HttpServletResponse response, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL.
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey.getKeyString();
  }

  public static int deleteBlob(String resumeBlobKey) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    boolean isBlobDeleted = false;

    BlobKey blobKey = new BlobKey(resumeBlobKey);
    blobstoreService.delete(blobKey);

    BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
    isBlobDeleted = blobInfoFactory.loadBlobInfo(blobKey) == null;

    if (isBlobDeleted) {
      return 200; // 200: OK - The request is OK
    } else {
      return 400; // 400: Bad Request - The request cannot be fulfilled
    }
  }
}

/* TODO add a test file for ServletHelpers */
