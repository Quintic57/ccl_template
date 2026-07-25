package my.dw.ccl.util;

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

// TODO: Meld this into spring boot app
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
public class GoogleSheetsNotebookAdapter {
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SPREADSHEET_ID = "1dVmecQl6_PZ3m_BKRYhXAFvEPP1oiEnkzWgtR1ovbPQ";

    // Mapping of expected sheet names to target CSV file names
    private static final Map<String, String> SHEET_TO_CSV = new HashMap<>();
    static {
        SHEET_TO_CSV.put("Cross-Banlist", "decklist_cb.csv");
        SHEET_TO_CSV.put("Modern", "decklist_md.csv");
        SHEET_TO_CSV.put("Edison", "decklist_ed.csv");
        SHEET_TO_CSV.put("GOAT", "decklist_gt.csv");
    }

    /**
     * Fetches the four sheets from the provided spreadsheetId and writes CSV files into outputDir.
     * Uses a service account JSON credentials file for authentication.
     *
     * @param spreadsheetId ID of the Google Sheets spreadsheet (the "notebook")
     * @throws IOException when IO fails
     * @throws GeneralSecurityException when auth/transport setup fails
     */
    public static void fetchAndWrite(String spreadsheetId)
            throws IOException, GeneralSecurityException {
        if (spreadsheetId == null || spreadsheetId.isEmpty()) {
            throw new IllegalArgumentException("spreadsheetId is required");
        }
        Path outDir = Paths.get("src/main/resources/decklist/");
        Files.createDirectories(outDir);

        GoogleCredentials credentials;
        try (FileInputStream fis = new FileInputStream("src/main/resources/keys/ccltemplate-59e930f13508.json")) {
            credentials = ServiceAccountCredentials.fromStream(fis)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets.readonly"));
        }

        final var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName("ccl_template-google-sheets-adapter")
                .build();

        for (Map.Entry<String, String> e : SHEET_TO_CSV.entrySet()) {
            String sheetName = e.getKey();
            String csvFileName = e.getValue();
            Path outFile = outDir.resolve(csvFileName);

            try {
                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, sheetName)
                        .execute();
                List<List<Object>> values = response.getValues();
                writeCsv(outFile, values);
            } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException gre) {
                // If sheet not found (404 or 400), write empty placeholder CSV
                System.out.println("Sheet '" + sheetName + "' not found in spreadsheet. Skipping...");
            } catch (IOException ex) {
                // For other IO problems, rethrow so caller can handle/log.
                throw ex;
            }
        }
    }

    private static void writeCsv(Path outFile, List<List<Object>> values) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(outFile)) {
            if (values == null || values.isEmpty()) {
                // write nothing (empty placeholder)
                return;
            }
            for (List<Object> row : values) {
                String line = row.stream()
                        .map(cell -> cell == null ? "" : escapeCsv(cell.toString()))
                        .collect(Collectors.joining(","));
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // Minimal CSV escaping: double quotes become "" and values containing comma/quote/newline are quoted
    private static String escapeCsv(String s) {
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }

    // Simple helper main for manual runs
    public static void main(String[] args) throws Exception {
        fetchAndWrite(SPREADSHEET_ID);
    }
}
