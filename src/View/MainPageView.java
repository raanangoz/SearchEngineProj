package View;

import Controller.Controller;
import Model.Excpetions.SearcherException;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainPageView implements Initializable {

    //<editor-fold desc="buttons">
    public Button Browse_s; //browse button to load save path
    public Button Browse_w; //browse button to load working path
    public Button parse_b; //parse button
    public Button reset_b; //reset button
    public Button load_b; //load dictionary button
    public Button showdic_b; //show dictionary button
    public CheckBox stemming_option; //stemming checkbox
    public TextField work_path; //text field for working path
    public TextField save_path; //text field for saving path
    public TextField query_path; //query path
    public TextField query_text; //query text
    public SplitMenuButton splitMenuButton; //language menu
    public SplitMenuButton cityMenu; //language menu

    List<CheckMenuItem> allCityList = new ArrayList<>(); //list of citys
    public boolean checkRunAgain = false;
    public Menu menu;
    protected boolean checkbox_value = false; //start checkbox as false, if marked change to true
    //</editor-fold>

    private Controller controller = Controller.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cityMenu.setText("Cities");
    }

    //update work path textfield
    public void Browse_working(ActionEvent actionEvent) {
        UpdateTextField(work_path);
    }

    //update save path textfield
    public void Browse_saving(ActionEvent actionEvent) {
        UpdateTextField(save_path);
    }

    //update query path textfield
    public void Browse_query(ActionEvent actionEvent) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Folder");
            File selectedDirectory = chooser.showOpenDialog(null);
            query_path.setText(selectedDirectory.getPath());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a folder");
            alert.showAndWait();
        }
    }

    //gets path from filechoser and updates the field text
    private void UpdateTextField(TextField Path) {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose Folder");
            File selectedDirectory = chooser.showDialog(null);
            Path.setText(selectedDirectory.getPath());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a folder");
            alert.showAndWait();
        }
    }

    //checkbox for stemming, updates the boolean value accordingly
    public void checkBox_stemmimg(ActionEvent actionEvent) {
        checkbox_value = stemming_option.isSelected();
    }

    //merge function
    public void merge_func(ActionEvent actionEvent) {
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        controller.merg_func(workPath, savePath);
    }

    //parse function
    public void parse(ActionEvent actionEvent) {
        try {
            String workPath = work_path.getText();
            String savePath = save_path.getText();
            //if path left empty
            if (savePath.equals("") || workPath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a folder in save and work path");
                alert.showAndWait();
            } else {
                double t = System.currentTimeMillis();
                controller.parse(workPath, savePath, checkbox_value);
//                double y = System.currentTimeMillis() - t;
//                System.out.println("total time to parse = " + ((y) / 1000) + " seconds");
//                double l = System.currentTimeMillis();
                controller.writeLastDocsToDisk(savePath);
                controller.mergePartialPosting(workPath, savePath);

                HashMap<String, String> countryDocsList = controller.getCountryList();
                city_pick(countryDocsList);

                int coutIndexed = controller.getNumberOfIndexed();
                int uniqterms = controller.getDicSize();
//                System.out.println("total time to merge = " + ((System.currentTimeMillis() - l) / 1000) + " seconds");
//                System.out.println("total time to run program = " + ((System.currentTimeMillis() - t) / 1000) + " seconds");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Done");
                alert.setHeaderText("total time to run program : " + ((System.currentTimeMillis() - t) / 1000) + " seconds. \n" +
                        "total number of parsed documents is : " + coutIndexed);
                alert.showAndWait();
            }
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reset function to clear post file and dictionary
    public void reset(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a folder in save and work path");
                alert.showAndWait();
            } else
                controller.resetButton(savePath);
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        }

    }

    //show dictionary function
    public void show_dic(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Save Path does not have Dictionary file");
                alert.showAndWait();
            } else
                controller.showDic(savePath);
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fail");
            alert.setHeaderText("Dictionary does not exist");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        Stage s = (Stage) showdic_b.getScene().getWindow();
//        s.close();
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("Show_Dic.fxml"));
//            Stage stage = new Stage();
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setResizable(true);
//            stage.setTitle("Show Dictionary");
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //load dictionary
    public void load_dic(ActionEvent actionEvent) {
        try {
            controller.loadDic(save_path.getText());
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage() + " to load dictionary");
            alert.showAndWait();

        } catch (RuntimeException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fail");
            alert.setHeaderText(" Dictionary does not exist");
            alert.showAndWait();
        }
    }

    //run query from text
    public void run_query(ActionEvent actionEvent) {

        String queryText = query_text.getText();
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        //if path left empty
        if (savePath.equals("") || workPath.equals("") || queryText.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a folder in save and work path");
            alert.showAndWait();
        } else {
            controller.runQuery(queryText, workPath, savePath, checkbox_value);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Done");
        }
    }

    //run query from file
    public void run_query_file(ActionEvent actionEvent) {
        //TODO ADD HERE IF's like in quest string
        String queryText = query_path.getText();
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        List<String> chosenCities = getCountryForSearch(allCityList);
        controller.runQueryFile(queryText, workPath, savePath, checkbox_value, chosenCities);
    }

    // TODO: 22/12/2018 fix this  Itzik
    public void city_pick(HashMap<String, String> cityListDoc) {
        for (Map.Entry<String, String> entry : cityListDoc.entrySet())
            allCityList.add(new CheckMenuItem(entry.getKey()));
        if (checkRunAgain == false)
            cityMenu.getItems().addAll(allCityList);
        checkRunAgain = true;
//        if((CheckMenuItem)cityMenu.getItems().get(1))
    }

    //return selected countrys
    public List<String> getCountryForSearch(List<CheckMenuItem> allCountryList) {
        List<String> selectedCountrys = new LinkedList<>();
        for (int i = 0; i < allCountryList.size(); i++) {
            if (allCountryList.get(i).isSelected() == true)
                selectedCountrys.add(allCountryList.get(i).getText());
        }
        for (int i = 0; i < selectedCountrys.size(); i++)
            System.out.println(selectedCountrys.get(i));
        return selectedCountrys;
    }

    public void load_cities(ActionEvent actionEvent) {
        try {
            List<String> selectedCountrys = controller.loadCity(save_path.getText());
            allCityList.clear();
            for (int i = 0; i < selectedCountrys.size(); i++)
                allCityList.add(new CheckMenuItem(selectedCountrys.get(i)));
            cityMenu.getItems().removeAll();
            cityMenu.getItems().clear();
//            cityMenu.getItems().remove(0,5);
            cityMenu.getItems().addAll(allCityList);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fail");
            alert.setHeaderText(" City file does not exist");
            alert.showAndWait();
        }
    }
}