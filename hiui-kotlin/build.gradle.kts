@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kfc.library)
}

dependencies {
    jsMainImplementation(project(":hiui-core-kotlin"))

    jsMainImplementation(npmv("@hi-ui/hiui"))

    jsMainImplementation(enforcedPlatform(libs.kotlinw.wrappers.bom))
    jsMainImplementation(libs.kotlinw.react)
    jsMainImplementation(libs.kotlinw.react.dom)
}