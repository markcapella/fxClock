
# *****************************************************
# Variables to control Makefile operation

JCOMPILER = javac
JFLAGS = \
	--module-path /snap/openjfx/current/sdk/lib/ \
	--add-modules javafx.controls,javafx.swing,javafx.media

JRUNTIME = java

# ****************************************************
# Targets needed to build the executable from the source folder

fxClock: fxClock.java
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
		

	@echo "Build Done !"
	@echo

# ****************************************************
# Target needed to run the executable from the source folder

run: fxClock
	@if [ ! -d "/snap/openjfx/current" ]; then \
		echo "Error! The openjfx package is not installed, but is required."; \
		echo "   try 'sudo snap install openjfx', then re-run this make."; \
		echo ""; \
		exit 1; \
	fi

	$(JRUNTIME) $(JFLAGS) fxClock

	@echo "Run Done !"
	@echo

# ****************************************************
# Target needed to install the executable to user .local

install: fxClock
ifneq ($(shell id -u), 0)
	@echo "You must be root to perform this action. Please re-run with:"
	@echo "   sudo make install"
	@echo
	@exit 1;
endif

	@echo
	@echo "sudo make install: starts ..."

	mkdir -p /usr/local/fxClock

	cp *.class /usr/local/fxClock
	cp *.css /usr/local/fxClock

	cp 'okButton.png' /usr/local/fxClock
	cp 'cancelButton.png' /usr/local/fxClock
	cp 'alarmBeep.wav' /usr/local/fxClock

	cp 'fxClock.desktop' /usr/share/applications/
	cp 'fxClock.png' /usr/local/share/icons/hicolor/48x48/apps/

	sudo -u ${SUDO_USER} \
		mkdir -p /home/${SUDO_USER}/.local/fxClock
	sudo -u ${SUDO_USER} \
		rm -rf /home/${SUDO_USER}/.java/.userPrefs/fxClock

	@echo "Install Done !"
	@echo

# ****************************************************
# Target needed to uninstall the executable from user .local

uninstall:
ifneq ($(shell id -u), 0)
	@echo "You must be root to perform this action. Please re-run with:"
	@echo "   sudo make uninstall"
	@echo
	@exit 1;
endif

	@echo
	@echo "sudo make uninstall: starts ..."

	rm -rf /usr/local/fxClock

	rm -f /usr/share/applications/fxClock.desktop
	rm -f /usr/local/share/icons/hicolor/48x48/apps/fxClock.png

	sudo -u ${SUDO_USER} \
		rm -rf /home/${SUDO_USER}/.local/fxClock
	sudo -u ${SUDO_USER} \
		rm -rf /home/${SUDO_USER}/.java/.userPrefs/fxClock

	@echo "Uninstall Done !"
	@echo

# ****************************************************
# Target needed to clean the source folder for a fresh make

clean:
	rm -f *.class

	rm -f 'fxClockGenerated.png'
	rm -f 'fxClock.instancelock'

	rm -rf ~/.java/.userPrefs/fxClock

	@echo "Clean Done !"
	@echo
