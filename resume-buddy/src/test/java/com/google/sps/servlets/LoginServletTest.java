package com.google.sps;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.servlets.LoginServlet;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class LoginServletTest {

  private static final String USER_EMAIL = "animachaidze@gmail.com";
  private static final String USER_ID = "animachaidze";
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  private LoginServlet loginServlet;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig(),
              new LocalURLFetchServiceTestConfig())
          .setEnvEmail(USER_EMAIL)
          .setEnvAuthDomain("gmail.com")
          .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", USER_ID)));

  @Before
  public void setUp() {
    helper.setUp();
    helper.setEnvIsLoggedIn(true);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);

    loginServlet = new LoginServlet();
    // datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDoPost() throws IOException {
    when(request.getParameter("user-type")).thenReturn("Reviewer");
    loginServlet.doPost(request, response);
    verify(request, atLeast(1)).getParameter("user-type");

    Assert.assertTrue(true);
  }
}
