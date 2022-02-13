package docsascode.model

import com.structurizr.Workspace
import com.structurizr.model.Model
import com.structurizr.view.ViewSet


object InventoryWorkspace {
    val workspace: Workspace = Workspace(
        "Inventory Service",
        "Inventory Service example"
    )
    val model: Model = workspace.model
    val views: ViewSet = workspace.views
}