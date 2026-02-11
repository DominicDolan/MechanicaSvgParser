package generator


const val indent = "    "

fun buildScript(builder: ScriptBuilderContext.() -> Unit): String {
    val items = ArrayList<ScriptBuilder>()
    val context = ClassBuilderContextImpl(items)

    builder(context)

    return buildString {
        items.forEach {
            appendLine(it.toString(indent))
        }
    }.trimIndent()
}

interface ScriptBuilder {
    fun toString(indent: String): String {
        return this.toString().prependIndent(indent)
    }
}

class RawScript(private val text: String): ScriptBuilder {
    override fun toString(): String {
        return text
    }
}


class VariableBuilder(private val name: String, private val mutability: String): ScriptBuilder {
    private var type: String = ""
    private var value: String = ""
    private var override: String = ""
    private var private: String = ""

    fun override(): VariableBuilder {
        override = "override "
        return this
    }

    fun private(): VariableBuilder {
        private = "private "
        return this
    }

    fun type(type: String): VariableBuilder {
        this.type = ": $type"
        return this
    }

    fun value(value: String): VariableBuilder {
        this.value = " = $value"
        return this
    }

    fun value(value: ScriptBuilder): VariableBuilder {
        this.value = " = $value"
        return this
    }

    override fun toString(): String {
        return "$override$private$mutability $name$type$value"
    }

    companion object {
        fun createVal(name: String): VariableBuilder {
            return VariableBuilder(name, "val")
        }

        fun createVar(name: String): VariableBuilder {
            return VariableBuilder(name, "var")
        }
    }
}


class ClassBuilder(private val type: String): ScriptBuilder {
    private val items = ArrayList<ScriptBuilder>()
    private val context = ClassBuilderContextImpl(items)

    private var extends = ""
    private var name = ""
    private var builder: ScriptBuilderContext.() -> Unit = {}

    fun extends(extends: String): ClassBuilder {
        this.extends = " : $extends"
        return this
    }

    fun name(name: String): ClassBuilder {
        this.name = name
        return this;
    }

    fun body(builder: ScriptBuilderContext.() -> Unit): ClassBuilder {
        this.builder = builder
        return this
    }

    override fun toString(): String {
        items.clear()

        builder(context)

        return buildString {
            appendLine("$type$name$extends {".replace("  ", " "))
            items.forEach {
                appendLine(it.toString(indent))
            }
            appendLine("}")
        }
    }

    companion object {
        fun createClass(name: String): ClassBuilder {
            return ClassBuilder("class ").name(name)
        }

        fun createInnerClass(name: String): ClassBuilder {
            return ClassBuilder("inner class ").name(name)
        }

        fun createObject(name: String): ClassBuilder {
            return ClassBuilder("object ").name(name)
        }

        fun createCompanionObject(name: String): ClassBuilder {
            return ClassBuilder("companion object ").name(name)
        }

        fun createAnonymousObject(): ClassBuilder {
            return ClassBuilder("object ")
        }
    }
}


private class ClassBuilderContextImpl(
    private val items: ArrayList<ScriptBuilder>) : ScriptBuilderContext {

    override fun addVal(name: String): VariableBuilder {
        val variable = VariableBuilder.createVal(name)
        items.add(variable)
        return variable
    }

    override fun addVar(name: String): VariableBuilder {
        val variable = VariableBuilder.createVar(name)
        items.add(variable)
        return variable
    }

    override fun addClass(name: String): ClassBuilder {
        val clazz = ClassBuilder.createClass(name)
        items.add(clazz)
        return clazz
    }

    override fun addInnerClass(name: String): ClassBuilder {
        val clazz = ClassBuilder.createInnerClass(name)
        items.add(clazz)
        return clazz
    }

    override fun addObject(name: String): ClassBuilder {
        val clazz = ClassBuilder.createObject(name)
        items.add(clazz)
        return clazz
    }

    override fun addCompanionObject(name: String): ClassBuilder {
        val clazz = ClassBuilder.createCompanionObject(name)
        items.add(clazz)
        return clazz
    }

    override fun addAnonymousObject(): ClassBuilder {
        val clazz = ClassBuilder.createAnonymousObject()
        items.add(clazz)
        return clazz
    }

    override fun addRawText(text: String) {
        val raw = RawScript(text)
        items.add(raw)
    }
}
