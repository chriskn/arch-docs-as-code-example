package docsascode

import com.github.chriskn.structurizrextension.writeDiagrams
import docsascode.model.Components
import docsascode.model.Containers
import docsascode.model.Deployment
import docsascode.model.InventoryWorkspace.workspace
import docsascode.model.Systems
import docsascode.model.utils.createAsciiAlertTable
import java.io.File

private val outputFolder = File("src/docs/resources/plantuml/")
private val docsGeneratedFolder = File("src/docs/resources/docs_generated/")

fun main() {
    generateDiagrams()
    generateDocs()
}

private fun generateDiagrams() {
    Systems.createContextView()
    Containers.createContainerView()
    Components.createComponentView()
    Deployment.createDeploymentView()

    workspace.writeDiagrams(outputFolder)
}

private fun generateDocs() {
    val alertTable = createAsciiAlertTable()
    docsGeneratedFolder.mkdirs()
    File(docsGeneratedFolder, "alerts.adoc")
        .bufferedWriter()
        .use { out -> out.write(alertTable) }
}
