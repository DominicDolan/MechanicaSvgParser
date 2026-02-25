package utils

import org.intellij.lang.annotations.Language

fun String.camelToSnakeCase(): String {
    return "(?<=[a-zA-Z])[A-Z]"
        .toRegex()
        .replace(this) {
            "_${it.value}"
        }
}

/*

const camelize = s => s.replace(/-./g, x=>x[1].toUpperCase())
const words = ["stack-overflow","camel-case","alllowercase","allcapitalletters","custom-xml-parser","api-finder","json-response-data","person20-address","user-api20-endpoint"];
console.log(words.map(camelize));
 */

@Language("JavaScript")
val t = """
const camelize = s => s.replace(/-./g, x=>x[1].toUpperCase())
const words = ["stack-overflow","camel-case","alllowercase","allcapitalletters","custom-xml-parser","api-finder","json-response-data","person20-address","user-api20-endpoint"];
console.log(words.map(camelize));

"""

fun String.kebabToCamelCase(): String {
    return "-."
        .toRegex()
        .replace(this) {
            it.value[1].uppercase()
        }
}

fun String.kebabToPascalCase(): String {
    return this.kebabToCamelCase().replaceFirstChar { it.uppercaseChar() }
}