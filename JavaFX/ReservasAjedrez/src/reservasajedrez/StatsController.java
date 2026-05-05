package reservasajedrez;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

public class StatsController implements Initializable {

    @FXML private PieChart chartOcupacio;
    @FXML private LineChart<String, Number> chartEvolucio;
    @FXML private BarChart<String, Number> chartUsuaris;

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarOcupacionPistas();
        cargarEvolucionMensual();
        cargarTopJugadores();
    }

    private void cargarOcupacionPistas() {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/mesas/cerca"))
            .GET().build();

    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                JsonArray mesasArray = JsonParser.parseString(response.body()).getAsJsonArray();
                
                int ocupadas = 0;
                int libres = 0;

                for (JsonElement el : mesasArray) {
                    JsonObject mesa = el.getAsJsonObject();
                    if (mesa.get("disponibilidad").getAsBoolean()) {
                        libres++;
                    } else {
                        ocupadas++;
                    }
                }

                final int finalLibres = libres;
                final int finalOcupadas = ocupadas;

                Platform.runLater(() -> {
                    chartOcupacio.getData().clear();
                    chartOcupacio.getData().add(new PieChart.Data("Ocupades", finalOcupadas));
                    chartOcupacio.getData().add(new PieChart.Data("Lliures", finalLibres));
                });
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
}

    private void cargarEvolucionMensual() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/stats/evolucion"))
                .GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Reserves 2026");

                    for (JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        series.getData().add(new XYChart.Data<>(obj.get("mes").getAsString(), obj.get("total").getAsInt()));
                    }
                    Platform.runLater(() -> chartEvolucio.getData().add(series));
                });
    }

private void cargarTopJugadores() {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/stats/top-jugadores"))
            .GET().build();

    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                if (response.statusCode() == 200) {
                    JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Partides Jugades");

                    for (JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        series.getData().add(new XYChart.Data<>(
                            obj.get("username").getAsString(), 
                            obj.get("partidas").getAsInt()
                        ));
                    }

                    Platform.runLater(() -> {
                        chartUsuaris.getData().clear();
                        chartUsuaris.getData().add(series);
                    });
                }
            })
            .exceptionally(ex -> {
                System.err.println("Error cargando Top Jugadores: " + ex.getMessage());
                return null;
            });
}
}