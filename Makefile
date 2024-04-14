.PHONY: build
build:
	./gradlew fatJar

.PHONY: test
test:
	./gradlew test

.PHONY: run-example
run-example:
	java -jar build/libs/Zephyr-1.0.0.jar -i examples/hello_world.ze