package GUI;

import constant.Constant;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ModelWrapper;
import model.SlaveMode;
import service.DataApplyService;
import service.FileParseService;
import service.PathFixService;
import service.impl.DataApplyServiceImpl;
import service.impl.FileParseServiceImpl;
import service.impl.PathFixServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Trung on 10/14/2017.
 */
public class SlaveModeUI {

    private final FileChooser fileChooser = new FileChooser();

    private final FileParseService fileParseService = new FileParseServiceImpl();

    private final DataApplyService dataApplyService = new DataApplyServiceImpl();

    private final PathFixService pathFixService = new PathFixServiceImpl();

    private File mainLua;

    private File slaveLua;

    private String mainLuaPath = "";

    private String slaveLuaPath = "";

    /**
     * Draw SlaveMode UI
     * @param primaryStage
     * @param modelWrapper
     * @return
     */
    @SuppressWarnings("Duplicates")
    public VBox drawMainPane(Stage primaryStage, ModelWrapper modelWrapper) {
        SlaveMode slaveMode = modelWrapper.getSlaveMode();

        VBox vBox = new VBox();
        vBox.setPrefWidth(500);

        GridPane grid = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(-7, 10, 5, 10));

        // Main Account Lua
        mainLuaPath = slaveMode.getMainAccLua();
        Button browseMainLua = new Button("Browse");
        browseMainLua.setOnAction(e -> {
            mainLua = fileChooser.showOpenDialog(primaryStage);
            if (mainLua != null) {
                Path p1 = Paths.get(Constant.ZOOEY_BOT_INI_ABSOLUTE);
                Path p2 = Paths.get(mainLua.getAbsolutePath());
                mainLuaPath = pathFixService.fixPath(p1.relativize(p2).toString());
            }
        });

        grid.add(browseMainLua,1,1);
        grid.add(new Label("Main Account Lua Script:"), 0, 1);

        // Slave Account Lua
        slaveLuaPath = slaveMode.getSlaveAccLua();
        Button browseSlaveLua = new Button("Browse");
        browseSlaveLua.setOnAction(e -> {
            slaveLua = fileChooser.showOpenDialog(primaryStage);
            if (slaveLua != null) {
                Path p1 = Paths.get(Constant.ZOOEY_BOT_INI_ABSOLUTE);
                Path p2 = Paths.get(slaveLua.getAbsolutePath());
                slaveLuaPath = pathFixService.fixPath(p1.relativize(p2).toString());
            }
        });

        grid.add(browseSlaveLua,1,2);
        grid.add(new Label("Slave Account Lua Script:"), 0, 2);

        // ProcessMainAccountTurnFirst
        CheckBox mainAccountFirst = new CheckBox();
        mainAccountFirst.setSelected(slaveMode.isMainAccFirst());

        grid.add(mainAccountFirst,1,3);
        grid.add(new Label("Process Main Account turn first:"), 0, 3);

        // Save
        Button save = new Button();
        save.setText("SAVE");
        save.setPrefSize(180, 40);
        save.setOnAction(e -> handleSave(modelWrapper, mainAccountFirst.isSelected(), mainLuaPath, slaveLuaPath));
        grid.add(save, 1, 4);

        vBox.getChildren().addAll(grid);
        vBox.setStyle("-fx-background-color: white");
        return vBox;
    }

    /**
     * Handle save into SaveMode Object
     * @param modelWrapper
     * @param mainAccountFirst
     * @param mainAccountLua
     * @param slaveAccountLua
     */
    private void handleSave(ModelWrapper modelWrapper, boolean mainAccountFirst,
                            String mainAccountLua, String slaveAccountLua) {
        SlaveMode slaveMode = modelWrapper.getSlaveMode();
        List<String> fileContent = null;

        slaveMode.setMainAccFirst(mainAccountFirst);
        slaveMode.setSlaveAccLua(slaveAccountLua);
        slaveMode.setMainAccLua(mainAccountLua);

        try {
            fileContent = fileParseService.generateFileContentFromIni();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply data change
        for (String prefix: Constant.SLAVE_MODE_PARAMS) {
            dataApplyService.applyData(prefix, fileContent, modelWrapper, Constant.MODE_SLAVE);
        }
    }
}
