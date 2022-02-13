import ch.nomisp.confluence.publisher.PublishToConfluenceTask
import org.asciidoctor.gradle.editorconfig.AsciidoctorEditorConfigGenerator
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.asciidoctor.editorconfig") version "3.3.2"
    id("ch.nomisp.confluence.publisher") version "0.2.0"
}

group = "com.github.chriskn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    create("docs") {
        kotlin {
            compileClasspath += main.get().output
            runtimeClasspath += output + compileClasspath
        }
    }
    val docs by getting {
        dependencies {
            "docsImplementation"("io.github.chriskn:structurizr-c4puml-extension:0.7.2")
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

val asciiAttributes = mapOf(
    "imagesdir" to ".",
    "plantUmlDir" to "./plantuml",
    "toc" to "left",
    "toclevels" to 3,
    "max-width" to "100%",
    "projectName" to rootProject.name,
    "dateTime" to SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ").format(Date())
)

tasks.withType(AsciidoctorTask::class) {
    setSourceDir(file("./src/docs/resources"))
    setBaseDir(file("./src/docs/resources"))
    setOutputDir(file("build/docs"))
    attributes(asciiAttributes)
    options(mapOf("doctype" to "book"))
    isLogDocuments = true
    dependsOn("writeDiagrams")
}

tasks.withType(AsciidoctorEditorConfigGenerator::class) {
    setAttributes(asciiAttributes)
    setDestinationDir("./src/docs/resources")
    group = "documentation"
}

tasks.named("processDocsResources"){
    dependsOn("asciidoctorEditorConfig")
}

tasks.register("writeDiagrams", JavaExec::class) {
    classpath += sourceSets["docs"].runtimeClasspath
    mainClass.set("docsascode.WriteDiagramsKt")
    group = "documentation"
}

asciidoctorj {
    modules {
        diagram.use()
        diagram.setVersion("2.2.1")
    }
}

confluencePublisher {
    asciiDocRootFolder.set(tasks.asciidoctor.get().sourceDir)
    setAttributes(tasks.asciidoctor.get().attributes)
    rootConfluenceUrl.set("YOUR_CONFLUENCE_URL")
    spaceKey.set("YOUR_SPACE_KEY")
    ancestorId.set("ID_OF_PARENT_PAGE")
    // set username or password or use an api token as password with empty username
    username.set("")
    password.set(System.getenv("CONFLUENCE_TOKEN"))
    notifyWatchers.set(false)
}

tasks.withType(PublishToConfluenceTask::class) {
    dependsOn("writeDiagrams")
}



