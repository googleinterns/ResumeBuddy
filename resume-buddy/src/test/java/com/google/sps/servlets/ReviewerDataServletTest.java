package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.data.Degree;
import com.google.sps.data.NumYears;
import com.google.sps.data.SchoolYear;
import com.google.sps.servlets.ReviewerDataServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for UserDataServlet */
@RunWith(JUnit4.class)
public class ReviewerDataServletTest {
  private Entity reviewer;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private ReviewerDataServlet reviewerDataServlet;
  private DatastoreService datastore;

  private LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
          new LocalUserServiceTestConfig(),
          new LocalURLFetchServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    reviewerDataServlet = new ReviewerDataServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /** test reviewer who has filled out sign up form with all their information */
  @Test
  public void test() throws ServletException, IOException {
    helper.setEnvEmail("shreyabarua@google.com").setEnvAuthDomain("google.com").setEnvIsLoggedIn(true);
    when(ServletHelpers.getParameter(request, "fname", "")()).thenReturn("Shreya");
    when(ServletHelpers.getParameter(request, "lname", "")()).thenReturn("Barua");
    when(ServletHelpers.getParameter(request, "email", "")()).thenReturn("shreyabarua2@gmail.com");
    when(ServletHelpers.getParameter(request, "school", "")()).thenReturn("RPI");
    when(ServletHelpers.getParameter(request, "company", "")()).thenReturn("Google");
    when(ServletHelpers.getParameter(request, "career", "")()).thenReturn("Computer Software/Engineering");
    when(ServletHelpers.getParameter(request, "degree", "")()).thenReturn("bachelor");
    when(ServletHelpers.getParameter(request, "years-experience", "")()).thenReturn("less_than_5");

    reviewerDataServlet.doPost(request, response);
    



    
    String fname = response.get("firstName").getAsString();
    String lname = response.get("lastName").getAsString();
    String email = response.get("email").getAsString();
    String school = response.get("school").getAsString();
    String career = response.get("career").getAsString();
    String company = response.get("company").getAsString();
    Degree degree = Degree.valueOf(response.get("degree").getAsString());
    SchoolYear schoolYear = SchoolYear.valueOf(response.get("schoolYear").getAsString());
    NumYears yearsExperience = NumYears.valueOf(response.get("years-experience").getAsString());

    Assert.assertTrue(fname.equals("Shreya"));
    Assert.assertTrue(lname.equals("Barua"));
    Assert.assertTrue(email.equals("shreyabarua@google.com"));
    Assert.assertTrue(school.equals("RPI"));
    Assert.assertTrue(career.equals("Computer Software/Engineering"));
    Assert.assertTrue(company.equals("Google"));
    Assert.assertTrue(degree.equals(Degree.BACHELOR));
    Assert.assertTrue(schoolYear.equals(SchoolYear.OTHER));
    Assert.assertTrue(yearsExperience.equals(NumYears.LESS_THAN_5));
  }

}
