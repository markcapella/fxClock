// *********************************************************************
// *** fxClock JavaFX Application                                    ***
// *********************************************************************


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.lang.Runtime;
import java.lang.Thread;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.LocalDateTimeStringConverter;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;


// *********************************************************************
// *** fxClock JavaFX Application                                    ***
// *********************************************************************

public class fxClock extends Application {
    // All app static finals.
    static final String   WINDOW_TITLE                = "fxClock";
    static final String   ALARM_SOUND_FOR_APP         = "alarmBeep.wav";

    static final Integer  WINDOW_ICON_PNG_HEIGHT     = 96;
    static final Integer  WINDOW_ICON_PNG_WIDTH      = 96;

    static final Integer  GNOME_ICON_HEIGHT          = 24;
    static final Integer  GNOME_ICON_WIDTH           = 24;
    static final Insets   GNOME_IMAGE_MARGIN_INSETS   = new Insets(1, 10, 1, 1);

    static final Double   DISPLAY_FONTSIZE_MAXIMUM = 65.0;
    static final Double   DISPLAY_FONTSIZE_MINIMUM  = 10.0;

    static final String   APP_STATE_PREFNAME        = "App_State";
    static final String   WINDOW_ONTOP_PREFNAME     = "Window_OnTop";
    static final String   WINDOW_POS_X_PREFNAME     = "Window_Position_X";
    static final String   WINDOW_POS_Y_PREFNAME     = "Window_Position_Y";
    static final String   WINDOW_WIDTH_PREFNAME     = "Window_Width";
    static final String   WINDOW_HEIGHT_PREFNAME    = "Window_Height";
    static final String   ALARM_VALUE_PREFNAME      = "Alarm_Value";
    static final String   TIMEBOX_FONTSIZE_PREFNAME = "Timebox_Fontsize";
    static final String   DATEBOX_FONTSIZE_PREFNAME = "Datebox_Fontsize";

    static final APPSTATE APP_DEFAULT_STATE         = APPSTATE.ALARM_NOT_SET;
    static final Boolean  WINDOW_ONTOP_DEFAULT      = false;
    static final Double   WINDOW_DEFAULT_POS_X      = 200.0;
    static final Double   WINDOW_DEFAULT_POS_Y      = 200.0;
    static final Double   WINDOW_DEFAULT_WIDTH      = 380.0;
    static final Double   WINDOW_DEFAULT_HEIGHT     = 400.0;
    static final Double   ALARM_BUTTON_FONT_SIZE    = 16.0;
    static final Integer  ALARM_BUTTON_WIDTH        = 150;
    static final Integer  ALARM_BUTTON_HEIGHT       = 32;
    static final Double   TIMEBOX_DEFAULT_FONTSIZE  = 32.0;
    static final Double   DATEBOX_DEFAULT_FONTSIZE  = 22.0;


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
            "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #0000ff 100%)," +
            "linear-gradient(#dddddd 0%, #f6f6f6 50%);";

    static final String ALARM_BUTTON_SET_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #0000ff 100%)," +
        "linear-gradient(#dddddd 0%, yellow 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";

    static final String ALARM_BUTTON_SETTING_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #0000ff 100%)," +
        "linear-gradient(#dddddd 20%, #4D9EED 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";

    static final String ALARM_BUTTON_RINGING_STYLE =
        "-fx-background-color:" +
        "linear-gradient(#f2f2f2, #4D9EED)," +
        "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #0000ff 100%)," +
        "linear-gradient(#dddddd 20%, red 50%);" +
        "-fx-background-radius: 8,7,6;" +
        "-fx-background-insets: 0,1,2;" +
        "-fx-text-fill: black;" +
        "-fx-effect: dropshadow(three-pass-box, " +
            "rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);";


    /** *********************************************************************
     * Global App State Enums.
     **/
    enum APPSTATE {
        NEVER_ACTIVE("NEVER_ACTIVE"),
        ALARM_NOT_SET("ALARM_NOT_SET"),
        SETTING_ALARM("SETTING_ALARM"),
        ALARM_SET("ALARM_SET"),
        ALARM_RINGING("ALARM_RINGING");

        APPSTATE(String saveAs) {
            this.stateString = saveAs;
        }

        private final String stateString;
        public String getStringValue() {
            return this.stateString;
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
    }

    /** *********************************************************************
     * Global App var.
     **/
    static boolean mShutdownNormal = false;
    static final Timer mSecondTimer = new Timer();
    static final Preferences mPref = Preferences.userRoot().node(WINDOW_TITLE);
    static Clip mAlarmAudioClip;

    // Main fxClock UI frame.
    static Stage mApplication;
        static Image mApplicationIcon;
        static BufferedImage mNewApplicationIcon;

        static ImageView mGnomeImageView;
        static HBox mTimeDateBox;
            static Label mTimeLabel;
            static Label mDateLabel;

        static Button mAlarmButton;

    // Time label font size popup control.
    static Alert mTimeBoxFontAlert;
        static Slider mTimeBoxFontSlider;
    static Alert mDateBoxFontAlert;
        static Slider mDateBoxFontSlider;

    // Settings fxClock UI frame;
    static Alert mAlarmDialog;
        static VBox mAlarmEditBox;
        static LocalDateTimePicker mAlarmPicker;


    /** *********************************************************************
     * Main Start stage. Set title, icon, etc.
     **/
    @Override
    public void start(Stage stage) {
        mApplication = stage;
        mApplication.setTitle(WINDOW_TITLE);

        mNewApplicationIcon = createApplicationIcon();
        setApplicationIcon(mApplication);
        mAlarmAudioClip = createAlarmAudioClip();

        restoreApplicationProperties(mApplication);

        mApplication.setScene(new Scene(createApplicationScene(),
            WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT));
        mTimeBoxFontAlert =
            createNewFontDialog("Time Fontsize", getTimeBoxFontSize());
        mTimeBoxFontAlert.initOwner(mApplication.getScene().getWindow());
        mDateBoxFontAlert =
            createNewFontDialog("Date Fontsize", getDateBoxFontSize());
        mDateBoxFontAlert.initOwner(mApplication.getScene().getWindow());

        createApplicationPropertyListeners(mApplication);

        mApplication.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (getAppState() == APPSTATE.SETTING_ALARM) {
                    mAlarmDialog.close();
                }
                mTimeBoxFontAlert.close();
                mDateBoxFontAlert.close();
            }
        });

        setApplicationShutdownHook();
        mApplication.show();

        mAlarmDialog = createAlarmDialog();
        mAlarmDialog.initOwner(mApplication.getScene().getWindow());

        startSecondTimer(mApplication);
    }

    /** *********************************************************************
     * Main Stop for normal app close.
     **/
    @Override
    public void stop() {
        mShutdownNormal = true;

        // Cancel main timer. (This method may be called repeatedly.)
        mSecondTimer.cancel();
    }

    /** *********************************************************************
     * Creates resource of clock face with current time.
     **/
    public BufferedImage createApplicationIcon() {
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

        // <!-- Clock center, small circle -->
        gc.setLineWidth(1);
        gc.setFill(Color.BLUE);
        gc.fillOval(46, 46, 4, 4);

        // Set background transparent and return result.
        final SnapshotParameters snapParms = new SnapshotParameters();
        snapParms.setFill(Color.TRANSPARENT);

        final WritableImage writableImage = new WritableImage(
            WINDOW_ICON_PNG_WIDTH, WINDOW_ICON_PNG_HEIGHT);
        canvas.snapshot(snapParms, writableImage);

        return SwingFXUtils.fromFXImage((Image) writableImage, null);
    }

    /** *********************************************************************
     * Helper method, loads Window Icon from where we've
     * Created resource of clock face with current time.
     **/
    public void setApplicationIcon(Stage app) {
        // Remove one we previously set.
        if (mApplicationIcon != null) {
            app.getIcons().remove(mApplicationIcon);
        }

        // Set the new one.
        try {
            mApplicationIcon = SwingFXUtils.toFXImage(mNewApplicationIcon, null);
            app.getIcons().add(mApplicationIcon);
        } catch (Exception e) {
            System.out.println(
                "fxClock: setApplicationIcon() Setting window icon fails: \n" + e);
        }
    }

    /** *********************************************************************
     * Load audio clip for alarm.
     **/
    public Clip createAlarmAudioClip() {
        Clip alarmAudioClip = null;

        try {
            alarmAudioClip = AudioSystem.getClip();
            alarmAudioClip.open(AudioSystem.getAudioInputStream(
                new File(ALARM_SOUND_FOR_APP)));
        } catch (IOException | LineUnavailableException |
                 UnsupportedAudioFileException e) {
            System.out.println(
                "fxClock: start() Alarm beep audio sound is unavailable.");
        }

        return alarmAudioClip;
    }

    /** *********************************************************************
     * Restore window property changes @ app start / restart.
     **/
    public void restoreApplicationProperties(Stage app) {
        app.setX(getWindowPosX());
        app.setY(getWindowPosY());

        app.setWidth(getWindowWidth());
        app.setHeight(getWindowHeight());

        app.setAlwaysOnTop(getWindowOnTopValue());
    }

    /** *********************************************************************
     * Create main Form / display scene ... Date/time and alarm button.
     **/
    public VBox createApplicationScene() {
        final VBox sceneBox = new VBox();
        sceneBox.setAlignment(Pos.CENTER);

        mTimeDateBox = updateTimeDateBox(new HBox());

        sceneBox.getChildren().add(mTimeDateBox);
        sceneBox.getChildren().add(getNewSpacer());

        mAlarmButton = createAlarmButton();
        sceneBox.getChildren().add(mAlarmButton);

        return sceneBox;
    }

    /** *********************************************************************
     * Update Time/date box for application scene.
     **/
    public HBox updateTimeDateBox(HBox timeDateBox) {
        timeDateBox.setAlignment(Pos.CENTER);

        // Add Icon to Window contents as GNOME doesn't use it in the titlebar.
        if (System.getenv("XDG_SESSION_DESKTOP").contains("GNOME")) {
            if (mGnomeImageView != null) {
                timeDateBox.getChildren().remove(mGnomeImageView);
            }
            mGnomeImageView = new ImageView(mApplicationIcon);
            HBox.setMargin(mGnomeImageView, GNOME_IMAGE_MARGIN_INSETS);
            mGnomeImageView.setPreserveRatio(true);
            mGnomeImageView.setFitWidth(GNOME_ICON_WIDTH);
            mGnomeImageView.setFitHeight(GNOME_ICON_HEIGHT);
            timeDateBox.getChildren().add(mGnomeImageView);
        }

        if (mTimeLabel != null) {
            timeDateBox.getChildren().remove(mTimeLabel);
        }
        if (mDateLabel != null) {
            timeDateBox.getChildren().remove(mDateLabel);
        }

        final LocalDateTime ldt =
            LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        mTimeLabel = new Label(getNNWithLeadZero(ldt.getHour()) + ":" +
            getNNWithLeadZero(ldt.getMinute()) + " ");
        mTimeLabel.setFont(new Font(getTimeBoxFontSize()));

        mTimeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Optional<ButtonType> result = mTimeBoxFontAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    final Slider timeFontSlider = (Slider) mTimeBoxFontAlert.getDialogPane().getContent();
                    setTimeBoxFontSize(timeFontSlider.getValue());
                    mTimeLabel.setFont(new Font(getTimeBoxFontSize()));
                }
            }
        });
        timeDateBox.getChildren().add(mTimeLabel);

        mDateLabel = new Label(MONTH_NAMES[ldt.getMonthValue() - 1] + " " +
            getNNWithLeadZero(ldt.getDayOfMonth()));
        mDateLabel.setFont(new Font(getDateBoxFontSize()));

        mDateLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Optional<ButtonType> result = mDateBoxFontAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    final Slider dateFontSlider = (Slider) mDateBoxFontAlert.getDialogPane().getContent();
                    setDateBoxFontSize(dateFontSlider.getValue());
                    mDateLabel.setFont(new Font(getDateBoxFontSize()));
                }
            }
        });
        timeDateBox.getChildren().add(mDateLabel);

        return timeDateBox;
    }

    /** *********************************************************************
     * Get new Alarm Button for application scene box.
     **/
    public Button createAlarmButton() {
        final Button alarmButton = new Button("Alarm");
        alarmButton.setFont(new Font(ALARM_BUTTON_FONT_SIZE));
        alarmButton.setMinWidth(ALARM_BUTTON_WIDTH);
        alarmButton.setMinHeight(ALARM_BUTTON_HEIGHT);
        alarmButton.setAlignment(Pos.CENTER);

        alarmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                onAlarmButtonClicked();
            }
        });

        switch (getAppState()) {
            case ALARM_NOT_SET:
                alarmButton.setStyle(ALARM_BUTTON_STYLE);
                alarmButton.setText("Alarm");
                break;

            case ALARM_SET:
                alarmButton.setStyle(ALARM_BUTTON_SET_STYLE);
                alarmButton.setText(getStyledAlarmString(getAlarmValue()));
                break;

            case ALARM_RINGING:
                alarmButton.setStyle(ALARM_BUTTON_RINGING_STYLE);
                alarmButton.setText(getStyledAlarmString(getAlarmValue()));
                if (mAlarmAudioClip != null) {
                    if (mAlarmAudioClip.isRunning() == false) {
                        mAlarmAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
                break;
        }

        return alarmButton;
    }

    /** *********************************************************************
     * Alarm button on clicked().
     **/
    public void onAlarmButtonClicked() {

        if (getAppState() == APPSTATE.ALARM_NOT_SET) {
            setAppState(APPSTATE.SETTING_ALARM);
            mAlarmButton.setStyle(ALARM_BUTTON_SETTING_STYLE);
            removeAlarmValue();
            mAlarmPicker.setLocalDateTime(getAlarmValue());
            mAlarmDialog.show();
            return;
        }

        if (getAppState() == APPSTATE.SETTING_ALARM) {
            setAppState(APPSTATE.ALARM_NOT_SET);
            mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            mAlarmButton.setText("Alarm");
            removeAlarmValue();
            mAlarmDialog.close();
            return;
        }

        if (getAppState() == APPSTATE.ALARM_SET) {
            setAppState(APPSTATE.ALARM_NOT_SET);
            mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            mAlarmButton.setText("Alarm");
            removeAlarmValue();
            return;
        }

        if (getAppState() == APPSTATE.ALARM_RINGING) {
            setAppState(APPSTATE.ALARM_NOT_SET);
            mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
            removeAlarmValue();
            if (mAlarmAudioClip != null) {
                mAlarmAudioClip.stop();
            }
            mAlarmButton.setText("Alarm");
            return;
        }
    }

    /** *********************************************************************
     * NewFontDialogs are used to change field font sizes.
     **/
    public Alert createNewFontDialog(String title, Double fontSize) {
        Alert fontAlert = new Alert(AlertType.CONFIRMATION);
        fontAlert.setTitle(title);

        fontAlert.setHeaderText(null);
        fontAlert.setGraphic(null);
        fontAlert.initModality(Modality.APPLICATION_MODAL);

        final Slider slider = new Slider(DISPLAY_FONTSIZE_MINIMUM,
            DISPLAY_FONTSIZE_MAXIMUM, fontSize);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        fontAlert.getDialogPane().setContent(slider);
        return fontAlert;
    }

    /** *********************************************************************
     * Capture application property changes for restart.
     **/
    public void createApplicationPropertyListeners(Stage app) {
        app.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue o, Number oV, Number newValue) {
                setWindowPosX(newValue.doubleValue());
            }});
        app.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue o, Number oV, Number newValue) {
                setWindowPosY(newValue.doubleValue());
            }});

        app.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue o, Number oV, Number newValue) {
                setWindowWidth(newValue.doubleValue());
            }});
        app.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue o, Number oV, Number newValue) {
                setWindowHeight(newValue.doubleValue());
            }});

        app.alwaysOnTopProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue o, Boolean oV, Boolean newValue) {
                setWindowOnTopValue(newValue);
            }});
    }

    /** *********************************************************************
     * Application shutdown hook executes after either/or ;
     *     A)   Normal app shutdown & we've executed stop()
     *     B) Abnormal app shutdown (proc kill) & we've NOT executed stop()
     **/
    public void setApplicationShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Cancel main timer. (This method may be called repeatedly.)
                mSecondTimer.cancel();

                switch (getAppState()) {
                    case SETTING_ALARM:
                        setAppState(APPSTATE.ALARM_NOT_SET);
                        removeAlarmValue();
                        break;

                    case ALARM_RINGING:
                        if (mAlarmAudioClip != null) {
                            mAlarmAudioClip.stop();
                        }
                        break;
                }
            }
        });
    }

    /** *********************************************************************
     * Alarm Dialog to set & edit Alarm date / time.
     **/
    public Alert createAlarmDialog() {
        final Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle("Set Alarm Date & Time");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.initModality(Modality.NONE);

        mAlarmPicker = new LocalDateTimePicker();
        mAlarmEditBox = createAlarmEditBox(mAlarmPicker);

        alert.getDialogPane().setContent(mAlarmEditBox);

        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent e) {
                final ButtonType button = alert.getResult();
                if (button == null || button == ButtonType.CANCEL) {
                    setAppState(APPSTATE.ALARM_NOT_SET);
                    mAlarmButton.setStyle(ALARM_BUTTON_STYLE);
                    removeAlarmValue();
                    mAlarmButton.setText("Alarm");
                    return;
                }

                if (button == ButtonType.OK) {
                    setAppState(APPSTATE.ALARM_SET);
                    mAlarmButton.setStyle(ALARM_BUTTON_SET_STYLE);
                    setAlarmValue(mAlarmPicker.getLocalDateTime());
                    mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
                    return;
                }
            }
        });

        return alert;
    }

    /** *********************************************************************
     * Get new Alarm Edit box for Alarm Dialog.
     **/
    public VBox createAlarmEditBox(LocalDateTimePicker alarmPicker) {
        final VBox alarmEditBox = new VBox();

        alarmEditBox.setAlignment(Pos.CENTER);
        alarmEditBox.setMaxWidth(300.0);
        alarmEditBox.getChildren().add(alarmPicker);

        return alarmEditBox;
    }

    /** *********************************************************************
     * Application main timer, once per second.
     *     Alarm trigger is checked each second immediately.
     *     Clock image for icons updates each new minute.
     **/
    public void startSecondTimer(Stage app) {
        mSecondTimer.scheduleAtFixedRate(new TimerTask() {
            Integer mTimerPrevMin;
            @Override
            public void run() {
                // Update app with new clockFace icon once a minute.
                final Integer nowMinute = LocalDateTime.ofInstant(
                    Instant.now(), ZoneId.systemDefault()).getMinute();

                if (mTimerPrevMin == null || !mTimerPrevMin.equals(nowMinute)) {
                    mTimerPrevMin = nowMinute;
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            mNewApplicationIcon = createApplicationIcon();
                            setApplicationIcon(app);
                            updateTimeDateBox(mTimeDateBox);
                        }
                    });
                }

                // Check if alarm has gone off.
                if (getAppState() == APPSTATE.ALARM_SET) {
                    if (getAlarmValue().isBefore(LocalDateTime.now())) {
                        setAppState(APPSTATE.ALARM_RINGING);
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                mAlarmButton.setStyle(ALARM_BUTTON_RINGING_STYLE);
                                mAlarmButton.setText(getStyledAlarmString(getAlarmValue()));
                            }
                        });
                        if (mAlarmAudioClip != null) {
                            if (mAlarmAudioClip.isRunning() == false) {
                                mAlarmAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
                            }
                        }
                    }
                }
            }
        }, 0, 1000 /* per-second */);
    }

    /** *********************************************************************
     * Helper methods ... all Preferences getter / setters.
     **/
    public APPSTATE getAppState() {
        return APPSTATE.appStateValueOf(mPref.get(APP_STATE_PREFNAME,
            APP_DEFAULT_STATE.getStringValue()));
    }
    public void setAppState(APPSTATE state) {
        mPref.put(APP_STATE_PREFNAME, state.getStringValue());
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Boolean getWindowOnTopValue() {
        return mPref.getBoolean(WINDOW_ONTOP_PREFNAME, WINDOW_ONTOP_DEFAULT);
    }
    public void setWindowOnTopValue(Boolean onTopValue) {
        mPref.putBoolean(WINDOW_ONTOP_PREFNAME, onTopValue);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public LocalDateTime getAlarmValue() {
        return getLDTFromString(mPref.get(ALARM_VALUE_PREFNAME,
            getStringFromLDT(LocalDateTime.now())));
    }
    public void setAlarmValue(LocalDateTime alarmValue) {
        mPref.put(ALARM_VALUE_PREFNAME, getStringFromLDT(alarmValue));
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }
    public void removeAlarmValue() {
        mPref.remove(ALARM_VALUE_PREFNAME);
    }

    public Double getWindowPosX() {
        return mPref.getDouble(WINDOW_POS_X_PREFNAME, WINDOW_DEFAULT_POS_X);
    }
    public void setWindowPosX(Double x) {
        mPref.putDouble(WINDOW_POS_X_PREFNAME, x);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Double getWindowPosY() {
        return mPref.getDouble(WINDOW_POS_Y_PREFNAME, WINDOW_DEFAULT_POS_Y);
    }
    public void setWindowPosY(Double y) {
        mPref.putDouble(WINDOW_POS_Y_PREFNAME, y);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Double getWindowWidth() {
        return mPref.getDouble(WINDOW_WIDTH_PREFNAME, WINDOW_DEFAULT_WIDTH);
    }
    public void setWindowWidth(Double w) {
        mPref.putDouble(WINDOW_WIDTH_PREFNAME, w);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Double getWindowHeight() {
        return mPref.getDouble(WINDOW_HEIGHT_PREFNAME, WINDOW_DEFAULT_HEIGHT);
    }
    public void setWindowHeight(Double h) {
        mPref.putDouble(WINDOW_HEIGHT_PREFNAME, h);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Double getTimeBoxFontSize() {
        return mPref.getDouble(TIMEBOX_FONTSIZE_PREFNAME, TIMEBOX_DEFAULT_FONTSIZE);
    }
    public void setTimeBoxFontSize(Double s) {
        mPref.putDouble(TIMEBOX_FONTSIZE_PREFNAME, s);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    public Double getDateBoxFontSize() {
        return mPref.getDouble(DATEBOX_FONTSIZE_PREFNAME, DATEBOX_DEFAULT_FONTSIZE);
    }
    public void setDateBoxFontSize(Double s) {
        mPref.putDouble(DATEBOX_FONTSIZE_PREFNAME, s);
        try {
            mPref.flush(); // seriously reuired.
        } catch (BackingStoreException e) {
            throw new IllegalStateException(
                "Java VM Preferences services are unavailable to this app - fatal.");
        }
    }

    /** *********************************************************************
     * Helper method to return a spacer box.
     **/
    public HBox getNewSpacer() {
        final HBox spacerBox = new HBox();
        spacerBox.setPrefHeight(15);
        spacerBox.setPrefWidth(15);
        return spacerBox;
    }

    /** *********************************************************************
     * Helper method, format number ( < 60 ) as two-digit with leading zero.
     **/
    public String getNNWithLeadZero(Integer number) {
        return (number < 10) ?
            "0" + number.toString() :
            number.toString();
    }

    /** *********************************************************************
     * Helper methods to Format LocalDateTime to String
     * and String to LocalDateTime.
     **/
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
}
