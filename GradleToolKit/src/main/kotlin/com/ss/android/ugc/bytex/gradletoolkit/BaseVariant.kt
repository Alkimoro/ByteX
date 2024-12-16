package com.ss.android.ugc.bytex.gradletoolkit

import com.android.build.api.artifact.ArtifactType
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.Directory
import java.io.File

val BaseVariant.scope: VariantScopeDelegate
    get() = if (ANDROID_GRADLE_PLUGIN_VERSION.major >= 8) {
        throw Exception("current AGP version is unsupported")
    } else if (ANDROID_GRADLE_PLUGIN_VERSION.major >= 7 && ANDROID_GRADLE_PLUGIN_VERSION.minor >= 4) {
        ReflectionUtils.getField<Any>(this, BaseVariantImpl::class.java, "component").let {
            V74VariantScope.findVariantScopeByConfig(it)
        }
    } else if (ANDROID_GRADLE_PLUGIN_VERSION.major < 4 || (ANDROID_GRADLE_PLUGIN_VERSION.major == 4 && ANDROID_GRADLE_PLUGIN_VERSION.minor == 0)) {
        LowVersionVariantScope(this.scope40)
    } else {
        LowVersionVariantScope(this.scope41)
    }

private val BaseVariant.scope40: VariantScope
    get() = ReflectionUtils.callMethod<BaseVariantData>(this, javaClass, "getVariantData", arrayOf(), arrayOf()).scope

private val BaseVariant.scope41: VariantScope
    get() = ReflectionUtils.getField<Any>(this, BaseVariantImpl::class.java, "componentProperties").let {
        ReflectionUtils.callMethod<BaseVariantData>(it, it.javaClass, "getVariantScope", arrayOf(), arrayOf()) as VariantScope
    }

fun BaseVariant.getArtifactCollection(configType: AndroidArtifacts.ConsumedConfigType, artifactScope: AndroidArtifacts.ArtifactScope, artifactType: AndroidArtifacts.ArtifactType): ArtifactCollection {
    return if (ANDROID_GRADLE_PLUGIN_VERSION.major < 4 || (ANDROID_GRADLE_PLUGIN_VERSION.major == 4 && ANDROID_GRADLE_PLUGIN_VERSION.minor == 0)) {
        this.getArtifactCollection40(configType, artifactScope, artifactType)
    } else {
        this.getArtifactCollection41(configType, artifactScope, artifactType)
    }
}

private fun BaseVariant.getArtifactCollection40(configType: AndroidArtifacts.ConsumedConfigType, artifactScope: AndroidArtifacts.ArtifactScope, artifactType: AndroidArtifacts.ArtifactType): ArtifactCollection {
    return lowVersionScope.getArtifactCollection(configType, artifactScope, artifactType)
}

private fun BaseVariant.getArtifactCollection41(configType: AndroidArtifacts.ConsumedConfigType, artifactScope: AndroidArtifacts.ArtifactScope, artifactType: AndroidArtifacts.ArtifactType): ArtifactCollection {
    return ReflectionUtils.getField<Any>(this, BaseVariantImpl::class.java, "component").let {
        ReflectionUtils.callPublicMethod<Any>(it, it.javaClass, "getVariantDependencies", arrayOf(), arrayOf()).let {
            ReflectionUtils.callPublicMethod<ArtifactCollection>(it, it.javaClass, "getArtifactCollection", arrayOf(
                AndroidArtifacts.ConsumedConfigType::class.java, AndroidArtifacts.ArtifactScope::class.java, AndroidArtifacts.ArtifactType::class.java
            ), arrayOf(configType, artifactScope, artifactType))
        }
    }
}

fun BaseVariant.getArtifactFiles(artifactType: ArtifactType): Collection<File> {
    return if (ANDROID_GRADLE_PLUGIN_VERSION.major > 3 || (ANDROID_GRADLE_PLUGIN_VERSION.major == 3 && ANDROID_GRADLE_PLUGIN_VERSION.minor >= 5)) {
        this.getArtifactFiles35_(artifactType)
    } else {
        this.getArtifactFiles30_(artifactType)
    }
}

private fun BaseVariant.getArtifactFiles35_(artifactType: ArtifactType): Collection<File> {
    return lowVersionScope.artifacts.getFinalProducts<Directory>(artifactType).orNull?.map { it.asFile }?.toSet()
        ?: emptyList()
}

private fun BaseVariant.getArtifactFiles30_(artifactType: ArtifactType): Collection<File> {
    return lowVersionScope.artifacts.getArtifactFiles(artifactType).files
}

private val BaseVariant.lowVersionScope: VariantScope
    get() = if (ANDROID_GRADLE_PLUGIN_VERSION.major < 4 || (ANDROID_GRADLE_PLUGIN_VERSION.major == 4 && ANDROID_GRADLE_PLUGIN_VERSION.minor == 0)) {
        this.scope40
    } else { this.scope41 }

//val BaseVariant.blameLogOutputFolder: File
//    get() = if (ANDROID_GRADLE_PLUGIN_VERSION.major < 4 || (ANDROID_GRADLE_PLUGIN_VERSION.major == 4 && ANDROID_GRADLE_PLUGIN_VERSION.minor == 0)) {
//        this.blameLogOutputFolder40
//    } else {
//        this.blameLogOutputFolder41
//    }
//private val BaseVariant.blameLogOutputFolder40: File
//    get() = lowVersionScope.resourceBlameLogDir
//
//
//private val BaseVariant.blameLogOutputFolder41: File
//    get() = mergeResources.let {
//        ReflectionUtils.callMethod<DirectoryProperty>(it, MergeResources::class.java, "getBlameLogOutputFolder", arrayOf(), arrayOf()).asFile.get()
//    }