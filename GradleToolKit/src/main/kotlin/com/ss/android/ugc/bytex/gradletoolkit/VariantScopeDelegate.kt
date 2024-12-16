package com.ss.android.ugc.bytex.gradletoolkit

import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import java.io.File

interface VariantScopeDelegate {
    fun consumesFeatureJars(): Boolean
    fun buildDir(): File
}

class LowVersionVariantScope(
    val scope: VariantScope
) : VariantScopeDelegate {
    override fun consumesFeatureJars(): Boolean {
        return scope.consumesFeatureJars()
    }

    override fun buildDir(): File {
        return scope.globalScope.buildDir
    }
}

class V74VariantScope(
    private val consumesFeatureJars: Boolean,
    private val buildDir: File,
) : VariantScopeDelegate {

    companion object {
        fun findVariantScope(variantManager: VariantManager, variantName: String): V74VariantScope? {
            var config: Any? = null
            for (info in variantManager.rfMethodInvoke("getMainComponents") as List<Any>) {
                val properties = info.rfMethodInvoke("getVariant")
                if (properties.rfMethodInvoke("getName") == variantName) {
                    config = properties
                }
            }
            config ?: return null
            return findVariantScopeByConfig(config)
        }

        //config: ComponentCreationConfig
        fun findVariantScopeByConfig(config: Any): V74VariantScope {
            val consumesFeatureJars = (config.rfMethodInvoke("getComponentType")
                .rfMethodInvoke("isBaseModule") as Boolean)
                    && (config
                .rfMethodInvoke("getOldVariantApiLegacySupport")
                .rfMethodInvoke("getBuildTypeObj")
                .rfMethodInvoke("isMinifyEnabled") as Boolean)
                    && (config
                .rfMethodInvoke("getGlobal")
                .rfMethodInvoke("getHasDynamicFeatures") as Boolean)

            val buildDir = config.rfMethodInvoke("getGlobal")
                .rfMethodInvoke("getGlobalArtifacts")
                .rfFiledInvoke("buildDirectory")
                .rfMethodInvoke("getAsFile")
                .rfMethodInvoke("get") as File
            return V74VariantScope(consumesFeatureJars, buildDir)
        }
    }


    override fun consumesFeatureJars(): Boolean {
        return consumesFeatureJars
    }

    override fun buildDir(): File {
        return buildDir
    }
}

fun Any.rfMethodInvoke(method: String, vararg args: Any): Any {
    return this.javaClass.getMethod(method).also { it.isAccessible = true }.let {
        if (args.isEmpty()) {
            it.invoke(this)
        } else {
            it.invoke(this, args)
        }
    }
}
fun Any.rfFiledInvoke(filed: String): Any {
    return this.javaClass.getDeclaredField(filed).also { it.isAccessible = true }.get(this)
}