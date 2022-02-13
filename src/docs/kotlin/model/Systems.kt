package docsascode.model

import com.github.chriskn.structurizrextension.model.Dependency
import com.github.chriskn.structurizrextension.model.softwareSystem
import com.github.chriskn.structurizrextension.plantuml.C4PlantUmlLayout
import com.github.chriskn.structurizrextension.plantuml.DependencyConfiguration
import com.github.chriskn.structurizrextension.plantuml.Direction
import com.github.chriskn.structurizrextension.view.systemContextView
import com.structurizr.model.InteractionStyle
import com.structurizr.model.Location
import com.structurizr.model.Person
import docsascode.model.ExternalTeams.maintainedBy
import docsascode.model.InventoryWorkspace.model

object Systems {

    val orderService = model.softwareSystem(
        name = "Order service",
        description = "Orders new goods",
        location = Location.External
    )
    val warehouse = model.softwareSystem(
        name = "Warehouse",
        description = "Provides inventory data via Kafka",
        location =  Location.External
    )

    val graphQlFederation = model.softwareSystem(
        name = "GraphQL Federation",
        description = "Provides a federated Graph to clients",
        location = Location.External,
    )

    val inventoryService = model.softwareSystem(
        name = "Inventory service",
        description = "Reads inventory data and provides it to clients via subgraph-inventory. " +
            "Orders new goods if they run out of stock",
        uses = listOf(
            Dependency(
                destination = graphQlFederation,
                description = "subgraph-inventory",
                link = "http://your-federated-graph/subgraph-inventory"
            ),
            Dependency(
                destination = warehouse,
                description = "Reads inventory data from", technology = "Kafka",
                interactionStyle = InteractionStyle.Asynchronous
            ),
            Dependency(
                destination = orderService,
                description = "Triggers order if goods run out of stock",
                technology = "REST"
            )
        ),
        link = "#container-view"
    )

    init {
       warehouse.maintainedBy(teamName = "Team A")
       orderService.maintainedBy(teamName = "Team B")
    }

    fun createContextView(){
        val contextView = InventoryWorkspace.views.systemContextView(
            softwareSystem = inventoryService,
            key = "inventory_context",
            description = "Context diagram for the web shop inventory system",
            layout = C4PlantUmlLayout(
                dependencyConfigurations = listOf(
                    DependencyConfiguration(
                        filter = {it.destination == graphQlFederation},
                        direction = Direction.Up
                    ),
                    DependencyConfiguration(
                        filter = {it.destination == orderService},
                        direction = Direction.Right
                    ),
                    DependencyConfiguration(
                        filter = {it.source is Person},
                        direction = Direction.Up
                    )
                )
            )
        )
        contextView.addAllElements()
    }

}