package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Schema
import com.github.erosb.jsonsKema.SchemaLoader
import java.net.URI
import java.nio.charset.StandardCharsets

internal enum class ResourceKind {
	MANIFEST,
	BONE,
	PROPORTION,
	BONE_OVERRIDE,
	PROPORTION_OVERRIDE,
	LANGUAGE,
	MISPLACED,
}

internal fun kindFor(path: String): ResourceKind? = when {
	path == "manifest.json" -> ResourceKind.MANIFEST
	Regex("data/bones/[^/]+\\.json").matches(path) -> ResourceKind.BONE
	Regex("data/proportions/[^/]+\\.json").matches(path) -> ResourceKind.PROPORTION
	Regex("data/overrides/bones/[^/]+\\.json").matches(path) -> ResourceKind.BONE_OVERRIDE
	Regex("data/overrides/proportions/[^/]+\\.json").matches(path) -> ResourceKind.PROPORTION_OVERRIDE
	Regex("assets/lang/[^/]+\\.json").matches(path) -> ResourceKind.LANGUAGE
	(path.startsWith("data/") || path.startsWith("assets/lang/")) && path.endsWith(".json") -> ResourceKind.MISPLACED
	else -> null
}

internal object PackSchemas {
	private const val ROOT = ClasspathResourcePackSource.CORE_ROOT + "/schemas/v1"
	private val names = ResourceKind.entries.filter { it != ResourceKind.MISPLACED }.associateWith { kind ->
		when (kind) {
			ResourceKind.MANIFEST -> "manifest.schema.json"
			ResourceKind.BONE -> "bone.schema.json"
			ResourceKind.PROPORTION -> "proportion.schema.json"
			ResourceKind.BONE_OVERRIDE -> "bone-override.schema.json"
			ResourceKind.PROPORTION_OVERRIDE -> "proportion-override.schema.json"
			ResourceKind.LANGUAGE -> "language.schema.json"
			ResourceKind.MISPLACED -> error("No schema")
		}
	}
	private val base = URI("classpath:/$ROOT/")
	private val documents = names.values.distinct().associateWith { name ->
		PackSchemas::class.java.classLoader.getResourceAsStream("$ROOT/$name")?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
			?: error("Bundled resource-pack schema is missing: $ROOT/$name")
	}
	private val config = com.github.erosb.jsonsKema.SchemaLoaderConfig.createDefaultConfig(documents.mapKeys { (name, _) -> base.resolve(name) })
	private val loaded = names.mapValues { (_, name) -> SchemaLoader(JsonParser(documents.getValue(name), base.resolve(name)).parse(), config.copy(initialBaseURI = base.resolve(name))).load() }
	fun schema(kind: ResourceKind): Schema = loaded.getValue(kind)
}
