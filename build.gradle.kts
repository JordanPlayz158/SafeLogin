import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mod_version: String by extra
val maven_group: String by extra
val archives_base_name: String by extra
val minecraft_version: String by extra
val yarn_mappings: String by extra
val loader_version: String by extra
val fabric_version: String by extra
val fabric_kotlin_version: String by extra

plugins {
	id("fabric-loom") version "1.7-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
}

version = mod_version
group = maven_group

base {
	archivesName = archives_base_name
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("safelogin") {
			sourceSets.main
			sourceSets["client"]
		}
	}

}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")
}

tasks.processResources {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to version))
	}
}

tasks.compileJava {
	options.release = 17
}

tasks.getByName<KotlinCompile>("compileClientKotlin") {
	compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

tasks.compileKotlin {
	compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}"}
	}

	if (project.hasProperty("dev")) {
		destinationDirectory.set(File("$rootDir/run/mods"))
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = archives_base_name
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}