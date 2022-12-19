package docsascode.model

import com.github.chriskn.structurizrextension.model.C4Properties
import com.github.chriskn.structurizrextension.model.Dependency
import com.github.chriskn.structurizrextension.model.deploymentNode
import com.github.chriskn.structurizrextension.model.infrastructureNode
import com.github.chriskn.structurizrextension.plantuml.C4PlantUmlLayout
import com.github.chriskn.structurizrextension.plantuml.DependencyConfiguration
import com.github.chriskn.structurizrextension.plantuml.Layout
import com.github.chriskn.structurizrextension.plantuml.Mode
import com.github.chriskn.structurizrextension.view.deploymentView
import docsascode.model.utils.resolveHelmValues

object Deployment {

    private val aws = InventoryWorkspace.model.deploymentNode(
        name = "AWS",
        icon = "aws",
        properties = C4Properties(
            values = listOf(
                listOf("accountId", "123456"),
                listOf("region", "eu-central-1")
            )
        )
    )

    private val rds = aws.deploymentNode(
        name = "Relational Database Service (RDS)",
        icon = "awsrds",
        hostsContainers = listOf(Containers.database)
    )

    private val eks = aws.deploymentNode(
        name = "Elastic Kubernetes Service (EKS)",
        icon = "awsekscloud"
    )


    private val inventoryPod = eks.deploymentNode(
        name = "Inventory POD",
        properties = C4Properties(
            values = resolveHelmValues()
        )
    )

    private val inventoryDocker = inventoryPod.deploymentNode(
        name = "Docker Container",
        icon = "docker",
        hostsContainers = listOf(Containers.inventoryProvider)
    )

    private val ingress = eks.infrastructureNode(
        name = "Ingress",
        description = "Used for load balancing and SSL termination",
        icon = "nginx",
        technology = "NGINX",
        uses = listOf(
            Dependency(
                destination = inventoryPod,
                description = "Forwards requests to"
            )
        )
    )

    private val apollo = InventoryWorkspace.model.deploymentNode(
        name = "Apollo Studio",
        hostsSystems = listOf(Systems.graphQlFederation),
        uses = listOf(
            Dependency(
                destination = ingress,
                description = "Forwards ${Containers.subGraphInventory.name} queries to"
            )
        )
    )

    fun createDeploymentView() {
        val deploymentView = InventoryWorkspace.views.deploymentView(
            system = Systems.inventoryService,
            key = "inventory_deployment",
            description = "Deployment diagram for the Inventory service",
            layout = C4PlantUmlLayout(
                dependencyConfigurations = listOf(
                    DependencyConfiguration(
                        filter = { it.source == ingress },
                        mode = Mode.Neighbor
                    ),
                ),
                layout = Layout.LeftToRight
            )
        )
        deploymentView.addAllDeploymentNodes()
    }
}