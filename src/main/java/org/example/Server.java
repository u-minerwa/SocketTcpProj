package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    String CONST_MOSCOW = "Moscow";
    String CONST_EUROPE = "Europe";
    static String common_city = "Saratov";
    static String common_country = "RU";
    String country = "Russia";
    static String s_city = common_city+','+common_country;
    static int city_id = 0;
    static String appId = "2a9391878d3c4a87279b49bdc5f73a9d";

    public static void whileTrue() throws Exception {
        while (true){
            // Получение данных о погоде:
            JSONObject data = WeatherDataFetcherFromBefore.fetchWeatherData(s_city, appId);
            city_id = data.getJSONArray("list").getJSONObject(0).getInt("id");

            // Получение временной зоны:
            TimeZone tz_a = TimeZone.getTimeZone("Europe/Saratov"); // "Europe/Saratov"
            ZonedDateTime dt_a = ZonedDateTime.now(tz_a.toZoneId());

            // Форматирование даты и времени:
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dt_a.format(formatter);

            // Создание JSON формата:
            JSONObject weatherJsonFirst = new JSONObject();
            weatherJsonFirst.put("city", common_city);
            weatherJsonFirst.put("country", common_country);
            weatherJsonFirst.put("city_id", city_id);
            weatherJsonFirst.put("conditions", data.getJSONArray("list").getJSONObject(0).getJSONArray("weather").
                    getJSONObject(0).getString("description"));
            weatherJsonFirst.put("date_time_now", formattedDateTime);
            weatherJsonFirst.put("temp", data.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp"));
            weatherJsonFirst.put("temp_min", data.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_min"));
            weatherJsonFirst.put("temp_max", data.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_max"));

            // Преобразование в строку JSON с отступами:
            String jsonString = weatherJsonFirst.toString();

            // Вывод строки JSON:
            System.out.println(jsonString);
            System.out.println("Successful!");
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void proverka() throws Exception {
        System.out.println("saasas");
    }

    public static void startServer() throws Exception {
        serverSocket = new ServerSocket(9999);
        System.out.println("Ждём подключения клиента...");

        clientSocket = serverSocket.accept();
        System.out.println("Подключен клиент с адресом: " + clientSocket.getInetAddress());

        // Здесь ты можешь использовать clientSocket для взаимодействия с клиентом:

        Server.whileTrue();
        // System.out.println("dsdsd");
    }

    public void closeSockets() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


