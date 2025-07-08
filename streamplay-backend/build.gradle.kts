plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"

}

// Projektkoordinaten: Gruppe identifiziert dein Projekt eindeutig (z.B. GitHub-Handle)
//group = "de.thomasf.streamplay"
group = "io.github.codergod1337.streamplay"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

configurations {
	compileOnly {
		// Annotation Processor-Dependencies (Lombok) in compileOnly einbeziehen
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// ORM-Framework & Repository-Support für PostgreSQL
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// OAuth2 Resource Server zum Verifizieren von JWT-Tokens
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	// liefert die JWT-Decoder/Verifier (Nimbus)
	//implementation("org.springframework.boot:spring-boot-starter-oauth2-jose")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")      // Public API für Jwts.builder()/parser()  liefert die Schnittstelle und Klassen wie Jwts.
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")       // Implementierung (signing, parsing) enthält die tatsächliche Logik zum Signieren und Parsen.
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")    // JSON-Handling via Jackson verbindet JJWT mit Jackson für JSON-Serialisierung.

	// Spring Security Core
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Bean Validation (JSR-380, Hibernate Validator)
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// REST-API-Framework
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Monitoring: Health, Metrics, Info Endpoints hinzufügen
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// DevTools für automatischen Neustart & LiveReload
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// OpenAPI / Swagger UI für interaktive API-Dokumentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// PostgreSQL JDBC-Treiber (nur zur Laufzeit)
	runtimeOnly("org.postgresql:postgresql")

	// Lombok zum Generieren von Boilerplate-Code
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")

	// Testing: Spring Boot Test-Framework & Security-Test-Tools
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Konfiguration für den Test-Task
// JUnit 5 Platform bleibt aktiv
tasks.withType<Test> {
	useJUnitPlatform()
}
