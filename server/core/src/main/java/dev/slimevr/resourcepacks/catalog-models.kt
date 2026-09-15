package dev.slimevr.resourcepacks

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

data class SourcedResource<T>(val path: String, val value: T, val raw: JsonObject)

@Serializable(with = LanguageResourceSerializer::class)
data class LanguageResource(val schema: String?, val translations: Map<String, String>)

data class ParsedResourcePack(
	val source: String,
	val manifest: SourcedResource<PackManifest>,
	val bones: List<SourcedResource<BoneDefinition>>,
	val proportions: List<SourcedResource<ProportionDefinition>>,
	val boneOverrides: List<SourcedResource<BoneOverride>>,
	val proportionOverrides: List<SourcedResource<ProportionOverride>>,
	val languages: List<SourcedResource<LanguageResource>>,
)

data class ResourcePackDiagnostic(val path: String, val pointer: String = "", val message: String)
class ResourcePackParseException(val diagnostics: List<ResourcePackDiagnostic>) :
	IllegalArgumentException(
		diagnostics.joinToString("\n") { "${it.path}${it.pointer}: ${it.message}" },
	)

data class ResourcePackFailure(val folder: String, val diagnostics: List<ResourcePackDiagnostic>)
data class ResourcePackCatalog(
	val core: ParsedResourcePack,
	val userPacks: List<ParsedResourcePack>,
	val failures: List<ResourcePackFailure>,
)
