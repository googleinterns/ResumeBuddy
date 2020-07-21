import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.List;
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
    Filter revieweeFilter = new FilterPredicate("reviewee", FilterOperation.EQUAL, email);
    revieweeQuery.setFilter(revieweeFilter);
    PreparedQuery results = datastore.prepare(revieweeQuery);

    if(results.countEntities(FetchOptions.Builder.withDefaults()) == 0) {
        Query reviewerQuery = new Query("Match");
        Filter reviewerFilter = new FilterPredicate("reviewer", FilterOperation.EQUAL, email);
        reviewerQuery.setFilter(revieweeFilter);
        PreparedQuery reviewerResults = datastore.prepare(revieweeQuery);
        String reviewerBlobKeyString = reviewerResults.getProperty("resumeBlobKey").toString();
        BlobKey userblobKey = new BlobKey(userBlobKeyString);
        blobstoreService.serve(userBlobKey, response);
    }

    String revieweeBlobKeyString = revieweeResults.getProperty("resumeBlobKey").toString();
    BlobKey userBlobKey = new BlobKey(revieweeBlobKeyString);
    blobstoreService.serve(userBlobKey, response);
  }
}
