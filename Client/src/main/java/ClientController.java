import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public ListView<String> fileList;
    public MultipleSelectionModel<String> selectionModel;
    public ListView<String> serverResponse;
    private Label selectedLbl = new Label();
    private boolean doubleClick = false;
    private List<String[]> files;




    public void initialize(URL location, ResourceBundle resources) {
        files = new ArrayList<>();
        refreshList();
        getSelectedText();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if(doubleClick) {
            refreshList(getSelectedText());
        }else {
            doubleClick = true;
        }
        new Thread(this::delayDoubleClick).start();
    }

    private void delayDoubleClick(){
        try {
            Thread.sleep(500);
            doubleClick = false;
        } catch (InterruptedException e) {
            System.err.println("Error when wait double click");
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            refreshList(getSelectedText());
        }
        if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)) refreshList("\\...");
    }

    private String getSelectedText(){
        selectionModel = fileList.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedLbl.setText(newValue);
            }
        });
        return selectedLbl.getText();
    }

    private void refreshList(){
        files = Arrays.asList(FileHandler.getFileList());
        refreshFiles();
    }

    private void refreshList(String selectedFile){
        if(selectedFile.length()>0) {
            if(selectedFile.equals("\\...")){
                String[] temp = files.get(0);
                String[] steps = temp[1].split("\\\\");
                if (steps.length>2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < steps.length - 2; i++) {
                        sb.append(steps[i]);
                        sb.append("\\");
                    }
                    selectedFile = sb.toString();
                    files = Arrays.asList(FileHandler.getFileList(selectedFile));
                    refreshFiles();
                } else refreshList();
            }else {
                files = Arrays.asList(FileHandler.getFileList(getPath(selectedFile)));
                refreshFiles();
            }
        }
    }

    private void refreshFiles () {
        fileList.getItems().clear();
        String[] fileSchedule = new String[files.size()+1];
        fileSchedule[0] = "\\...";
        for (int i = 1; i <= files.size(); i++) {
            String[] nod = files.get(i-1);
            String[] fileName = nod[1].split("\\\\");
            fileSchedule[i] = nod[0] + fileName[fileName.length-1];
        }
        Platform.runLater(()->{fileList.getItems().addAll(fileSchedule);});
        selectionModel = fileList.getSelectionModel();
    }



    private String getPath (String selectedFiles){
        String selectedName[] = selectedFiles.split("\t");
        for (String[] file: files) {
            String[] fileName = file[1].split("\\\\");
            if(selectedName[selectedName.length-1].equals(fileName[fileName.length-1])){
                return file[1];
            }
        }
        throw new RuntimeException("Error when search files");
    }

    public void printServerMessage (String msg){
        serverResponse.getItems().clear();
        Platform.runLater(()->{serverResponse.getItems().add(msg);});
        selectionModel = serverResponse.getSelectionModel();
    }


    public void sendToServer(ActionEvent actionEvent) {
        try {
            Net net = Net.start(8189, this);
            net.sendFile(getPath(getSelectedText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}