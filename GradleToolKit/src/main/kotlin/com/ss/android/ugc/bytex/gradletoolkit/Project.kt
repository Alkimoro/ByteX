package com.ss.android.ugc.bytex.gradletoolkit

import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.builder.model.Version
import com.android.repository.Revision
import org.gradle.api.Project

//todo:fix me
val revision = Revision.parseRevision(Version.ANDROID_GRADLE_PLUGIN_VERSION)
fun Project.findVariantScope(variantName: String): VariantScopeDelegate? {
    return findVariantManager().findVariantScope(variantName)
}


private fun Project.findVariantManager(): VariantManager {
    return if (revision.major > 3 || revision.minor >= 6) {
        findVariantManager36()
    } else {
        findVariantManager35()
    }
}

private fun Project.findVariantManager35(): VariantManager {
    return project.plugins.findPlugin(com.android.build.gradle.AppPlugin::class.java)!!.variantManager
}

private fun Project.findVariantManager36(): VariantManager {
    return project.plugins.findPlugin("com.android.internal.application")!!.let {
        it.javaClass.getMethod("getVariantManager").invoke(it) as VariantManager
    }
}

private fun VariantManager.findVariantScope(variantName: String): VariantScopeDelegate? {
    return if (revision.major >= 8) {
        throw Exception("current AGP version is unsupported")
    } else if (revision.major >= 7 && revision.minor >= 4) {
        V74VariantScope.findVariantScope(this, variantName)
    } else if (revision.major < 4) {
        findVariantScope3X(variantName)?.let { LowVersionVariantScope(it) }
    } else if (revision.minor == 0) {
        findVariantScope40(variantName)?.let { LowVersionVariantScope(it) }
    } else {
        findVariantScope41(variantName)?.let { LowVersionVariantScope(it) }
    }
}

private fun VariantManager.findVariantScope3X(variantName: String): VariantScope? {
    return variantScopes.firstOrNull { it.fullVariantName == variantName }
}

private fun VariantManager.findVariantScope40(variantName: String): VariantScope? {
    return variantScopes.firstOrNull { it::class.java.getMethod("getName").invoke(it) == variantName }
}


private fun VariantManager.findVariantScope41(variantName: String): VariantScope? {
    for (info in this.javaClass.getMethod("getMainComponents").invoke(this) as List<Any>) {
        val properties = info.javaClass.getMethod("getProperties").invoke(info)
        if (properties.javaClass.getMethod("getName").invoke(properties) == variantName) {
            return properties.javaClass.getMethod("getVariantScope").invoke(properties) as VariantScope
        }
    }
    return null
}