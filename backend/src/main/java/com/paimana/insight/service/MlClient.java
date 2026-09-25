package com.paimana.insight.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paimana.insight.model.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MlClient {

    private final ObjectMapper objectMapper;
    private final String mlBaseUrl;

    public MlClient(
            @Value("${app.ml.base-url}") String mlBaseUrl,
            ObjectMapper objectMapper
    ) {
        this.mlBaseUrl = mlBaseUrl;
        this.objectMapper = objectMapper;
    }

    public Prediction predict(Project p) {

        try {

            // ==============================
            // 1. Create request data
            // ==============================

            Map<String, Object> request = new LinkedHashMap<>();

            request.put(
                    "original_cost",
                    p.getOriginalCost() == null
                            ? 0.0
                            : p.getOriginalCost().doubleValue()
            );

            request.put(
                    "revised_cost",
                    p.getRevisedCost() == null
                            ? 0.0
                            : p.getRevisedCost().doubleValue()
            );

            request.put(
                    "expenditure",
                    p.getExpenditure() == null
                            ? 0.0
                            : p.getExpenditure().doubleValue()
            );

            request.put(
                    "physical_progress",
                    p.getPhysicalProgress() == null
                            ? 0.0
                            : p.getPhysicalProgress().doubleValue()
            );

            request.put(
                    "original_end_date",
                    String.valueOf(p.getOriginalEndDate())
            );

            request.put(
                    "revised_end_date",
                    String.valueOf(p.getRevisedEndDate())
            );


            // ==============================
            // 2. Convert data to JSON
            // ==============================

            String json = objectMapper.writeValueAsString(request);

            byte[] jsonBytes =
                    json.getBytes(StandardCharsets.UTF_8);


            // ==============================
            // 3. Print request details
            // ==============================

            System.out.println("=================================");
            System.out.println("SENDING JSON TO ML SERVICE");
            System.out.println(json);
            System.out.println("JSON SIZE: " + jsonBytes.length);
            System.out.println("ML URL: " + mlBaseUrl + "/predict");
            System.out.println("=================================");


            // ==============================
            // 4. Create connection
            // ==============================

            URL url = new URL(
                    mlBaseUrl + "/predict"
            );

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();


            // ==============================
            // 5. Configure HTTP request
            // ==============================

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            // Important:
            // Send exact content length
            connection.setFixedLengthStreamingMode(
                    jsonBytes.length
            );


            // ==============================
            // 6. Send JSON body
            // ==============================

            try (OutputStream outputStream =
                         connection.getOutputStream()) {

                outputStream.write(jsonBytes);
                outputStream.flush();
            }

            System.out.println("JSON BODY SENT");


            // ==============================
            // 7. Get response
            // ==============================

            int status =
                    connection.getResponseCode();

            InputStream inputStream;

            if (status >= 200 && status < 300) {

                inputStream =
                        connection.getInputStream();

            } else {

                inputStream =
                        connection.getErrorStream();
            }


            // ==============================
            // 8. Read response body
            // ==============================

            String responseBody = "";

            if (inputStream != null) {

                responseBody =
                        new String(
                                inputStream.readAllBytes(),
                                StandardCharsets.UTF_8
                        );
            }


            // ==============================
            // 9. Print ML response
            // ==============================

            System.out.println("=================================");
            System.out.println("ML HTTP STATUS");
            System.out.println(status);

            System.out.println("ML RESPONSE");
            System.out.println(responseBody);

            System.out.println("=================================");


            connection.disconnect();


            // ==============================
            // 10. Check HTTP status
            // ==============================

            if (status < 200 || status >= 300) {

                throw new RuntimeException(
                        "ML service returned HTTP "
                                + status
                                + ": "
                                + responseBody
                );
            }


            // ==============================
            // 11. Convert response to object
            // ==============================

            Prediction result =
                    objectMapper.readValue(
                            responseBody,
                            Prediction.class
                    );


            System.out.println(
                    "ML PREDICTION SUCCESS"
            );

            System.out.println(result);


            return result;


        } catch (Exception e) {

            // ==============================
            // Error handling
            // ==============================

            System.out.println("=================================");
            System.out.println("ML ERROR");
            System.out.println(e.getMessage());
            System.out.println("=================================");

            e.printStackTrace();


            // ==============================
            // Fallback response
            // ==============================

            return new Prediction(
                    "MEDIUM",
                    "MEDIUM",
                    "MEDIUM",
                    "Fallback-Heuristic",
                    "demo-1"
            );
        }
    }


    // ==============================
    // ML response model
    // ==============================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Prediction(

            String costRisk,

            String delayRisk,

            String overallRisk,

            String modelName,

            String modelVersion

    ) {
    }
}