package dev.slimevr.resourcepacks.languages

import dev.slimevr.resourcepacks.LanguageResourceSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LanguageResourceSerializer::class)
data class LanguageResource(val schema: String?, val translations: Map<String, String>)
