package com.google.sps.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.sps.Consts;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

public class GmailAPI {
  private static final String APPLICATION_NAME = "ResumeBuddy";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  static Gmail service = null;
  private static File credentialsFilePath = new File("credentials.json");

  public static Gmail getGmailService() throws IOException, GeneralSecurityException {

    InputStream in = new FileInputStream(credentialsFilePath);
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    Credential authorize =
        new GoogleCredential.Builder()
            .setTransport(GoogleNetHttpTransport.newTrustedTransport())
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(
                clientSecrets.getDetails().getClientId().toString(),
                clientSecrets.getDetails().getClientSecret().toString())
            .build()
            .setAccessToken(Consts.ACCESS_TOKEN)
            .setRefreshToken(Consts.REFRESH_TOKEN);

    // Create Gmail service
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    service =
        new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
            .setApplicationName(GmailAPI.APPLICATION_NAME)
            .build();

    return service;
  }
}
