package pykt

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
    val obj = PyModule_Create2(mod, PYTHON_API_VERSION)

    if (PyType_Ready(PyType_Configurable) < 0) {
        PyErr_Print()
        return null
    } else {
        PyModule_AddType(obj, PyType_Configurable)
    }

    if (PyType_Ready(PyType_NativeCounter) < 0) {
        PyErr_Print()
        return null
    } else {
        PyModule_AddType(obj, PyType_NativeCounter)
    }

    return obj
}
