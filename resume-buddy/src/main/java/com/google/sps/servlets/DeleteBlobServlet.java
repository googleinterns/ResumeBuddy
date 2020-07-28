import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-blob")
public class DeleteBlobServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    boolean isBlobDeleted = false;

    String resumeBlobKey = request.getParameter("resumeBlobKey");
    BlobKey blobKey = new BlobKey(resumeBlobKey);
    blobstoreService.delete(blobKey);

    // check if blob is empty (not sure if this will work, I dont know what the blobkey is being set
    // too after its deleted)
    if (blobKey == null) {
      isBlobDeleted = true;
    }

    // set the status message corresponding to wheter its true or false
    if (isBlobDeleted) {
      response.setStatus(200); // works
      // response.setMessage("The blob has been deleted!");
    } else {
      response.setStatus(400); // error
      // response.statusText("The blob is not been deleted.");
    }
  }
}
