package docsascode.model

import com.github.chriskn.structurizrextension.plantuml.C4PlantUmlLayout
import com.github.chriskn.structurizrextension.plantuml.DependencyConfiguration
import com.github.chriskn.structurizrextension.plantuml.Direction
import com.github.chriskn.structurizrextension.view.componentView
import com.structurizr.analysis.AbstractComponentFinderStrategy
import com.structurizr.analysis.AnnotationTypeMatcher
import com.structurizr.analysis.ComponentFinder
import com.structurizr.analysis.ExtendsClassTypeMatcher
import com.structurizr.analysis.IgnoreDuplicateComponentStrategy
import com.structurizr.analysis.ImplementsInterfaceTypeMatcher
import com.structurizr.analysis.NameSuffixTypeMatcher
import com.structurizr.analysis.RegexTypeMatcher
import com.structurizr.analysis.SpringComponentFinderStrategy
import com.structurizr.analysis.StructurizrAnnotationsComponentFinderStrategy
import com.structurizr.analysis.TypeMatcherComponentFinderStrategy
import com.structurizr.model.Component
import docsascode.model.Containers.inventoryProvider
import docsascode.model.Containers.subGraphInventory
import example.webshop.inventory.AbstractQuery
import example.webshop.inventory.Consumer
import org.reflections.util.ClasspathHelper
import org.springframework.stereotype.Repository
import java.net.URLClassLoader


object Components {

    init {
        val strategy =
            createStructurizrAnnotationsComponentFinderStrategy()
        // createSpringComponentFinderStrategy()
        // createTypeMatcherComponentFinderStrategy()
        strategy.duplicateComponentStrategy = IgnoreDuplicateComponentStrategy()

        val componentFinder = ComponentFinder(
            inventoryProvider,
            "example.webshop.inventory",
            strategy,
        )
        // Workaround for issue https://github.com/ronmamo/reflections/issues/373 occurring when running from jar
        componentFinder.urlClassLoader = URLClassLoader(
            ClasspathHelper
                .forPackage("example.webshop.inventory")
                .toTypedArray()
        )
        //  componentFinder.exclude(".*\\.Abstract.*", ".*\\.Consumer")
        componentFinder.findComponents()
    }

    private fun createTypeMatcherComponentFinderStrategy(): AbstractComponentFinderStrategy =
        TypeMatcherComponentFinderStrategy(
            NameSuffixTypeMatcher(
                "WriteService",
                "Writes data to database and triggers orders if goods run out of stock",
                "Spring Service"
            ),
            NameSuffixTypeMatcher(
                "ReadService",
                "Reads data from database",
                "Spring Service"
            ),
            AnnotationTypeMatcher(
                Repository::class.java,
                "Spring JDBC Repository",
                "Spring JDBC"
            ),
            ImplementsInterfaceTypeMatcher(
                Consumer::class.java,
                "Consumes Kafka topic",
                "Spring Cloud Stream Binder Kafka"
            ),
            ExtendsClassTypeMatcher(
                AbstractQuery::class.java,
                "GraphQL query",
                "Spring Controller"
            ),
            RegexTypeMatcher(
                ".*\\.OrderClient",
                "Sends orders via POST to order service",
                "Spring RestTemplate"
            )
        )


    private fun createSpringComponentFinderStrategy(): AbstractComponentFinderStrategy = SpringComponentFinderStrategy()

    private fun createStructurizrAnnotationsComponentFinderStrategy(): AbstractComponentFinderStrategy =
        StructurizrAnnotationsComponentFinderStrategy()

    fun createComponentView() {
        val componentView = InventoryWorkspace.views.componentView(
            container = inventoryProvider,
            key = "inventory_components",
            description = "Component diagram for the ${inventoryProvider.name} service",
            layout = C4PlantUmlLayout(
                dependencyConfigurations = listOf(
                    DependencyConfiguration(
                        filter = { it.destination.parent == Systems.warehouse || it.destination == subGraphInventory },
                        direction = Direction.Up
                    )
                )
            )
        )
//        inventoryProvider.components
//            .filter {
//                it.technology.equals(SpringComponentFinderStrategy.SPRING_REPOSITORY)
//            }.map {
//                it.description = "Spring JDBC Repository"
//            }
        componentView.addAllComponents()
        inventoryProvider.components.forEach {
            componentView.addNearestNeighbours(it)
        }
    }

}