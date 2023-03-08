package mjs.cdc.helper

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import mjs.kotest.HtmlReporter

class KotestConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(
        HtmlReporter(reportFilename = "index.html")
    )
}