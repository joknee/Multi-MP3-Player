package sample;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

public class Controller
{
    @FXML private Slider timeSlider;
    @FXML private ImageView playBtn;
    @FXML private ImageView pauseBtn;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Button browseBtn;
    @FXML private ToggleButton drumsSoloBtn;
    @FXML private ToggleButton drumsMuteBtn;
    @FXML private ToggleButton bassSoloBtn;
    @FXML private ToggleButton bassMuteBtn;
    @FXML private ToggleButton gtrSoloBtn;
    @FXML private ToggleButton gtrMuteBtn;
    @FXML private ToggleButton voxSoloBtn;
    @FXML private ToggleButton voxMuteBtn;
    @FXML private Pane pane;
    @FXML private Label trackOneLabel;
    @FXML private Label trackTwoLabel;
    @FXML private Label trackThreeLabel;
    @FXML private Label trackFourLabel;
    private boolean atEndOfMedia = false;

    File selectedFile;
    Media trackOne;
    MediaPlayer player;
    MediaPlayer.Status status;
    Duration duration;

    public void browseAction(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open MP3 File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));

        selectedFile = fileChooser.showOpenDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            trackOneLabel.setText(selectedFile.getName());
            trackOne = new Media(selectedFile.toURI().toString());
            player = new MediaPlayer(trackOne);
            player.setAutoPlay(false);
        }
    }

    public void playClick(Event event)
    {
        updateValues();
        status = player.getStatus();

        if (status == MediaPlayer.Status.UNKNOWN
                || status == MediaPlayer.Status.HALTED)
        {
            return;
        }

        if (status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED)
        {
            if (atEndOfMedia)
            {
                player.seek(player.getStartTime());
                atEndOfMedia = false;
                updateValues();
            }
            player.play();
        }

        player.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue)->
        {
            updateValues();
        });

        player.setOnReady(() ->
        {
            duration = player.getMedia().getDuration();
            updateValues();
        });

        timeSlider.valueProperty().addListener((Observable ov) ->
        {
            if (timeSlider.isValueChanging())
            {
                if (duration != null)
                {
                    player.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
                updateValues();
            }
        });
    }

    public void pauseClick(Event event)
    {
        status = player.getStatus();
        if (status != MediaPlayer.Status.PAUSED
                || status != MediaPlayer.Status.READY
                || status != MediaPlayer.Status.STOPPED)
        {
            player.pause();
        }
    }

    protected void updateValues()
    {
        if (timeSlider != null && duration != null)
        {
            Platform.runLater(() ->
            {
                Duration currentTime = player.getCurrentTime();
                timeSlider.setDisable(duration.isUnknown());

                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging())
                {
                    timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
            });
        }
    }

    /*private static String formatTime(Duration elapsed, Duration duration)
    {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);

        if (elapsedHours > 0)
        {
            intElapsed -= elapsedHours * 60 * 60;
        }

        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO))
        {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);

            if (durationHours > 0)
            {
                intDuration -= durationHours * 60 * 60;
            }

            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0)
            {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes,
                        elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            }
            else
            {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        }
        else
        {
            if (elapsedHours > 0)
            {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            }
            else
            {
                return String.format("%02d:%02d",elapsedMinutes, elapsedSeconds);
            }
        }
    }*/

    public void colorToggle(ToggleButton tb, ToggleButton link)
    {
        String check = tb.toString().toLowerCase();
        if (check.contains("solo"))
        {
            if (tb.isSelected())
            {
                tb.setStyle("-fx-background-color: red");
                link.setSelected(false);
                link.setStyle(null);
            }
            else if (!tb.isSelected())
            {
                tb.setStyle(null);
            }
        }
        else if (check.contains("mute"))
        {
            if (tb.isSelected())
            {
                tb.setStyle("-fx-background-color: yellow");
                link.setSelected(false);
                link.setStyle(null);
            }
            else if (!tb.isSelected())
            {
                tb.setStyle(null);
            }
        }
    }

    public void drumsSolo(ActionEvent actionEvent)
    {
        colorToggle(drumsSoloBtn, drumsMuteBtn);
    }

    public void drumsMute(ActionEvent actionEvent)
    {
        colorToggle(drumsMuteBtn, drumsSoloBtn);
    }

    public void bassSolo(ActionEvent actionEvent)
    {
        colorToggle(bassSoloBtn, bassMuteBtn);
    }

    public void bassMute(ActionEvent actionEvent)
    {
        colorToggle(bassMuteBtn, bassSoloBtn);
    }

    public void gtrSolo(ActionEvent actionEvent)
    {
        colorToggle(gtrSoloBtn, gtrMuteBtn);
    }

    public void gtrMute(ActionEvent actionEvent)
    {
        colorToggle(gtrMuteBtn, gtrSoloBtn);
    }

    public void voxSolo(ActionEvent actionEvent)
    {
        colorToggle(voxSoloBtn, voxMuteBtn);
    }

    public void voxMute(ActionEvent actionEvent)
    {
        colorToggle(voxMuteBtn, voxSoloBtn);
    }

}
