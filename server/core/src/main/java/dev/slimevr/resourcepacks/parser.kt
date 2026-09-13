package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Validator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/** Validates trusted-schema JSON, then decodes a complete typed resource model. */
class ResourcePackParser {
	private val json = Json {
		ignoreUnknownKeys = false
		explicitNulls = true
	}

	suspend fun parse(source: ResourcePackSource): ParsedResourcePack {
		val entries = try {
			source.entries()
		} catch (e: Exception) {
			throw ResourcePackParseException(listOf(ResourcePackDiagnostic("", message = "Unable to scan ${source.description}: ${e.message}")))
		}
		val diagnostics = mutableListOf<ResourcePackDiagnostic>()
		entries.groupBy { it.path }.filterValues { it.size > 1 }.keys.sorted().forEach { diagnostics += ResourcePackDiagnostic(it, message = "Duplicate resource path") }
		val manifestEntry = entries.firstOrNull { it.path == "manifest.json" }
		if (manifestEntry == null) diagnostics += ResourcePackDiagnostic("manifest.json", message = "Missing manifest")

		val bones = mutableListOf<SourcedResource<BoneDefinition>>()
		val proportions = mutableListOf<SourcedResource<ProportionDefinition>>()
		val boneOverrides = mutableListOf<SourcedResource<BoneOverride>>()
		val proportionOverrides = mutableListOf<SourcedResource<ProportionOverride>>()
		val languages = mutableListOf<SourcedResource<LanguageResource>>()
		var manifest: SourcedResource<PackManifest>? = null
		for (entry in entries) {
			val kind = kindFor(entry.path) ?: continue
			if (kind == ResourceKind.MISPLACED) {
				diagnostics += ResourcePackDiagnostic(entry.path, message = "JSON document is not allowed at this pack path")
				continue
			}
			val document = validate(entry, kind, diagnostics) ?: continue
			try {
				when (kind) {
					ResourceKind.MANIFEST -> manifest = SourcedResource(entry.path, json.decodeFromJsonElement(document))
					ResourceKind.BONE -> bones += SourcedResource(entry.path, json.decodeFromJsonElement(document))
					ResourceKind.PROPORTION -> proportions += SourcedResource(entry.path, json.decodeFromJsonElement(document))
					ResourceKind.BONE_OVERRIDE -> boneOverrides += SourcedResource(entry.path, json.decodeFromJsonElement(document))
					ResourceKind.PROPORTION_OVERRIDE -> proportionOverrides += SourcedResource(entry.path, json.decodeFromJsonElement(document))
					ResourceKind.LANGUAGE -> languages += SourcedResource(entry.path, json.decodeFromJsonElement(document))
				}
			} catch (e: Exception) {
				diagnostics += ResourcePackDiagnostic(entry.path, message = "Schema/model drift while decoding: ${e.message}")
			}
		}
		if (diagnostics.isNotEmpty()) throw ResourcePackParseException(diagnostics.sortedWith(compareBy<ResourcePackDiagnostic> { it.path }.thenBy { it.pointer }))
		return ParsedResourcePack(source.description, requireNotNull(manifest), bones.toList(), proportions.toList(), boneOverrides.toList(), proportionOverrides.toList(), languages.toList())
	}

	private fun validate(entry: ResourcePackEntry, kind: ResourceKind, diagnostics: MutableList<ResourcePackDiagnostic>): JsonElement? {
		val instance = try {
			JsonParser(entry.contents).parse()
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "Malformed JSON: ${e.message}")
			return null
		}
		val failure = Validator.forSchema(PackSchemas.schema(kind)).validate(instance)
		if (failure != null) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "$kind schema: $failure")
			return null
		}
		return try {
			json.parseToJsonElement(entry.contents)
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic(entry.path, message = "Malformed JSON: ${e.message}")
			null
		}
	}
}
