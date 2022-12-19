package docsascode.model.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import java.io.File

private val yamlParser: ObjectMapper = YAMLMapper()
private val valuesYaml = File("./helm/inventory-service/values.yml").readText()

fun resolveHelmValues(): List<List<String>> {
    val test = yamlParser.readValue(
        valuesYaml,
        object : TypeReference<Map<String, Any>>() {}
    )
    val properties = mutableListOf<List<String>>()
    flattenProperties(test, properties)
    return properties.sortedBy { it.first() }
}

private fun flattenProperties(
    propertyMap: Map<String, Any>,
    keys: MutableList<List<String>>,
    parentPath: String = ""
) {
    propertyMap.entries
        .forEach { (propertyName, propertyValue): Map.Entry<String, Any> ->
            val canonicalName = if (parentPath.isNotBlank()) {
                "$parentPath.$propertyName"
            } else {
                propertyName
            }
            if (propertyValue is Map<*, *>) {
                flattenProperties(
                    propertyValue as Map<String, Any>,
                    keys,
                    canonicalName
                )
            } else {
                keys.add(
                    listOf(canonicalName, propertyValue.toString())
                )
            }
        }
}

