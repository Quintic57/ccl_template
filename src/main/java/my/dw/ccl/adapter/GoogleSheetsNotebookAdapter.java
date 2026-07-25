package my.dw.ccl.adapter;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.micrometer.common.util.StringUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public List<Map<String, String>> getSheetValues(final String spreadSheetId, final Format format)
            throws IOException, GeneralSecurityException {
        if (spreadSheetId == null || spreadSheetId.isEmpty() || format == null) {
            throw new IllegalArgumentException("spreadsheetId and/or format is required");
        }

        final GoogleCredentials credentials;
        try (FileInputStream fis = new FileInputStream("src/main/resources/keys/ccltemplate-59e930f13508.json")) {
            credentials = ServiceAccountCredentials.fromStream(fis)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets.readonly"));
        }

        // NOTE: use a plain NetHttpTransport (default JVM cacerts truststore) rather than
        // GoogleNetHttpTransport.newTrustedTransport(), which pins to the bundled google.jks and
        // therefore rejects the Zscaler-intercepted TLS chain. cacerts contains the Zscaler root.
        final Sheets adapter = new Sheets.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName("ccl_template-google-sheets-adapter").build();

        try {
            final ValueRange response = adapter.spreadsheets().values()
                .get(spreadSheetId, format.getName())
                .execute();
            // Sheets returns a CSV-like nested list: first row is the header, the rest are data rows.
            return convertRawSheetValues(response.getValues());
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException gre) {
            // TODO: Add logging implementation
            System.out.println("Sheet '" + format.getName() + "' not found in spreadsheet. Skipping...");
            return List.of();
        }
    }

    private List<Map<String, String>> convertRawSheetValues(final List<List<Object>> values) {
      if (values == null || values.size() < 2) {
        return new ArrayList<>();
      }

      final List<Object> headerRow = values.get(0);
      final List<Map<String, String>> rows = new ArrayList<>();
      for (int r = 1; r < values.size(); r++) {
        final List<Object> row = values.get(r);
        final Map<String, String> rowMap = new LinkedHashMap<>();
        for (int c = 0; c < headerRow.size(); c++) {
          final String key = headerRow.get(c) == null ? "" : headerRow.get(c).toString().trim();
          // Sheets omits trailing empty cells, so a data row may be shorter than the header.
          final String value = c < row.size() && row.get(c) != null ? row.get(c).toString() : "";
          rowMap.put(key, value);
        }
        // Skip blank/trailing rows that have no deck name.
        if (StringUtils.isNotBlank(rowMap.get("Name"))) {
          rows.add(rowMap);
        }
      }

      return rows;
    }

}
