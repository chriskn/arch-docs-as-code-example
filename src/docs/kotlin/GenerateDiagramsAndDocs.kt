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
private val alertFile = File("src/docs/resources/docs_generated/alerts.adoc")

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
    alertFile.bufferedWriter().use { out -> out.write(alertTable) }
}
