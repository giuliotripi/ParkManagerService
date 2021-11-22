import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	kotlin("plugin.serialization") version "1.5.31"
}

group = "eu.musarellatripi"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	flatDir {
		dir(file("../unibolibs"))
	}
}

dependencies {
	//implementation(files("../unibolibs/parkingService4-1.0.jar"))
	implementation(":it.unibo.parkingService4:1.0")
	implementation("org.json:json:20210307")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("org.eclipse.californium:californium-core:3.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

/*tasks.withType<JavaExec> {
	group = "Execution" // <-- change the name as per your need
	description = "Run the wen server"
	main = "eu.musarellatripi.webapp.WebappApplicationKt"
	classpath = sourceSets["main"].runtimeClasspath
}*/
/*task("execute", JavaExec::class) {
	group = "Execution" // <-- change the name as per your need
	description = "Run the wen server"
	main = "eu.musarellatripi.webapp.WebappApplicationKt"
	classpath = sourceSets["main"].runtimeClasspath
}

tasks.withType<Jar> {

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	manifest {
		attributes["Main-Class"] = "eu.musarellatripi.webapp.WebappApplicationKt"
	}
	// To add all of the dependencies
	from(sourceSets.main.get().output)

	dependsOn(configurations.runtimeClasspath)
	from({
		configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
	})
	exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}*/
