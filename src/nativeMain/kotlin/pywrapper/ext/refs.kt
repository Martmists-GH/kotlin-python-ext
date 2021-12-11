package pywrapper.ext

import python.Py_DecRef
import python.Py_IncRef
import pywrapper.PyObjectT

internal inline fun PyObjectT.incref() : PyObjectT {
    this?.let(::Py_IncRef)
    return this
}

internal inline fun PyObjectT.decref() : PyObjectT {
    this?.let(::Py_DecRef)
    return this
}