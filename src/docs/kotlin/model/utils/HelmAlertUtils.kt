package docsascode.model.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

private val yamlParser: ObjectMapper = YAMLMapper()
    .registerModule(KotlinModule.Builder().build())
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

// matches variables and stores reference $variablename in group $1
private val variablesRegex = """\{\{\s*(.*?)\s*\}\}""".toRegex()

fun createAsciiAlertTable(): String {
    val alertTableRows = parseAlertingRules()
        .joinToString(separator = "\n\n") { it.toAsciiTable() }
    return """
        ||===
        |${Alert.ASCII_TABLE_HEADER}
        
        $alertTableRows
        
        ||===
    """.trimMargin()
}


private fun parseAlertingRules(): List<Alert> {
    val rulesYaml = File(
        "./helm/inventory-service/templates/prometheus-rules.yaml"
    ).readText().replace(variablesRegex, "$1")
    val yamlAlerts = yamlParser.readTree(rulesYaml).findPath("rules")
    return yamlParser.readValue(
        yamlAlerts.toPrettyString(),
        object : TypeReference<List<Alert>>() {}
    )
}

data class Alert(
    val alert: String,
    val expr: String,
    val `for`: String,
    val annotations: Annotations,
    val labels: Labels
) {
    companion object {
        const val ASCII_TABLE_HEADER =
            "|Name |Severity |Summary |Description |Expression |Time range"
    }

    private val invalidSubstrings = listOf("\n", "|")

    fun toAsciiTable(): String {
        return """
           || ${alert.sanitize()}
           || ${labels.severity.sanitize()}
           || ${annotations.summary.sanitize()}
           || ${annotations.description.sanitize()}
           ||`${expr.sanitize()}`
           || ${`for`.sanitize()}
        """.trimIndent()
    }

    private fun String?.sanitize(): String {
        var replaced = this
        invalidSubstrings.forEach { replaced = replaced?.replace(it, "") }
        return replaced ?: " "
    }

    data class Labels(val severity: String)

    data class Annotations(val summary: String, val description: String?)
}

