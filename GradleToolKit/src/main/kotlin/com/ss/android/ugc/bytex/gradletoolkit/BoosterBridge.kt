package com.ss.android.ugc.bytex.gradletoolkit

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.scope.getOutputDir
import com.didiglobal.booster.gradle.*
import java.io.File

internal val BaseVariant.bridgeAllClass: Collection<File>
    get() = this.allClasses.toList()
internal val BaseVariant.bridgeApk: Collection<File>
    get() = this.apk.toList()
internal val BaseVariant.bridgeMergedAssets: Collection<File>
    get() = this.mergedAssets.toList()
internal val BaseVariant.bridgeMergedRes: Collection<File>
    get() = this.mergedRes.toList()
internal val BaseVariant.bridgeMergedManifests: Collection<File>
    get() = if (ANDROID_GRADLE_PLUGIN_VERSION.major == 3 && ANDROID_GRADLE_PLUGIN_VERSION.minor == 4) {
        listOf(when (this) {
            is ApplicationVariant -> File(InternalArtifactType.MERGED_MANIFESTS.getOutputDir(scope.buildDir()), name)
            is LibraryVariant -> File(InternalArtifactType.LIBRARY_MANIFEST.getOutputDir(scope.buildDir()), name)
            else -> throw IllegalArgumentException(this.name)
        })
    } else {
        this.mergedManifests.toList()
    }
internal val BaseVariant.bridgeMergedProcessedRes: Collection<File>
    get() = this.processedRes.toList()
internal val BaseVariant.bridgeSymbolList: Collection<File>
    get() = this.symbolList.toList()
internal val BaseVariant.bridgeSymbolListWithPackageName: Collection<File>
    get() = this.symbolListWithPackageName.toList()

