package pywrapper

import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import python.PyObject


typealias FuncPtr<T> = CPointer<CFunction<T>>
typealias PyObjectT = CPointer<PyObject>?
typealias PyMethodT = (PyObjectT, PyObjectT) -> PyObjectT
