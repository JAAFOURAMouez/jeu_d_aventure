JC = javac
JFLAGS = -d bin/

# Trouver tous les fichiers source
SOURCES = $(shell find src/ -name "*.java")

all:
	@mkdir -p bin/
	@echo $(SOURCES) > sources.txt
	$(JC) $(JFLAGS) @sources.txt

run:
	java -cp bin/ main.Main

clean:
	rm -rf bin/
	rm -f sources.txt
