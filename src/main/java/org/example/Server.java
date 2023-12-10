package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
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


    public static void proverka() throws Exception {
        System.out.println("saasas");
    }

    public static void startServerR() throws Exception {
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("Сервер запущен и ожидает подключения...");

            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket);

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                int messageCount = 1;
                while (true) {
                    String message = getJson();
                    out.println(message);
                    System.out.print(message);
                    Thread.sleep(5000);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getJson() {
        // Получение данных о погоде:
        try {
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
            return weatherJsonFirst.toString();
        } catch (Exception e) {
            System.out.println("Error while fetching data from API");
            return "";
        }
    }

    public static void startServer() throws Exception {
        serverSocket = new ServerSocket(9999);
        System.out.println("Ждём подключения клиента...");

        clientSocket = serverSocket.accept();
        System.out.println("Подключен клиент с адресом: " + clientSocket.getInetAddress());

        // Здесь ты можешь использовать clientSocket для взаимодействия с клиентом:

        Server.startServerR();
    }

    public static boolean closeSockets() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                return true;
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                throw new RuntimeException("Server Socket is closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
