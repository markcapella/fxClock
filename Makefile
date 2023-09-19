
# *****************************************************
# Variables to control Makefile operation.

JCOMPILER = javac
JFLAGS = \
	--module-path /snap/openjfx/current/sdk/lib/ \
	--add-modules javafx.controls,javafx.swing,javafx.media

JRUNTIME = java

# ****************************************************
# Targets needed to build the executable from the source folder.

fxClock: fxClock.java
	@echo
	@echo "Build starts ..."
	@echo

	@if [ ! -d "/snap/openjfx/current" ]; then \
		echo "Error! The openjfx package is not installed, but is required."; \
		echo "   try 'sudo snap install openjfx', then re-run this make."; \
		echo ""; \
		exit 1; \
	fi

	$(JCOMPILER) $(JFLAGS) fxClock.java LocalDateTimePicker.java LocalDateTimePickerSkin.java CalendarPicker.java DateTimeToCalendarHelper.java CalendarPickerControlSkin.java \
		CssMetaDataForSkinProperty.java SimpleDateFormatConverter.java CalendarPickerControlSkin.java CalendarTimePicker.java CalendarTimePickerSkin.java \
		GridPane.java NodeUtil.java CalendarPickerMonthlySkinAbstract.java GenericLayoutConstraints.java ListSpinner.java ListSpinnerSkin.java \
		Timer.java HBox.java VBox.java

	@echo
	@echo "Build Done !"
	@echo

# ****************************************************
# Target needed to run the executable from the source folder.

run: fxClock
	@echo
	@echo "Run: starts ..."
	@echo

	@if [ ! -d "/snap/openjfx/current" ]; then \
		echo "Error! The openjfx package is not installed, but is required."; \
		echo "   try 'sudo snap install openjfx', then re-run this make."; \
		echo ""; \
		exit 1; \
	fi

	$(JRUNTIME) $(JFLAGS) fxClock

	@echo
	@echo "Run Done !"
	@echo

# ****************************************************
# Target needed to install the executable.

install: fxClock
ifneq ($(shell id -u), 0)
	@echo "You must be root to perform this action. Please re-run with:"
	@echo "   sudo make install"
	@echo
	@exit 1;
endif

	@echo
	@echo "Install: starts ..."
	@echo

	cp 'startFxClock' /usr/local/bin/
	chmod +x /usr/local/bin/startFxClock
	@echo

	cp 'stopFxClock' /usr/local/bin/
	chmod +x /usr/local/bin/stopFxClock
	@echo

	mkdir -p /usr/local/fxClock
	cp *.class /usr/local/fxClock
	cp *.css /usr/local/fxClock
	cp 'alarmBeep.wav' /usr/local/fxClock
	@echo

	cp 'fxclock.desktop' /usr/share/applications/
	cp 'fxclock.png' /usr/share/icons/hicolor/48x48/apps/
	@echo

	sudo -u ${SUDO_USER} \
		rm -rf /home/${SUDO_USER}/.java/.userPrefs/fxClock
	@echo

	@echo "Install Done !"
	@echo

# ****************************************************
# Target needed to uninstall the executable.

uninstall:
ifneq ($(shell id -u), 0)
	@echo "You must be root to perform this action. Please re-run with:"
	@echo "   sudo make uninstall"
	@echo
	@exit 1;
endif

	@echo
	@echo "Uninstall: starts ..."
	@echo

	rm -f /usr/local/bin/startFxClock
	rm -f /usr/local/bin/stopFxClock
	@echo

	rm -rf /usr/local/fxClock
	@echo

	rm -f /usr/share/applications/fxclock.desktop
	rm -f /usr/share/icons/hicolor/48x48/apps/fxclock.png
	@echo

	sudo -u ${SUDO_USER} \
		rm -rf /home/${SUDO_USER}/.java/.userPrefs/fxClock
	@echo

	@echo "Uninstall Done !"
	@echo

# ****************************************************
# Target needed to clean the source folder for a fresh make.

clean:
	@echo
	@echo "Clean: starts ..."
	@echo

	rm -f *.class

	rm -rf ~/.java/.userPrefs/fxClock

	@echo
	@echo "Clean Done !"
	@echo
