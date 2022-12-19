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
import docsascode.model.utils.parseTopicDestinations

object Containers {

    val subGraphInventory = graphQlFederation.container(
        name = "subgraph-inventory",
        description = "Provides inventory data",
        location = Location.Internal,
        technology = "GraphQL",
        icon = "graphql"
    )

    private val topicConsumedByInventoryService =
        parseTopicDestinations().map { topicName ->
            warehouse.kafkaTopic(topicName, resolveDesciptionByTopicName(topicName))
        }

    private fun SoftwareSystem.kafkaTopic(
        name: String,
        description: String
    ): Container = this.container(
        name = name,
        description = description,
        c4Type = C4Type.QUEUE,
        technology = "Kafka",
        icon = "kafka",
        link = "https://examplecompany.akhq.org/$name"
    )

    private fun resolveDesciptionByTopicName(topicName: String): String = with(topicName) {
        when {
            contains("goods") -> "Contains metadata regarding goods"
            contains("stock") -> "Contains data regarding the amount of goods in stock"
            else -> ""
        }
    }

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
        uses = topicConsumedByInventoryService.map { topic ->
            Dependency(
                destination = topic,
                description = "Reads",
                technology = "Kafka",
                interactionStyle = Asynchronous
            )
        }.plus(
            listOf(
                Dependency(destination = database, description = "Reads and writes inventory data to/from"),
                Dependency(destination = subGraphInventory, description = "contributes to federated graph"),
                Dependency(
                    destination = orderService,
                    description = "Triggers order if goods run out of stock",
                    technology = "REST"
                )
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