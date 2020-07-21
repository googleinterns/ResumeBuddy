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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/blobstore-serve")
public class BlobstoreServeServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Gets the logged in user's email, finds the PDF blob from the Match datastore and serves it
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);
    String email = userService.getCurrentUser().getEmail();

    Query revieweeQuery = new Query("Match");
    Query reviewerQuery = new Query("Match");
    Filter revieweeFilter = new FilterPredicate("reviewee", FilterOperator.EQUAL, email);
    Filter reviewerFilter = new FilterPredicate("reviewer", FilterOperator.EQUAL, email);
    revieweeQuery.setFilter(revieweeFilter);
    reviewerQuery.setFilter(reviewerFilter);
    PreparedQuery reviewerResults = datastore.prepare(reviewerQuery);
    PreparedQuery revieweeResults = datastore.prepare(revieweeQuery);
    String matchBlobKeyString = "";

    if (revieweeResults.countEntities(FetchOptions.Builder.withDefaults()) == 0) {
      matchBlobKeyString = reviewerResults.asSingleEntity().getProperty("resumeBlobKey").toString();
    } else {
      matchBlobKeyString = revieweeResults.asSingleEntity().getProperty("resumeBlobKey").toString();
    }

    BlobKey matchBlobKey = new BlobKey(matchBlobKeyString);
    blobstoreService.serve(matchBlobKey, response);
  }
}
