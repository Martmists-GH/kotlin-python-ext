package pykt

import kotlinx.cinterop.staticCFunction
import python.Py_None
import pywrapper.PyObjectT
import pywrapper.ext.incref
import pywrapper.ext.pydef

val helloFunc = staticCFunction { self: PyObjectT, args: PyObjectT ->
    println("Hello world!")
    Py_None.incref()
}.pydef("hello", "Prints hello world")
