import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApplication extends Application {

    static final Logger log = LoggerFactory.getLogger(ClientApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("ClientFrame.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}