package generator


interface ScriptBuilderContext {
    fun addVal(name: String): VariableBuilder
    fun addVar(name: String): VariableBuilder
    fun addClass(name: String): ClassBuilder
    fun addInnerClass(name: String): ClassBuilder
    fun addObject(name: String): ClassBuilder
    fun addCompanionObject(name: String): ClassBuilder
    fun addAnonymousObject(): ClassBuilder
    fun addRawText(text: String)
}