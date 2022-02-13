package docsascode.model

import com.github.chriskn.structurizrextension.model.Dependency
import com.github.chriskn.structurizrextension.model.person
import com.structurizr.model.Location
import com.structurizr.model.SoftwareSystem
import docsascode.model.Systems.orderService
import docsascode.model.Systems.warehouse
import docsascode.model.InventoryWorkspace.model

object ExternalTeams {

    fun SoftwareSystem.maintainedBy(teamName: String) = model.person(
        name = teamName,
        description = "Maintains and operates the ${this.name}",
        location = this.location,
        uses = listOf(Dependency(this, "maintains and operates")),
        link = "https://yourconfluence.com/teams/${teamName.replace(" ", "+")}"
    )
}