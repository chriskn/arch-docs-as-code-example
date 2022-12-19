package docsascode.model.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

private val yamlParser: ObjectMapper = YAMLMapper()
private val appYaml = object {}::class.java.classLoader.getResource("application.yml")?.readText() ?: ""
fun parseTopicDestinations(): List<String> = yamlParser
    .readTree(appYaml)
    .path("spring").path("cloud").path("stream").path("bindings")
    .fields()
    .asSequence()
    .map { it.value.get("destination").textValue() }
    .toList()

