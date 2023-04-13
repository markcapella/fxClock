import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;


public class DateTimePicker extends DatePicker {
    public static final String mDefaultFormat = "HH:mm yyyy-MM-dd";

    private ObjectProperty<LocalDateTime> mDateTimeValue =
        new SimpleObjectProperty<>(LocalDateTime.now());

    private DateTimeFormatter mFormatter;

    private ObjectProperty<String> mFormat = new SimpleObjectProperty<String>() {
        public void set(String newValue) {
            super.set(newValue);
            mFormatter = DateTimeFormatter.ofPattern(newValue);
        }
    };

    public DateTimePicker() {
        getStyleClass().add("datetime-picker");
        setFormat(mDefaultFormat);
        setConverter(new InternalConverter());

        // Syncronize changes to the underlying date value back to the mDateTimeValue.
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                mDateTimeValue.set(null);
            } else {
                if (mDateTimeValue.get() == null) {
                    mDateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
                } else {
                    LocalTime time = mDateTimeValue.get().toLocalTime();
                    mDateTimeValue.set(LocalDateTime.of(newValue, time));
                }
            }
        });

        // Syncronize changes to mDateTimeValue back to the underlying date value.
        mDateTimeValue.addListener((observable, oldValue, newValue) -> {
            setValue(newValue == null ? null : newValue.toLocalDate());
        });

        // Persist changes onblur.
        getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                simulateEnterPressed();
            }
        });

    }

    private void simulateEnterPressed() {
        getEditor().fireEvent(new KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED,
            null, null, KeyCode.ENTER, false, false, false, false));
    }

    public LocalDateTime getDateTimeValue() {
        return mDateTimeValue.get();
    }

    public void setDateTimeValue(LocalDateTime mDateTimeValue) {
        if (mDateTimeValue.isAfter(LocalDateTime.of(1971, 6, 30, 12, 00)))
            this.mDateTimeValue.set(mDateTimeValue);
        else
            this.mDateTimeValue.set(null);
    }

    public ObjectProperty<LocalDateTime> dateTimeValueProperty() {
        return mDateTimeValue;
    }

    public String getFormat() {
        return mFormat.get();
    }

    public ObjectProperty<String> formatProperty() {
        return mFormat;
    }

    public void setFormat(String mFormat) {
        this.mFormat.set(mFormat);
    }


class InternalConverter extends StringConverter<LocalDate> {
    public String toString(LocalDate object) {

        LocalDateTime value = getDateTimeValue();
        return (value != null) ? value.format(mFormatter) : "";
    }

    public LocalDate fromString(String value) {
        if (value == null) {
            mDateTimeValue.set(null);
            return null;
        }

        mDateTimeValue.set(LocalDateTime.parse(value, mFormatter));
        return mDateTimeValue.get().toLocalDate();
        }
    }
}