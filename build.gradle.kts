import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    kotlin("plugin.lombok") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.5"
    id("io.freefair.lombok") version "8.13.1"
}

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/nexus/content/groups/public/")
    mavenCentral()
    maven("https://r.irepo.space/maven/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(fileTree("libs"))
    // kotlin
    compileOnly(kotlin("stdlib"))
    // jabel
    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
    compileOnly("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
    annotationProcessor("net.java.dev.jna:jna-platform:5.13.0")
    // bukkit
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    // ni
    compileOnly("pers.neige.neigeitems:NeigeItems:+")
    // multiple-string-searcher
    compileOnly("org.neosearch.stringsearcher:multiple-string-searcher:0.1.1")
    // bstats
    compileOnly("org.bstats:bstats-bukkit:3.0.2")
    // colonel
    implementation("pers.neige.colonel:colonel-common:+") {
        exclude(group = "org.neosearch.stringsearcher")
    }
    implementation("pers.neige.colonel:colonel-kotlin:+")
    implementation("pers.neige.colonel:colonel-bukkit:+")
    // XSeries
    implementation("com.github.cryptomorin:XSeries:13.6.0")
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/**")
        exclude("module-info.java")
        // kotlin
        relocate("kotlin.", "pers.neige.neigeitems.libs.kotlin.")
        // bstats
        relocate("org.bstats", "pers.neige.neigeitems.libs.bstats")
        // stringsearcher
        relocate("org.neosearch.stringsearcher", "pers.neige.neigeitems.libs.stringsearcher")
        // colonel
        relocate("pers.neige.colonel", "${rootProject.group}.libs.colonel")
    }
    build {
        dependsOn(shadowJar)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.compileJava {
    sourceCompatibility = "17"
    options.release = 8
    options.encoding = "UTF-8"

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.create("apiJar", Jar::class) {
    dependsOn(tasks.compileJava, tasks.compileKotlin)
    from(tasks.compileJava, tasks.compileKotlin)

    // clean no-class file
    include { it.isDirectory or it.name.endsWith(".class") }
    includeEmptyDirs = false

    archiveClassifier.set("api")
}

tasks.assemble {
    dependsOn(tasks["apiJar"])
}

tasks.withType<ProcessResources> {
    val properties = mapOf(
        "version" to rootProject.version,
        "group" to rootProject.group,
        "name" to rootProject.name,
        "prefix" to "§7[§2§lGoods§6§lTrade§7] §r"
    )
    inputs.properties(properties)
    filesMatching(listOf("plugin.yml", "config.yml")) {
        expand(properties)
    }
}
