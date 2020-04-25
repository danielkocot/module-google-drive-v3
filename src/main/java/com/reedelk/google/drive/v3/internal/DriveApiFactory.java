package com.reedelk.google.drive.v3.internal;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.reedelk.google.drive.v3.component.DriveConfiguration;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.component.Implementor;
import com.reedelk.runtime.api.exception.PlatformException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;

public class DriveApiFactory {

    private DriveApiFactory() {
    }

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static DriveApi create(Class<? extends Implementor> implementor, DriveConfiguration configuration) {
        requireNotNull(implementor, configuration, "Google Drive Configuration must be provided.");

        try {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credentials credentials = getCredentials(configuration);

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                    .setApplicationName(implementor.getSimpleName())
                    .build();

            return new DriveApi(service);
        } catch (IOException | GeneralSecurityException e) {
            // TODO: Better error message.
            throw new PlatformException(e);
        }
    }

    private static Credentials getCredentials(DriveConfiguration configuration) throws IOException, GeneralSecurityException {
        String credentialsFilePath = configuration.getCredentials();
        String serviceAccountEmail = configuration.getServiceAccountEmail();
        try (FileInputStream is = new FileInputStream(credentialsFilePath)) {
            ServiceAccountCredentials.Builder builder = ServiceAccountCredentials.fromStream(is)
                    .toBuilder()
                    .setScopes(DriveScopes.all());
            if (StringUtils.isNotBlank(serviceAccountEmail)) {
                // The email of the user account to impersonate,
                // if delegating domain-wide authority to the service account.
                builder.setServiceAccountUser(serviceAccountEmail);
            }
            return builder.build();
        }
    }
}
