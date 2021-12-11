package pykt

import kotlinx.cinterop.ptr
import python.*
import pywrapper.PyObjectT
import pywrapper.builders.makeModule

val mod = makeModule(
    km_name="pykt",
    km_methods= listOf(
        helloFunc,
    ),
)

fun createPyKtModule(): PyObjectT {
    val obj = PyModule_Create2(mod.ptr, PYTHON_API_VERSION)

    if (PyType_Ready(PyType_Configurable.ptr) < 0) {
        PyErr_Print()
        return null
    } else {
        PyModule_AddType(obj, PyType_Configurable.ptr)
    }

    if (PyType_Ready(PyType_NativeCounter.ptr) < 0) {
        PyErr_Print()
        return null
    } else {
        PyModule_AddType(obj, PyType_NativeCounter.ptr)
    }

    return obj
}
