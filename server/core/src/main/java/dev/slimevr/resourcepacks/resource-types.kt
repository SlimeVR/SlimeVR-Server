package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Schema
import com.github.erosb.jsonsKema.SchemaLoader
import com.github.erosb.jsonsKema.SchemaLoaderConfig
import com.github.erosb.jsonsKema.Validator
import dev.slimevr.resourcepacks.bones.BoneDefinition
import dev.slimevr.resourcepacks.bones.BoneOverride
import dev.slimevr.resourcepacks.languages.LanguageResource
import dev.slimevr.resourcepacks.proportions.ProportionDefinition
import dev.slimevr.resourcepacks.proportions.ProportionOverride
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.net.URI
import java.nio.charset.StandardCharsets

abstract class ResourceType<T : Any>(val id: String) {
	abstract fun matches(path: String): Boolean
	abstract fun parse(entry: ResourcePackEntry, packFormatVersion: Int, diagnostics: MutableList<ResourcePackDiagnostic>): SourcedResource<T>?
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

	open fun migrateJson(document: JsonObject, fromVersion: Int): JsonObject {
		return document // Default is no migration
	}

	override fun parse(entry: ResourcePackEntry, packFormatVersion: Int, diagnostics: MutableList<ResourcePackDiagnostic>): SourcedResource<T>? {
		val (instance, document) = try {
			JsonParser(entry.contents).parse() to ResourcePackJson.parseToJsonElement(entry.contents).jsonObject
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "Malformed JSON: ${e.message}")
			return null
		}
		
		val migratedDocument = try {
			migrateJson(document, packFormatVersion)
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "Migration failed: ${e.message}")
			return null
		}
		
		val migratedInstance = if (migratedDocument === document) instance else JsonParser(migratedDocument.toString()).parse()

		val failure = Validator.forSchema(schema).validate(migratedInstance)
		if (failure != null) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "$id schema: $failure")
			return null
		}
		return try {
			SourcedResource(entry.path, decode(migratedDocument), migratedDocument)
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "Schema/model drift while decoding: ${e.message}")
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
		override fun matches(path: String) = path.startsWith("data/bones/") && path.endsWith(".json") && path.count { it == '/' } == 2
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<BoneDefinition>(document)
	}
	val PROPORTION: JsonResourceType<ProportionDefinition> = object : JsonResourceType<ProportionDefinition>("proportion", "proportion.schema.json") {
		override fun matches(path: String) = path.startsWith("data/proportions/") && path.endsWith(".json") && path.count { it == '/' } == 2
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<ProportionDefinition>(document)
	}
	val BONE_OVERRIDE: JsonResourceType<BoneOverride> = object : JsonResourceType<BoneOverride>("bone_override", "bone-override.schema.json") {
		override fun matches(path: String) = path.startsWith("data/overrides/bones/") && path.endsWith(".json") && path.count { it == '/' } == 3
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<BoneOverride>(document)
	}
	val PROPORTION_OVERRIDE: JsonResourceType<ProportionOverride> = object : JsonResourceType<ProportionOverride>("proportion_override", "proportion-override.schema.json") {
		override fun matches(path: String) = path.startsWith("data/overrides/proportions/") && path.endsWith(".json") && path.count { it == '/' } == 3
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<ProportionOverride>(document)
	}
	val LANGUAGE: JsonResourceType<LanguageResource> = object : JsonResourceType<LanguageResource>("language", "language.schema.json") {
		override fun matches(path: String) = path.startsWith("assets/lang/") && path.endsWith(".json") && path.count { it == '/' } == 2
		override fun decode(document: JsonObject) = ResourcePackJson.decodeFromJsonElement<LanguageResource>(document)
	}

	val ALL = listOf(MANIFEST, BONE, PROPORTION, BONE_OVERRIDE, PROPORTION_OVERRIDE, LANGUAGE)

	fun isMisplaced(path: String): Boolean = (path.startsWith("data/") || path.startsWith("assets/lang/")) && path.endsWith(".json")
}

internal object PackSchemas {
	const val ROOT = ClasspathResourcePackSource.CORE_ROOT + "/schemas/v1"
	val documents = ResourceTypes.ALL.filterIsInstance<JsonResourceType<*>>().associate { type ->
		type.schemaName to (PackSchemas::class.java.classLoader.getResourceAsStream("$ROOT/${type.schemaName}")?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
			?: error("Bundled resource-pack schema is missing: $ROOT/${type.schemaName}"))
	}
	val config = SchemaLoaderConfig.createDefaultConfig(documents.mapKeys { (name, _) -> URI("classpath:/$ROOT/").resolve(name) })

	@Deprecated("Use ResourceType instead")
	fun schema(id: String): Schema {
		val type = ResourceTypes.ALL.filterIsInstance<JsonResourceType<*>>().first { it.id == id }
		val base = URI("classpath:/$ROOT/")
		val document = documents[type.schemaName]!!
		return SchemaLoader(JsonParser(document, base.resolve(type.schemaName)).parse(), config.copy(initialBaseURI = base.resolve(type.schemaName))).load()
	}
}
