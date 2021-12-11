#include <Python.h>
#include "libkotlin_python_ext_api.h"

extern "C" {
PyMODINIT_FUNC PyInit_pykt(void) {
    return (PyObject*)libkotlin_python_ext_symbols()->kotlin.root.initialize();
}
}