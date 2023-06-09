import javax.imageio.ImageIO;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateTimeStringConverter;


// *********************************************************************
// *** fxClock JavaFX Application                                    ***
// *********************************************************************

public class fxClock extends Application {
    // All app static finals.
    static final String WINDOW_TITLE = "fxClock";

    static final String ALARM_SOUND_FOR_APP = "alarmBeep.wav";
    static final String OK_BUTTON_PNG = "okButton.png";
    static final String CANCEL_BUTTON_PNG = "cancelButton.png";

    static final Integer WINDOW_ICON_PNG_HEIGHT = 96;
    static final Integer WINDOW_ICON_PNG_WIDTH = 96;

    static final Integer GNOME_ICON_HEIGHT = 24;
    static final Integer GNOME_ICON_WIDTH = 24;
    static final Insets GNOME_IMAGE_MARGIN_INSETS =
        new Insets(1, 10, 1, 1);

    static final String ALARM_VALUE_PREFNAME = "Alarm_Value";

    static final String WINDOW_ONTOP_PREFNAME   = "Window_OnTop";
    static final String WINDOW_POS_X_PREFNAME   = "Window_Position_X";
    static final String WINDOW_POS_Y_PREFNAME   = "Window_Position_Y";
    static final String WINDOW_WIDTH_PREFNAME   = "Window_Width";
    static final String WINDOW_HEIGHT_PREFNAME  = "Window_Height";
    static final String APP_STATE_PREFNAME      = "App_State";

    static final Boolean WINDOW_ONTOP_DEFAULT = false;
    static final Double WINDOW_DEFAULT_X = 200.0;
    static final Double WINDOW_DEFAULT_Y = 200.0;
    static final Double WINDOW_DEFAULT_WIDTH = 380.0;
    static final Double WINDOW_DEFAULT_HEIGHT = 400.0;
    static final APPSTATE APP_STATE_DEFAULT = APPSTATE.ALARM_NOT_SET;

    static final Double TIME_LABEL_FONT_SIZE = 32.0;
    static final Double DATE_LABEL_FONT_SIZE = 22.0;

    static final Double ALARM_BUTTON_FONT_SIZE = 16.0;
    static final Integer ALARM_BUTTON_WIDTH = 200;
    static final Integer ALARM_BUTTON_HEIGHT = 40;

    static final LocalDateTimeStringConverter LDT_STRING_CONVERTER =
        new LocalDateTimeStringConverter();

    static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    static final String ALARM_BUTTON_STYLE =
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);" +
        "-fx-background-insets: 0, 1, 2;" +
        "-fx-background-radius: 8, 7, 6;" +
        "-fx-background-color:" +
            "linear-gradient(#f2f2f2, #4D9EED)," +
            "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #4D9EED 100%)," +
            "linear-gradient(#dddddd 0%, #f6f6f6 50%);";

    static final String ALARM_BUTTON_SET_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #4D9EED 100%)," +
        "linear-gradient(#dddddd 0%, yellow 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";

    static final String ALARM_BUTTON_PRESSED_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #4D9EED 100%)," +
        "linear-gradient(#dddddd 20%, #4D9EED 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";

    static final String ALARM_BUTTON_RINGING_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #4D9EED 100%)," +
        "linear-gradient(#dddddd 20%, red 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";


    enum APPSTATE {
        NEVER_ACTIVE("NEVER_ACTIVE"),
        ALARM_NOT_SET("ALARM_NOT_SET"),
        SETTING_ALARM("SETTING_ALARM"),
        ALARM_SET("ALARM_SET"),
        ALARM_RINGING("ALARM_RINGING");

        private final String stateString;

        APPSTATE(String saveAs) {
            this.stateString = saveAs;
        }

        private static Map<String, APPSTATE> buildMap() {
            Map<String, APPSTATE> mapto = new HashMap<>();

            for (APPSTATE appState : APPSTATE.values()) {
                mapto.put(appState.stateString, appState);
            }
            return mapto;
        }

        private static final Map<String, APPSTATE> mAppStateMap =
            buildMap();

        private static APPSTATE appStateValueOf(String name) {
            return mAppStateMap.get(name);
        }

        public String getStringValue() {
            return this.stateString;
        }
    }


    // Class Global stubs.
    static final Timer mSecondTimer = new Timer();
    static final Preferences mPref = Preferences.userRoot().node(WINDOW_TITLE);
    BufferedImage mGeneratedApplicationImage;

    // Application for this UI framework.
    Stage mApplication;
    Image mApplicationIcon;

    // Application root view for this UI framework.
    VBox mSceneBox;

    HBox mTimeDateBox;
    ImageView mGnomeImageView;
    Label mTimeLabel;
    Label mDateLabel;

    Button mAlarmButton;

    VBox mAlarmEditBox;
    LocalDateTimePicker mAlarmPicker;

    HBox mActionBox;
    Button mOkButton;
    Button mCancelButton;

    AudioInputStream CLIP_SOUND_FOR_APP;
    Clip mClip;


    /** *********************************************************************
     * Main Start stage. Set title, icon, etc.
     */
    @Override
    public void start(Stage stage) {
        mApplication = stage;

        // Load audio clip for alarm.
        try {
            CLIP_SOUND_FOR_APP = AudioSystem.getAudioInputStream(
                new File(ALARM_SOUND_FOR_APP));
            mClip = AudioSystem.getClip();
            mClip.open(CLIP_SOUND_FOR_APP);
        } catch (IOException | LineUnavailableException |
                 UnsupportedAudioFileException e) {
            System.out.println(
                "fxClock: start() Alarm beep audio sound is unavailable.");
        }

        // Set window titlebar title & icon.
        mApplication.setTitle(WINDOW_TITLE);
        createWindowIcon();
        setWindowIcon();

        // Restore window location, size, onTop user prefs.
        mApplication.setX(getWindowPosX());
        mApplication.setY(getWindowPosY());
        mApplication.setWidth(getWindowWidth());
        mApplication.setHeight(getWindowHeight());
        mApplication.setAlwaysOnTop(getWindowOnTopValue());

        // Set window scene and initial size.
        initStageScene();
        mApplication.setScene(new Scene(mSceneBox,
            WINDOW_DEFAULT_HEIGHT, WINDOW_DEFAULT_WIDTH));
        mApplication.show();

        // Create main timer.
        mSecondTimer.scheduleAtFixedRate(new TimerTask() {
            Integer mTimerPrevMin;
            @Override
            public void run() {
                // Update app with new clockFace icon once a minute.
                final Integer nowMinute = LocalDateTime.ofInstant(
                    Instant.now(), ZoneId.systemDefault()).getMinute();

                if (mTimerPrevMin == null || mTimerPrevMin != nowMinute) {
                    Platform.runLater(() -> createWindowIcon());
                    Platform.runLater(() -> setWindowIcon());
                    Platform.runLater(() -> updateStageScene());
                    mTimerPrevMin = nowMinute;
                }

                // Check if alarm has gone off.
                if (getAppState() == APPSTATE.ALARM_SET) {
                    if (getAlarmValue().isBefore(LocalDateTime.now())) {
                        setAppState(APPSTATE.ALARM_RINGING);
                        mAlarmButton.setStyle(ALARM_BUTTON_RINGING_STYLE);
                        mClip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            }
        }, 0, 1000 /* per-second */);
    }

    /** *********************************************************************
     * Main Stop stage. Save state thru prefs.
     */
    @Override
    public void stop() {
        // Cancel main timer.
        mSecondTimer.cancel();

        // Save window location, size, onTop user prefs.
        setWindowPosX(mApplication.getX());
        setWindowPosY(mApplication.getY());
        setWindowWidth(mApplication.getWidth());
        setWindowHeight(mApplication.getHeight());

        if (getAppState() == APPSTATE.SETTING_ALARM) {
            setAlarmValue(mAlarmPicker.getLocalDateTime());
        }

        setWindowOnTopValue(mApplication.isAlwaysOnTop());
    }

    /** *********************************************************************
     * Create main Form / display scene ... Date/time and alarm button.
     */
    public void initStageScene() {
        // HBox for display of Current Time and Current Date.
        final LocalDateTime ldt = LocalDateTime.ofInstant(
            Instant.now(), ZoneId.systemDefault());

        mTimeDateBox = new HBox();
        mTimeDateBox.setAlignment(Pos.CENTER);

        // Add Icon to Window contents as GNOME doesn't use it in the titlebar.
        if (System.getenv("XDG_SESSION_DESKTOP").contains("GNOME")) {
            mGnomeImageView = new ImageView(mApplicationIcon);
            HBox.setMargin(mGnomeImageView, GNOME_IMAGE_MARGIN_INSETS);
            mGnomeImageView.setPreserveRatio(true);
            mGnomeImageView.setFitWidth(GNOME_ICON_WIDTH);
            mGnomeImageView.setFitHeight(GNOME_ICON_HEIGHT);
            mTimeDateBox.getChildren().add(mGnomeImageView);
        }

        mTimeLabel = new Label(getNNWithLeadZero(ldt.getHour()) + ":" +
            getNNWithLeadZero(ldt.getMinute()) + " ");
        mTimeLabel.setFont(new Font(TIME_LABEL_FONT_SIZE));
        mTimeDateBox.getChildren().add(mTimeLabel);

        mDateLabel = new Label(MONTH_NAMES[ldt.getMonthValue() - 1] + " " +
            getNNWithLeadZero(ldt.getDayOfMonth()));
        mDateLabel.setFont(new Font(DATE_LABEL_FONT_SIZE));
        mTimeDateBox.getChildren().add(mDateLabel);

        // Button for setting Alarm.
        mAlarmButton = new Button("Alarm");
        mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
        mAlarmButton.setAlignment(Pos.CENTER);
        mAlarmButton.setMinWidth(ALARM_BUTTON_WIDTH);
        mAlarmButton.setMinHeight(ALARM_BUTTON_HEIGHT);
        mAlarmButton.setFont(new Font(ALARM_BUTTON_FONT_SIZE));

        // Alarm Button action.
        mAlarmButton.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                mAlarmButton.setStyle(ALARM_BUTTON_PRESSED_STYLE);
            }
        });
        mAlarmButton.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            }
        });
        mAlarmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                if (getAppState() == APPSTATE.ALARM_NOT_SET) {
                    setAppState(APPSTATE.SETTING_ALARM);
                    removeAlarmValue();
                    updateStageScene();
                    return;
                }

                if (getAppState() == APPSTATE.ALARM_SET) {
                    setAppState(APPSTATE.ALARM_NOT_SET);
                    removeAlarmValue();
                    updateStageScene();
                    return;
                }

                if (getAppState() == APPSTATE.ALARM_RINGING) {
                    setAppState(APPSTATE.ALARM_NOT_SET);
                    mClip.stop();
                    removeAlarmValue();
                    updateStageScene();
                    return;
                }
            }
        });

        // Picker node.
        mAlarmPicker = new LocalDateTimePicker();

        // Cancel button.
        mCancelButton = new Button();
        mCancelButton.setAlignment(Pos.CENTER);

        // Cancel button image.
        try {
            mCancelButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream(CANCEL_BUTTON_PNG),
                    24, 24, false, false)));
        } catch (Exception e) {
            System.out.println(
                "fxClock: initStageScene() cancelButton image load fails.");
        }

        // Cancel button actions.
        mCancelButton.setOnAction(new EventHandler<ActionEvent>() {
            // Must be in APPSTATE.SETTING_ALARM to get here.
            @Override public void handle(ActionEvent event) {
                setAppState(APPSTATE.ALARM_NOT_SET);
                removeAlarmValue();
                updateStageScene();
            }
        });

        // Ok button.
        mOkButton = new Button();
        mOkButton.setAlignment(Pos.CENTER);

        // Ok button image.
        try {
            mOkButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream(OK_BUTTON_PNG),
                    24, 24, false, false)));
        } catch (Exception e) {
            System.out.println(
                "fxClock: initStageScene() okButton image load fails.");
        }

        // Ok button actions.
        mOkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                setAppState(APPSTATE.ALARM_SET);
                setAlarmValue(mAlarmPicker.getLocalDateTime());
                updateStageScene();
            }
        });

        mActionBox = new HBox();
        mActionBox.setAlignment(Pos.CENTER);
        mActionBox.getChildren().add(mCancelButton);
        mActionBox.getChildren().add(getNewSpacer());
        mActionBox.getChildren().add(mOkButton);

        // VBox for mAlarmPicker display.
        mAlarmEditBox = new VBox();
        mAlarmEditBox.setAlignment(Pos.CENTER);
        mAlarmEditBox.setMaxWidth(300.0);
        mAlarmEditBox.getChildren().add(mAlarmPicker);
        mAlarmEditBox.getChildren().add(getNewSpacer());
        mAlarmEditBox.getChildren().add(mActionBox);


        // Construct top level SceneBox.
        mSceneBox = new VBox();
        mSceneBox.setAlignment(Pos.CENTER);
        mSceneBox.getChildren().add(mTimeDateBox);
        mSceneBox.getChildren().add(getNewSpacer());

        mSceneBox.getChildren().add(mAlarmButton);
        mSceneBox.getChildren().add(mAlarmEditBox);


        if (getAppState() == APPSTATE.ALARM_NOT_SET) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText("Alarm");
            mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);

        } else if (getAppState() == APPSTATE.SETTING_ALARM) {
            mAlarmButton.setVisible(false);
            mAlarmButton.setManaged(false);
            mAlarmEditBox.setVisible(true);
            mAlarmEditBox.setManaged(true);
            mAlarmPicker.setLocalDateTime(getAlarmValue());

        } else if (getAppState() == APPSTATE.ALARM_SET) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
            mAlarmButton.setStyle(ALARM_BUTTON_SET_STYLE);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);

        } else if (getAppState() == APPSTATE.ALARM_RINGING) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
            mAlarmButton.setStyle(ALARM_BUTTON_RINGING_STYLE);
            mClip.loop(Clip.LOOP_CONTINUOUSLY);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);
        }
    }

    /** *********************************************************************
     * Update main Form / display scene ... Date/time and alarm button.
     */
    public void updateStageScene() {
        // HBox for display of Current Time and Current Date.
        final LocalDateTime ldt = LocalDateTime.ofInstant(
            Instant.now(), ZoneId.systemDefault());

        // Add Icon to Window contents as GNOME doesn't use it in the titlebar.
        if (System.getenv("XDG_SESSION_DESKTOP").contains("GNOME")) {
            mTimeDateBox.getChildren().remove(mGnomeImageView);
            mGnomeImageView = new ImageView(mApplicationIcon);
            HBox.setMargin(mGnomeImageView, GNOME_IMAGE_MARGIN_INSETS);
            mGnomeImageView.setPreserveRatio(true);
            mGnomeImageView.setFitWidth(GNOME_ICON_WIDTH);
            mGnomeImageView.setFitHeight(GNOME_ICON_HEIGHT);
            mTimeDateBox.getChildren().add(mGnomeImageView);
        }

        mTimeDateBox.getChildren().remove(mTimeLabel);
        mTimeLabel = new Label(getNNWithLeadZero(ldt.getHour()) + ":" +
            getNNWithLeadZero(ldt.getMinute()) + " ");
        mTimeLabel.setFont(new Font(TIME_LABEL_FONT_SIZE));
        mTimeDateBox.getChildren().add(mTimeLabel);

        mTimeDateBox.getChildren().remove(mDateLabel);
        mDateLabel = new Label(MONTH_NAMES[ldt.getMonthValue() - 1] + " " +
            getNNWithLeadZero(ldt.getDayOfMonth()));
        mDateLabel.setFont(new Font(DATE_LABEL_FONT_SIZE));
        mTimeDateBox.getChildren().add(mDateLabel);

        if (getAppState() == APPSTATE.ALARM_NOT_SET) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText("Alarm");
            mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);

        } else if (getAppState() == APPSTATE.SETTING_ALARM) {
            mAlarmButton.setVisible(false);
            mAlarmButton.setManaged(false);
            mAlarmEditBox.setVisible(true);
            mAlarmEditBox.setManaged(true);
            mAlarmPicker.setLocalDateTime(getAlarmValue());

        } else if (getAppState() == APPSTATE.ALARM_SET) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
            mAlarmButton.setStyle(ALARM_BUTTON_SET_STYLE);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);

        } else if (getAppState() == APPSTATE.ALARM_RINGING) {
            mAlarmButton.setVisible(true);
            mAlarmButton.setManaged(true);
            mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
            mAlarmButton.setStyle(ALARM_BUTTON_RINGING_STYLE);
            mAlarmEditBox.setVisible(false);
            mAlarmEditBox.setManaged(false);
        }
    }

    /** *********************************************************************
     * Helper methods ... all Preferences getter / setters.
     */
    public Boolean getWindowOnTopValue() {
        return mPref.getBoolean(WINDOW_ONTOP_PREFNAME, WINDOW_ONTOP_DEFAULT);
    }

    public void setWindowOnTopValue(Boolean onTopValue) {
        mPref.putBoolean(WINDOW_ONTOP_PREFNAME, onTopValue);
    }

    public LocalDateTime getAlarmValue() {
        return getLDTFromString(mPref.get(ALARM_VALUE_PREFNAME,
            getStringFromLDT(LocalDateTime.now())));
    }

    public void setAlarmValue(LocalDateTime alarmValue) {
        mPref.put(ALARM_VALUE_PREFNAME, getStringFromLDT(alarmValue));
    }

    public void removeAlarmValue() {
        mPref.remove(ALARM_VALUE_PREFNAME);
    }

    public APPSTATE getAppState() {
        return APPSTATE.appStateValueOf(mPref.get(APP_STATE_PREFNAME,
            APP_STATE_DEFAULT.getStringValue()));
    }

    public void setAppState(APPSTATE state) {
        mPref.put(APP_STATE_PREFNAME, state.getStringValue());
    }

    public Double getWindowPosX() {
        return mPref.getDouble(WINDOW_POS_X_PREFNAME, WINDOW_DEFAULT_X);
    }

    public void setWindowPosX(Double x) {
        mPref.putDouble(WINDOW_POS_X_PREFNAME, x);
    }

    public Double getWindowPosY() {
        return mPref.getDouble(WINDOW_POS_Y_PREFNAME, WINDOW_DEFAULT_Y);
    }

    public void setWindowPosY(Double y) {
        mPref.putDouble(WINDOW_POS_Y_PREFNAME, y);
    }

    public Double getWindowWidth() {
        return mPref.getDouble(WINDOW_WIDTH_PREFNAME, WINDOW_DEFAULT_WIDTH);
    }

    public void setWindowWidth(Double w) {
        mPref.putDouble(WINDOW_WIDTH_PREFNAME, w);
    }

    public Double getWindowHeight() {
        return mPref.getDouble(WINDOW_HEIGHT_PREFNAME, WINDOW_DEFAULT_HEIGHT);
    }

    public void setWindowHeight(Double h) {
        mPref.putDouble(WINDOW_HEIGHT_PREFNAME, h);
    }

    /** *********************************************************************
     * Helper method to return a spacer box.
     */
    public HBox getNewSpacer() {
        final HBox spacerBox = new HBox();
        spacerBox.setPrefHeight(15);
        spacerBox.setPrefWidth(15);

        return spacerBox;
    }

    /** *********************************************************************
     * Helper method, format number ( < 60 ) as two-digit with leading zero.
     */
    public String getNNWithLeadZero(Integer number) {
        return (number < 10) ?
            "0" + number.toString() :
            number.toString();
    }

    /** *********************************************************************
     * Helper methods to Format LocalDateTime to String
     * and String to LocalDateTime.
     */
    public LocalDateTime getLDTFromString(String date) {
        return LDT_STRING_CONVERTER.fromString(date);
    }

    public String getStringFromLDT(LocalDateTime date) {
        return LDT_STRING_CONVERTER.toString(date);
    }

    public String getStyledAlarmString(LocalDateTime date) {
        return getNNWithLeadZero(date.getHour()) + ":" +
            getNNWithLeadZero(date.getMinute()) + " " +
            MONTH_NAMES[date.getMonthValue() - 1] + " " +
            getNNWithLeadZero(date.getDayOfMonth());
    }

    /** *********************************************************************
     * Creates resource of clock face with current time.
     */
    public void createWindowIcon() {
        final Canvas canvas = new Canvas(
            WINDOW_ICON_PNG_WIDTH, WINDOW_ICON_PNG_HEIGHT);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        // <!-- Clock "ear" bells -->
        gc.setFill(Color.BLACK);
        gc.fillOval(4, 4, 32, 32);
        gc.fillOval(60, 4, 32, 32);

        // <!-- Outer clock body circle -->
        gc.setFill(Color.BLUE);
        gc.fillOval(8, 8, 80, 80);

        // <!-- Inner clock body circle -->
        gc.setFill(Color.WHITE);
        gc.fillOval(16, 16, 64, 64);

        // <!-- Clock top alarm button -->
        gc.setFill(Color.BLACK);
        gc.fillRect(44, 2, 8, 6);

        // <!-- Two clock feet -->
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        gc.strokeLine(14, 86, 20, 80);
        gc.strokeLine(82, 86, 76, 80);
        gc.setLineWidth(1);

        // <!-- Two clock hands -->
        final Instant instant = Instant.now();
        final LocalDateTime ldt = LocalDateTime.ofInstant(
            instant, ZoneId.systemDefault());
        final Integer nowHour = ldt.getHour();
        final Integer nowMin = ldt.getMinute();

        final Integer hourMod = nowHour % 12;
        final Double hourSec = (hourMod * 3600.0);
        final Double minSec = (nowMin * 60.0);
        final Double totSec = (hourSec + minSec);

        final Double totSecondsInHour = 12.0 * 60.0 * 60.0;
        final Double hourRot = totSec / totSecondsInHour * 360.0 - 90;
        final Integer minRot = nowMin * 360 / 60 - 90;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(48 -  4 * Math.cos(Math.toRadians(hourRot)),
            48 -  4 * Math.sin(Math.toRadians(hourRot)),
            48 + 18 * Math.cos(Math.toRadians(hourRot)),
            48 + 18 * Math.sin(Math.toRadians(hourRot)));
        gc.strokeLine(48 -  4 * Math.cos(Math.toRadians(minRot)),
            48 -  4 * Math.sin(Math.toRadians(minRot)),
            48 + 26 * Math.cos(Math.toRadians(minRot)),
            48 + 26 * Math.sin(Math.toRadians(minRot)));
        gc.setLineWidth(1);

        // <!-- Clock center, small circle -->
        gc.setFill(Color.BLUE);
        gc.fillOval(46, 46, 4, 4);

        // Set background transparent.
        final SnapshotParameters snapParms = new SnapshotParameters();
        snapParms.setFill(Color.TRANSPARENT);

        // Save clockFace as global.
        final WritableImage writableImage = new WritableImage(
            WINDOW_ICON_PNG_WIDTH, WINDOW_ICON_PNG_HEIGHT);
        canvas.snapshot(snapParms, writableImage);

        mGeneratedApplicationImage =
            SwingFXUtils.fromFXImage((Image) writableImage, null);
    }

    /** *********************************************************************
     * Helper method, loads Window Icon from where we've
     * Created resource of clock face with current time.
     */
    public void setWindowIcon() {
        // Remove one we previously set.
        if (mApplicationIcon != null) {
            mApplication.getIcons().remove(mApplicationIcon);
        }

        // Set the new one.
        try {
            mApplicationIcon = SwingFXUtils.toFXImage(mGeneratedApplicationImage, null);
            mApplication.getIcons().add(mApplicationIcon);
        } catch (Exception e) {
            System.out.println(
                "fxClock: setWindowIcon() Setting window icon fails: \n" + e);
        }
    }
}