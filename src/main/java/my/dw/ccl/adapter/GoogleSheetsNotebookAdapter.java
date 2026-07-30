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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.ygo.YgoDeck;
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
            return convertSheetValues(response.getValues(), format);
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException gre) {
            // TODO: Add logging implementation
            System.out.println("Sheet '" + format.getName() + "' not found in spreadsheet. Skipping...");
            return List.of();
        }
    }

    /**
     * Converts the raw Google Sheets {@code values} (a CSV-like nested list where the first row is
     * the header and every subsequent row is a data row) into a list of {@link Deck}.
     * Column headers are expected to match the CSV column names (Name, Banlist, Implemented Date,
     * Ported, Active, Shared, OCG, Custom). Rows are keyed by header name so column order is
     * irrelevant, and trailing empty cells omitted by the Sheets API are treated as blank.
     */
    private List<Deck> convertSheetValues(final List<List<Object>> values, final Format format) {
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

        return rows.stream()
            .map(m -> new YgoDeck(
                    m.get("Name"),
                    format,
                    StringUtils.isNotBlank(m.get("Implemented Date")) ?  LocalDate.parse(m.get("Implemented Date")) : null,
                    Boolean.parseBoolean(m.get("Active")),
                    m.get("Shared"),
                    Boolean.parseBoolean(m.get("Custom")),
                    YearMonth.of(
                        Integer.parseInt((m.get("Banlist")).split("-")[0]),
                        Integer.parseInt(( m.get("Banlist")).split("-")[1])
                    ),
                    Boolean.parseBoolean(m.get("Ported")),
                    Boolean.parseBoolean(m.get("OCG"))
                )
            )
            .collect(Collectors.toList());
    }

}
