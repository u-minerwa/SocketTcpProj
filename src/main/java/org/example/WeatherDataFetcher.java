package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeatherDataFetcher {
    private static final String CONST_MOSCOW = "Moscow";
    private static final String CONST_EUROPE = "Europe";
    private static final String commonCity = "Saratov";
    private static final String commonCountry = "RU";
    private static final String sCity = commonCity + ',' + commonCountry;
    private static final String appId = "2a9391878d3c4a87279b49bdc5f73a9d";

    public static void fetchDataAndSend() throws IOException, InterruptedException {
        while (true) {
            // Проверка наличия в базе информации о нужном населенном пункте:
            HttpURLConnection cityConnection = fetchData("http://api.openweathermap.org/data/2.5/find",
                    "q=" + sCity + "&type=like&units=metric&APPID=" + appId);
            String cityResponse = readResponse(cityConnection);
            int cityId = Integer.parseInt(cityResponse.split("\"id\":")[1].split(",")[0]);

            // Получение информации о текущей погоде:
            HttpURLConnection weatherConnection = fetchData("http://api.openweathermap.org/data/2.5/weather",
                    "id=" + cityId + "&units=metric&lang=en&APPID=" + appId);
            String weatherResponse = readResponse(weatherConnection);

            // Date Time:
            TimeZone tz_a = TimeZone.getTimeZone(CONST_EUROPE + '/' + commonCity);
            ZonedDateTime dt_a = ZonedDateTime.now(tz_a.toZoneId());

            // Создание JSON формата:
            String weatherJsonFirst = String.format("{\"city\":\"%s\",\"country\":" +
                            "\"%s\",\"city_id\":%d,\"conditions\":\"%s\",\"date_time_now\":\"%s\",\"temp\":%f,\"temp_min\":%f,\"temp_max\":%f}\n",
                    commonCity,
                    commonCountry,
                    cityId,
                    weatherResponse.split("\"description\":\"")[1].split("\"")[0],
                    dt_a.toString(),
                    Double.parseDouble(weatherResponse.split("\"temp\":")[1].split(",")[0]),
                    Double.parseDouble(weatherResponse.split("\"temp_min\":")[1].split(",")[0]),
                    Double.parseDouble(weatherResponse.split("\"temp_max\":")[1].split("}")[0]));

            sendDataToServer(weatherJsonFirst);

            System.out.println("Successful!");
            TimeUnit.SECONDS.sleep(10);

        }
    }

    private static HttpURLConnection fetchData(String url, String params) throws IOException {
        URL apiUrl = new URL(url + "?" + params);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    private static void sendDataToServer(String data) {
        try (Socket clientSocket = new Socket("localhost", 9999)) {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


