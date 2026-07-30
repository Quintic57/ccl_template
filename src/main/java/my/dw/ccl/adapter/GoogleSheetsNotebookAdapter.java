package my.dw.ccl.adapter;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;
import org.springframework.stereotype.Service;

/**
 * Adapter to read a "notebook" (spreadsheet) from Google Sheets and write each of the
 * four expected sheets to local CSV files. If a sheet is missing, an empty CSV placeholder is
 * written instead.
 *
 * Usage (service account):
 * 1) Enable Google Sheets API in Google Cloud Console
 * 2) Create a Service Account, download the JSON key
 * 3) Share the spreadsheet with the service account email
 * 4) Call: GoogleSheetsNotebookAdapter.fetchAndWrite(spreadsheetId, pathToServiceAccountJson, outputDir);
 */
@Service
public class GoogleSheetsNotebookAdapter {

    public List<Deck> getDecksByFormat(final String spreadSheetId, final Format format)
            throws IOException, GeneralSecurityException {
        if (spreadSheetId == null || spreadSheetId.isEmpty() || format == null) {
            throw new IllegalArgumentException("spreadsheetId and/or format is required");
        }

        final GoogleCredentials credentials;
        try (FileInputStream fis = new FileInputStream("src/main/resources/keys/ccltemplate-59e930f13508.json")) {
            credentials = ServiceAccountCredentials.fromStream(fis)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets.readonly"));
        }

        final Sheets adapter = new Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName("ccl_template-google-sheets-adapter").build();

        try {
            ValueRange response = adapter.spreadsheets().values()
                .get(spreadSheetId, format.getName())
                .execute();
            List<List<Object>> values = response.getValues();
            final String temp = "";
//                writeCsv(outFile, values);
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException gre) {
            // TODO: Add logging implementation
            System.out.println("Sheet '" + format.getName() + "' not found in spreadsheet. Skipping...");
            return List.of();
        } catch (IOException ex) {
            // For other IO problems, rethrow so caller can handle/log.
            throw ex;
        }

        // TODO: IMPLEMENT
        return List.of();
    }

    public Map<Format, List<Deck>> getAllDecks(final String spreadSheetId) throws IOException, GeneralSecurityException {
        final Map<Format, List<Deck>> decksByFormat = new HashMap<>();

        for (final Format format: Format.values()) {
            decksByFormat.put(format, getDecksByFormat(spreadSheetId, format));
        }

        return decksByFormat;
    }

}
