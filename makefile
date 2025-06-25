
# *****************************************************
# Variables to control Makefile operation.

APP_NAME = "fxClock"
APP_VERSION = "2025-06-23"
APP_AUTHOR = "Mark James Capella"

# Color styling.
COLOR_RED = \033[0;31m
COLOR_GREEN = \033[1;32m
COLOR_YELLOW = \033[1;33m
COLOR_BLUE = \033[1;34m
COLOR_NORMAL = \033[0m

OPENJFX = /usr/share/openjfx/lib

JCOMPILER = javac
JAVAC = $(shell which javac)
JFLAGS = --module-path $(OPENJFX) --add-modules \
	javafx.controls,javafx.swing,javafx.media

JRUNTIME = java

# ****************************************************
# Targets needed to build the executable from the source folder.
#
all:
	@if [ -z $(JAVAC) ]; then \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) The openjdk"\
			"package is not installed,"; \
		echo "   but is required to compile."; \
		echo ""; \
		echo "   Try 'apt-file search bin/javac', then"\
			"sudo apt install the highest"; \
		echo "   displayed headless package available to you."; \
		echo ""; \
		echo  "Try:"; \
		echo "   $(COLOR_GREEN)sudo apt install"\
			"openjdk-21-jdk-headless$(COLOR_NORMAL)"; \
		echo ""; \
		exit 1; \
	fi

	@if [ ! -d $(OPENJFX) ]; then \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) The openjfx"\
			"package is not installed,"; \
		echo "   but is required to compile."; \
		echo ""; \
		echo  "Try:"; \
		echo "   $(COLOR_GREEN)sudo apt install openjfx$(COLOR_NORMAL)"; \
		echo "   then re-run this make."; \
		echo ""; \
		exit 1; \
	fi

	@if [ "$(shell id -u)" = 0 ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) You must not"\
			"be root to perform this action."; \
		echo; \
		echo  "Please re-run with:"; \
		echo "   $(COLOR_GREEN)make$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@echo
	@echo "Build starts ..."
	@echo

	$(JCOMPILER) $(JFLAGS) fxClock.java LocalDateTimePicker.java \
		LocalDateTimePickerSkin.java CalendarPicker.java \
		DateTimeToCalendarHelper.java CalendarPickerControlSkin.java \
		CssMetaDataForSkinProperty.java SimpleDateFormatConverter.java \
		CalendarPickerControlSkin.java CalendarTimePicker.java \
		CalendarTimePickerSkin.java GridPane.java NodeUtil.java \
		CalendarPickerMonthlySkinAbstract.java \
		GenericLayoutConstraints.java ListSpinner.java \
		ListSpinnerSkin.java Timer.java HBox.java VBox.java


	@echo "true" > "BUILD_COMPLETE"

	@echo
	@echo "Build Done !"
	@echo

# ****************************************************
# Target needed to run the executable from the source folder.
#
run:
	@if [ "$(shell id -u)" = 0 ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) You must not"\
			"be root to perform this action."; \
		echo; \
		echo  "Please re-run with:"; \
		echo "   $(COLOR_GREEN)make run$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@echo
	@echo "$(COLOR_BLUE)Build Starts.$(COLOR_NORMAL)"
	@echo

	@if [ ! -d $(OPENJFX) ]; then \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) The openjfx"\
			"package is not installed,"; \
		echo "   but is required to run."; \
		echo ""; \
		echo  "Try:"; \
		echo "   $(COLOR_GREEN)sudo apt install openjfx$(COLOR_NORMAL)"; \
		echo "   then re-run."; \
		echo ""; \
		exit 1; \
	fi

	$(JRUNTIME) $(JFLAGS) fxClock

	@echo
	@echo "$(COLOR_BLUE)Run Done.$(COLOR_NORMAL)"

# ****************************************************
# Target needed to install the executable.
#
install:
	@if [ ! -f BUILD_COMPLETE ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) Nothing"\
			"currently built to install."; \
		echo; \
		echo "Please make this project first, with:"; \
		echo "   $(COLOR_GREEN)make$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@if ! [ "$(shell id -u)" = 0 ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) You must"\
			"be root to perform this action."; \
		echo; \
		echo  "Please re-run with:"; \
		echo "   $(COLOR_GREEN)sudo make install$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@echo
	@echo "$(COLOR_BLUE)Install Starts.$(COLOR_NORMAL)"
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

	@echo
	@echo "$(COLOR_BLUE)Install Done.$(COLOR_NORMAL)"

# ****************************************************
# Target needed to uninstall the executable.
#
uninstall:
	@if ! [ "$(shell id -u)" = 0 ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) You must"\
			"be root to perform this action."; \
		echo; \
		echo  "Please re-run with:"; \
		echo "   $(COLOR_GREEN)sudo make uninstall$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@echo
	@echo "$(COLOR_BLUE)Uninstall Starts.$(COLOR_NORMAL)"
	@echo

	rm -f /usr/local/bin/startFxClock
	rm -f /usr/local/bin/stopFxClock
	@echo

	rm -rf /usr/local/fxClock
	@echo

	rm -f /usr/share/applications/fxclock.desktop
	rm -f /usr/share/icons/hicolor/48x48/apps/fxclock.png
	@echo

	@echo
	@echo "$(COLOR_BLUE)Uninstall Done.$(COLOR_NORMAL)"

# ****************************************************
# Target needed to clean the source folder for a fresh make.
#
clean:
	@if [ "$(shell id -u)" = 0 ]; then \
		echo; \
		echo "$(COLOR_RED)Error!$(COLOR_NORMAL) You must not"\
			"be root to perform this action."; \
		echo; \
		echo  "Please re-run with:"; \
		echo "   $(COLOR_GREEN)make clean$(COLOR_NORMAL)"; \
		echo; \
		exit 1; \
	fi

	@echo
	@echo "$(COLOR_BLUE)Clean Starts.$(COLOR_NORMAL)"
	@echo

	rm -f *.class

	@rm -f "BUILD_COMPLETE"

	@echo
	@echo "$(COLOR_BLUE)Clean Done.$(COLOR_NORMAL)"
