.PHONY: build
build:
	bash script/check_version.sh
	./gradlew fatJar

.PHONY: test
test:
	bash script/check_version.sh
	./gradlew test
	./gradlew jacocoTestReport

.PHONY: run-example
run-example:
	bash script/check_version.sh
	java -jar build/libs/Zephyr-1.0.0.jar -i examples/main_example.ze