package com.google.sps;

import javax.servlet.http.HttpServletRequest;

/* utility class dedicated to static helper functions for servlets*/
public class ServletHelpers {

  private ServletHelpers() {}

  /**
   * @return the request parameter, or the default value if the parameter was not specified 
by
   * the client
   */
  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null) ? defaultValue : value;
  }
}

/* TODO add a test file for ServletHelpers */
