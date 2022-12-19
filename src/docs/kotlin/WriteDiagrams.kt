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

fun main() {
    Systems.createContextView()
    Containers.createContainerView()
    Components.createComponentView()
    Deployment.createDeploymentView()

    workspace.writeDiagrams(outputFolder)

    val table = createAsciiAlertTable()
    println(table)
}
