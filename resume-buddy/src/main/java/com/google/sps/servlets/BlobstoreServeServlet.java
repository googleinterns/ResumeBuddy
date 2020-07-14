import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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
    // Gets the logged in user's email and sets the result to the user's entity
    // TODO: (sesexton) If the reviewer is logged in, it get their reviewee's resume
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    Query query = new Query("Reviewee");
    Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    query.setFilter(emailFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Use the results to grab the users resumeBlobKey, and serve the blob with that key
    // TODO: (sesexton) If multiple accounts , grab the first submission of pdf
    String userBlobKeyString = results.asSingleEntity().getProperty("resumeBlobKey").toString();
    BlobKey userBlobKey = new BlobKey(userBlobKeyString);
    blobstoreService.serve(userBlobKey, response);
  }
}
