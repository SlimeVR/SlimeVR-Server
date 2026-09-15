package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Schema
import com.github.erosb.jsonsKema.SchemaLoader
import com.github.erosb.jsonsKema.SchemaLoaderConfig
import com.github.erosb.jsonsKema.Validator
import dev.slimevr.resourcepacks.bones.BoneDefinition
import dev.slimevr.resourcepacks.languages.LanguageResource
import dev.slimevr.resourcepacks.proportions.ProportionDefinition
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.net.URI
import java.nio.charset.StandardCharsets

abstract class ResourceType<T : Any>(val id: String) {
	open val mergeLayers: Boolean = false
	open fun migrateJson(document: JsonObject, fromVersion: Int): JsonObject = document
	abstract fun matches(path: String): Boolean
	abstract fun validateAndDecode(path: String, document: JsonObject, diagnostics: MutableList<in ResourcePackDiagnostic>): SourcedResource<T>?
}

abstract class JsonResourceType<T : Any>(
	id: String,
	val schemaName: String,
) : ResourceType<T>(id) {

	val schema: Schema by lazy {
		val base = URI("classpath:/${PackSchemas.ROOT}/")
		val document = PackSchemas.documents[schemaName] ?: error("Bundled resource-pack schema is missing: ${PackSchemas.ROOT}/$schemaName")
		SchemaLoader(JsonParser(document, base.resolve(schemaName)).parse(), PackSchemas.config.copy(initialBaseURI = base.resolve(schemaName))).load()
	}

	override fun validateAndDecode(path: String, document: JsonObject, diagnostics: MutableList<in ResourcePackDiagnostic>): SourcedResource<T>? {
		val instance = try {
			JsonParser(document.toString()).parse()
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(path, message = "Invalid JSON: ${e.message}")
			return null
		}
		val failure = Validator.forSchema(schema).validate(instance)
		if (failure != null) {
			diagnostics += ResourcePackDiagnostic(path, message = "$id schema: $failure")
			return null
		}
		return try {
			SourcedResource(path, decode(document), document)
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(path, message = "Schema/model drift while decoding: ${e.message}")
			null
		}
	}

	abstract fun decode(document: JsonObject): T
}

object ResourceTypes {
	val MANIFEST: JsonResourceType<PackManifest> = object : JsonResourceType<PackManifest>("manifest", "manifest.schema.json") {
		override fun matches(path: String) = path == "manifest.json"
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<PackManifest>(document)
	}
	val BONE: JsonResourceType<BoneDefinition> = object : JsonResourceType<BoneDefinition>("bone", "bone.schema.json") {
		override fun matches(path: String) = path.startsWith("data/") && path.contains("/bones/") && path.endsWith(".json") && path.count { it == '/' } == 3
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<BoneDefinition>(document)
	}
	val PROPORTION: JsonResourceType<ProportionDefinition> = object : JsonResourceType<ProportionDefinition>("proportion", "proportion.schema.json") {
		override fun matches(path: String) = path.startsWith("data/") && path.contains("/proportions/") && path.endsWith(".json") && path.count { it == '/' } == 3
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<ProportionDefinition>(document)
	}
	val LANGUAGE: JsonResourceType<LanguageResource> = object : JsonResourceType<LanguageResource>("language", "language.schema.json") {
		override val mergeLayers = true
		override fun matches(path: String) = path.startsWith("assets/") && path.contains("/lang/") && path.endsWith(".json") && path.count { it == '/' } == 3
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<LanguageResource>(document)
	}

	val ALL = listOf(MANIFEST, BONE, PROPORTION, LANGUAGE)
}

internal object PackSchemas {
	const val ROOT = ClasspathResourcePackSource.CORE_ROOT + "/schemas/v1"
	val documents = ResourceTypes.ALL.associate { type ->
		type.schemaName to (
			PackSchemas::class.java.classLoader.getResourceAsStream("$ROOT/${type.schemaName}")?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
				?: error("Bundled resource-pack schema is missing: $ROOT/${type.schemaName}")
			)
	}
	val config = SchemaLoaderConfig.createDefaultConfig(documents.mapKeys { (name, _) -> URI("classpath:/$ROOT/").resolve(name) })
}
