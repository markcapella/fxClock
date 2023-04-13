
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

	$(JCOMPILER) $(JFLAGS) fxClock.java DateTimePicker.java

	@echo "Build Done !"

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

# ****************************************************
# Target needed to install the executable to user .local

install: fxClock
	# Kill any active instances. Installed and run-from-dev folder conflict
	# over shared ~/.java.userPrefs.
	for FILE in $$(pgrep java) ; do \
		ps -p $$FILE -o args --no-headers | egrep fxClock && kill $$FILE; \
	done

	rm -rf ~/.local/fxClock
	mkdir ~/.local/fxClock

	cp *.class ~/.local/fxClock
	cp 'okButton.png' ~/.local/fxClock
	cp 'cancelButton.png' ~/.local/fxClock
	cp 'alarmBeep.wav' ~/.local/fxClock

	cp 'fxClock.desktop' ~/Desktop

	cp 'fxClock.desktop.png' ~/.local/share/icons/hicolor/48x48/apps/

	rm -rf ~/.java/.userPrefs/fxClock

	@echo "Install Done !"

# ****************************************************
# Target needed to uninstall the executable from user .local

uninstall:
	# Kill any active instances.
	for FILE in $$(pgrep java) ; do \
		ps -p $$FILE -o args --no-headers | egrep fxClock && kill $$FILE; \
	done

	rm -rf ~/.local/fxClock

	rm -f ~/Desktop/fxClock.desktop

	rm -f ~/.local/share/icons/hicolor/48x48/apps/fxClock.desktop.png

	rm -rf ~/.java/.userPrefs/fxClock

	@echo "Uninstall Done !"

# ****************************************************
# Target needed to clean the source folder for a fresh make

clean:
	rm -f *.class

	rm -f 'fxClockGenerated.png'
	rm -f 'fxClock.instancelock'

	rm -rf ~/.java/.userPrefs/fxClock

	@echo "Clean Done !"
