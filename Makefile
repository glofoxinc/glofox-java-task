# JDK 21+. Nothing is downloaded on first run.
# lib/junit-platform-console-standalone-6.1.3.jar is JUnit 6.1.3 (EPL-2.0):
# https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.1.3/

.PHONY: run test clean

run:
	rm -rf build
	mkdir -p build
	javac --release 21 -encoding UTF-8 -d build $(shell find src/main/java -name '*.java')
	java -cp build com.glofox.api.Main

test:
	rm -rf build
	mkdir -p build
	javac --release 21 -encoding UTF-8 -cp lib/junit-platform-console-standalone-6.1.3.jar -d build \
		$(shell find src/main/java src/test/java -name '*.java')
	java -jar lib/junit-platform-console-standalone-6.1.3.jar execute \
		-cp build --scan-class-path --disable-banner

clean:
	rm -rf build
