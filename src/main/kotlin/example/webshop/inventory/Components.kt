package example.webshop.inventory

import com.structurizr.annotation.UsesComponent
import com.structurizr.annotation.UsesContainer
import com.structurizr.annotation.UsesSoftwareSystem
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import com.structurizr.annotation.Component as StructurizrComponent

abstract class AbstractQuery

@Service
@StructurizrComponent(description = "Query providing inventory data to clients", technology = "GraphQL")
@UsesContainer(name = "Container://GraphQL Federation.subgraph-inventory", description = "Is part of")
class InventoryQuery(
    @UsesComponent(description = "Reads inventory data using")
    private val inventoryReadService: InventoryReadService
)

interface Consumer

@Service
@StructurizrComponent(description = "Consumes goods Kafka topic")
@UsesContainer(name = "Container://Warehouse.warehousegoods", description = "Consumes", technology = "Kafka")
class GoodsConsumer(
    @UsesComponent(description = "Writes received goods via")
    private val inventoryWriteService: InventoryWriteService
) : Consumer

@Component
@StructurizrComponent(description = "Consumes stock Kafka topic")
@UsesContainer(name = "Container://Warehouse.warehousestock_v1", description = "Consumes", technology = "Kafka")
class StockConsumer(
    @UsesComponent(description = "Writes received stock data")
    private val inventoryWriteService: InventoryWriteService
) : Consumer

@Component
@StructurizrComponent(description = "Sends orders via POST to Order service")
@UsesSoftwareSystem(name = "Order service", description = "Sends orders via POST requests to", technology = "REST")
class OrderClient

@Service
@StructurizrComponent(description = "Service reading inventory data")
class InventoryReadService(
    @UsesComponent(description = "Reads inventory data using")
    private val inventoryRepository: InventoryRepository
)

@Service
@StructurizrComponent(description = "Writes Inventory data to database and triggers orders if goods run out of stock")
class InventoryWriteService(
    @UsesComponent(description = "Writes Inventory data to")
    private val inventoryRepository: InventoryRepository,
    @UsesComponent(description = "Uses to order new goods")
    private val orderClient: OrderClient
)

@Repository
@StructurizrComponent(description = "JDBC repository reading/writing inventory data")
@UsesContainer(name = "Inventory Database", description = "Reads/writes Inventory data", technology = "JDBC")
class InventoryRepository


