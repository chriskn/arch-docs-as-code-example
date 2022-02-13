package docsascode.model

import com.github.chriskn.structurizrextension.model.C4Type
import com.github.chriskn.structurizrextension.model.Dependency
import com.github.chriskn.structurizrextension.model.container
import com.github.chriskn.structurizrextension.plantuml.C4PlantUmlLayout
import com.github.chriskn.structurizrextension.plantuml.DependencyConfiguration
import com.github.chriskn.structurizrextension.plantuml.Direction
import com.github.chriskn.structurizrextension.view.containerView
import com.structurizr.model.Container
import com.structurizr.model.InteractionStyle.Asynchronous
import com.structurizr.model.Location
import com.structurizr.model.SoftwareSystem
import docsascode.model.Systems.graphQlFederation
import docsascode.model.Systems.inventoryService
import docsascode.model.Systems.orderService
import docsascode.model.Systems.warehouse

object Containers {

    val subGraphInventory = graphQlFederation.container(
        name = "subgraph-inventory",
        description = "Provides inventory data",
        location = Location.Internal,
        technology = "GraphQL",
        icon = "graphql"
    )

    private val stockTopic = warehouse.kafkaTopic(
        name = "warehouse.stock",
        description = "Contains data regarding the amount of goods in stock"
    )
    private val goodsTopic = warehouse.kafkaTopic(
        name = "warehouse.goods",
        description = "Contains metadata regarding goods"
    )

    private fun SoftwareSystem.kafkaTopic(name: String, description: String): Container = this.container(
        name = name,
        description = description,
        c4Type = C4Type.QUEUE,
        technology = "Kafka",
        icon = "kafka",
        link = "https://examplecompany.akhq.org/$name"
    )

    val database = inventoryService.container(
        name = "Inventory Database",
        description = "Stores inventory items",
        c4Type = C4Type.DATABASE,
        technology = "PostgreSQL",
        icon = "postgresql"
    )

    val inventoryProvider = inventoryService.container(
        name = "Inventory Provider",
        description = "Reads inventory data and provides it to clients via subgraph-inventory. " +
            "Orders new goods if they run out of stock",
        technology = "SpringBoot, Spring Data JDBC, Kafka Streams",
        icon = "springboot",
        uses = listOf(
            Dependency(destination = database, description = "Reads and writes inventory data to/from"),
            Dependency(destination = subGraphInventory, description = "contributes to federated graph"),
            Dependency(
                destination = goodsTopic,
                description = "reads",
                technology = "Kafka",
                interactionStyle = Asynchronous
            ),
            Dependency(
                destination = stockTopic,
                description = "reads",
                technology = "Kafka",
                interactionStyle = Asynchronous
            ),
            Dependency(
                destination = orderService,
                description = "Triggers order if goods run out of stock",
                technology = "REST"
            )
        )
    )

    fun createContainerView() {
        val containerView = InventoryWorkspace.views.containerView(
            system = inventoryService,
            key = "inventory_container",
            description = "Container diagram for the inventory domain",
            layout = C4PlantUmlLayout(
                dependencyConfigurations = listOf(
                    DependencyConfiguration(
                        filter = { it.destination == database },
                        direction = Direction.Left
                    ),
                    DependencyConfiguration(
                        filter = { it.destination == subGraphInventory },
                        direction = Direction.Up
                    ),
                    DependencyConfiguration(
                        filter = { it.destination == orderService },
                        direction = Direction.Right
                    )
                ),
                nodeSep = 80,
                rankSep = 80,
            )
        )
        containerView.addNearestNeighbours(inventoryProvider)
        containerView.externalSoftwareSystemBoundariesVisible = true
    }

}