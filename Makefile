artifact_name       := disqualified-officers-search-consumer
version             := "unversioned"

dependency_check_base_suppressions := common_suppressions_spring_6.xml
dependency_check_minimum_cvss := 4
dependency_check_assembly_analyzer_enabled := false
dependency_check_suppressions_repo_url := git@github.com:companieshouse/dependency-check-suppressions.git
suppressions_file := target/suppressions.xml

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f $(artifact_name)-*.zip
	rm -f $(artifact_name).jar
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit: clean
	mvn test


.PHONY: test-integration
test-integration:
	mvn integration-test -Dskip.unit.tests=true failsafe:verify

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar: dependency-check
	mvn sonar:sonar -Dsonar.dependencyCheck.htmlReportPath=./target/dependency-check-report.html

.PHONY: sonar-pr-analysis
sonar-pr-analysis: dependency-check
	mvn sonar:sonar -P sonar-pr-analysis -Dsonar.dependencyCheck.htmlReportPath=./target/dependency-check-report.html

.PHONY: dependency-check
dependency-check:
	@ if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		suppressions_home="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
	fi; \
	if [ ! -d "$${suppressions_home}" ]; then \
	    suppressions_home_target_dir="./target/dependency-check-suppressions"; \
		if [ -d "$${suppressions_home_target_dir}" ]; then \
			suppressions_home="$${suppressions_home_target_dir}"; \
		else \
			mkdir -p "./target"; \
			git clone $(dependency_check_suppressions_repo_url) "$${suppressions_home_target_dir}" && \
				suppressions_home="$${suppressions_home_target_dir}"; \
		fi; \
	fi; \
	suppressions_path="$${suppressions_home}/suppressions/$(dependency_check_base_suppressions)"; \
	if [  -f "$${suppressions_path}" ]; then \
		cp -av "$${suppressions_path}" $(suppressions_file); \
		mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=$(dependency_check_minimum_cvss) -DassemblyAnalyzerEnabled=$(dependency_check_assembly_analyzer_enabled) -DsuppressionFiles=$(suppressions_file); \
	else \
		printf -- "\n ERROR Cannot find suppressions file at '%s'\n" "$${suppressions_path}" >&2; \
		exit 1; \
	fi

.PHONY: security-check
security-check: dependency-check
