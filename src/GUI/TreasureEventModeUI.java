package GUI;

import constant.Constant;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ModelWrapper;
import model.TreasureEventMode;
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
public class TreasureEventModeUI {

    private final FileChooser fileChooser = new FileChooser();

    private final FileParseService fileParseService = new FileParseServiceImpl();

    private final DataApplyService dataApplyService = new DataApplyServiceImpl();

    private final PathFixService pathFixService = new PathFixServiceImpl();

    private File treasureEventLua;

    private String treasureEventLuaPath = "";

    private File treasureEventNightmareLua;

    private String treasureEventNightmareLuaPath = "";

    /**
     * Draw Party Selection UI
     * @param modelWrapper
     * @return
     */
    @SuppressWarnings("Duplicates")
    public VBox drawMainPane(Stage primaryStage, ModelWrapper modelWrapper) {
        TreasureEventMode treasureEventMode = modelWrapper.getTreasureEventMode();

        VBox vBox = new VBox();
        vBox.setPrefWidth(500);

        GridPane grid = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(-7, 10, 5, 10));

        // EventRaidUrl
        TextField treasureEventRaidURL = new TextField();
        treasureEventRaidURL.setText(String.valueOf(treasureEventMode.getTreasureEventUrl()));

        grid.add(treasureEventRaidURL,1,1);
        grid.add(new Label("Treasure Event raid URL:"), 0, 1);

        // LuaScript
        treasureEventLuaPath = treasureEventMode.getTreasureEventModeScript();
        Button eventLuaScript = new Button("Browse");
        eventLuaScript.setOnAction(e -> {
            treasureEventLua = fileChooser.showOpenDialog(primaryStage);
            if (treasureEventLua != null) {
                Path p1 = Paths.get(Constant.ZOOEY_BOT_INI_ABSOLUTE);
                Path p2 = Paths.get(treasureEventLua.getAbsolutePath());
                treasureEventLuaPath = pathFixService.fixPath(p1.relativize(p2).toString());
            }
        });

        grid.add(eventLuaScript,1,2);
        grid.add(new Label("Treasure Event Lua Script:"), 0, 2);

        // Difficulty
        ChoiceBox<String> difficulty = new ChoiceBox<>();
        difficulty.getItems().addAll(Constant.TREASURE_RAIDS.keySet());
        difficulty.setValue(treasureEventMode.getNameByDifficulty());

        grid.add(difficulty,1,3);
        grid.add(new Label("Difficulty:"), 0, 3);

        // AP Cost
        TextField actionPointCost = new TextField();
        actionPointCost.setText(String.valueOf(treasureEventMode.getActionPointCost()));

        grid.add(actionPointCost,1,4);
        grid.add(new Label("Action Points Cost:"), 0, 4);

        // NightmareModeUrl
        TextField nightmareModeURL = new TextField();
        nightmareModeURL.setText(String.valueOf(treasureEventMode.getNightmareModeUrl()));

        grid.add(nightmareModeURL,1,5);
        grid.add(new Label("Treasure Event Nightmare raid URL:"), 0, 5);

        // LuaScript
        treasureEventNightmareLuaPath = treasureEventMode.getNightmareModeScript();
        Button treasureEventNightmareLuaScript = new Button("Browse");
        treasureEventNightmareLuaScript.setOnAction(e -> {
            treasureEventNightmareLua = fileChooser.showOpenDialog(primaryStage);
            if (treasureEventNightmareLua != null) {
                Path p1 = Paths.get(Constant.ZOOEY_BOT_INI_ABSOLUTE);
                Path p2 = Paths.get(treasureEventNightmareLua.getAbsolutePath());
                treasureEventNightmareLuaPath = pathFixService.fixPath(p1.relativize(p2).toString());
            }
        });

        grid.add(treasureEventNightmareLuaScript,1,6);
        grid.add(new Label("Treasure Event Nightmare Lua Script:"), 0, 6);

        // NightmareModePreferredSummon
        TextField nightmareModePreferredSummon = new TextField();
        nightmareModePreferredSummon.setText(treasureEventMode.getNightmareModePreferredSummon());

        grid.add(nightmareModePreferredSummon,1,7);
        grid.add(new Label("Treasure Event Preferred Summon:"), 0, 7);

        // RerollSummonWhenNoPreferredSummonWasFoundForNightmareMode
        CheckBox rerollForSummon = new CheckBox();
        rerollForSummon.setSelected(treasureEventMode.isRerollForSummon());

        grid.add(rerollForSummon,1,8);
        grid.add(new Label("Reroll For Summon during nightmare:"), 0, 8);

        // NightmareModeAvailableAtStart
        CheckBox nightmareAtStart = new CheckBox();
        nightmareAtStart.setSelected(treasureEventMode.isNightmareModeAvailableAtStart());

        grid.add(nightmareAtStart,1,9);
        grid.add(new Label("Nightmare Mode available at start:"), 0, 9);

        // Save
        Button save = new Button();
        save.setText("SAVE");
        save.setPrefSize(180, 40);
        save.setOnAction(e -> handleSave(modelWrapper, treasureEventRaidURL.getText(), treasureEventLuaPath, difficulty.getValue(),
                                        actionPointCost.getText(), nightmareModeURL.getText(), treasureEventNightmareLuaPath,
                                        nightmareModePreferredSummon.getText(), rerollForSummon.isSelected(),
                                        nightmareAtStart.isSelected()));
        grid.add(save, 1, 10);

        vBox.getChildren().addAll(grid);
        vBox.setStyle("-fx-background-color: white");
        return vBox;
    }

    /**
     * Handle save to TreasureEventMode
     * @param modelWrapper
     * @param treasureEventURL
     * @param treasureEventLuaPath
     * @param difficulty
     * @param actionPointCost
     * @param nightmareModeURL
     * @param nightmareLuaPath
     * @param nightmareModePreferredSummon
     * @param rerollForSummon
     * @param nightmareAtStart
     */
    private void handleSave(ModelWrapper modelWrapper, String treasureEventURL, String treasureEventLuaPath,
                            String difficulty, String actionPointCost, String nightmareModeURL, String nightmareLuaPath,
                            String nightmareModePreferredSummon, boolean rerollForSummon, boolean nightmareAtStart) {
        TreasureEventMode treasureEventMode = modelWrapper.getTreasureEventMode();
        List<String> fileContent = null;

        treasureEventMode.setTreasureEventUrl(treasureEventURL);
        treasureEventMode.setTreasureEventModeScript(treasureEventLuaPath);
        treasureEventMode.setDifficulty(Constant.TREASURE_RAIDS.get(difficulty));
        treasureEventMode.setActionPointCost(actionPointCost);
        treasureEventMode.setNightmareModeUrl(nightmareModeURL);
        treasureEventMode.setNightmareModeScript(nightmareLuaPath);
        treasureEventMode.setNightmareModePreferredSummon(nightmareModePreferredSummon);
        treasureEventMode.setRerollForSummon(rerollForSummon);
        treasureEventMode.setNightmareModeAvailableAtStart(nightmareAtStart);

        try {
            fileContent = fileParseService.generateFileContentFromIni();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply data change
        for (String prefix: Constant.TREASURE_EVENT_PARAMS) {
            dataApplyService.applyData(prefix, fileContent, modelWrapper, Constant.MODE_TREASURE_EVENT);
        }
    }
}
