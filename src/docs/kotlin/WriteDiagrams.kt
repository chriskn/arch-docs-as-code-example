package docsascode

import com.github.chriskn.structurizrextension.writeDiagrams
import docsascode.model.Containers
import docsascode.model.Deployment
import docsascode.model.InventoryWorkspace.workspace
import docsascode.model.Systems
import java.io.File

private val outputFolder = File("src/docs/resources/plantuml/")

fun main() {
    Systems.createContextView()
    Containers.createContainerView()
    Deployment.createDeploymentView()

    workspace.writeDiagrams(outputFolder)
}
