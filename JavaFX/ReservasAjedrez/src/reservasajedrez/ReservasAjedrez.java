package reservasajedrez;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReservasAjedrez extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // La ruta debe coincidir con la ubicación de tu archivo FXML
        // Si está en el paquete 'reservasajedrez', la ruta es /reservasajedrez/Login.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/reservasajedrez/Login.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Acceso al Sistema - Reservas Ajedrez");
        stage.setResizable(false); // Opcional: para que no se pueda maximizar el login
        stage.show();
    }
}
