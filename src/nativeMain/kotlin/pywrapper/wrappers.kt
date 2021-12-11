package pywrapper

import kotlinx.cinterop.*
import python.*

internal val PyType_GenericAllocKt = staticCFunction { arg0: CPointer<PyTypeObject>?, arg1: Py_ssize_t ->
    return@staticCFunction PyType_GenericAlloc(arg0, arg1)
}
internal val PyType_GenericNewKt = staticCFunction { arg0: CPointer<PyTypeObject>?, arg1: CPointer<PyObject>?, arg2: CPointer<PyObject>? ->
    return@staticCFunction PyType_GenericNew(arg0, arg1, arg1)
}
internal val KtType_StableRefFree = staticCFunction { self: COpaquePointer? ->
    val selfObj: CPointer<KtPyObject> = self?.reinterpret() ?: return@staticCFunction
    val ref = selfObj.pointed.ktObject!!.asStableRef<Any>()
    ref.dispose()
}
internal val KtType_StableRefRepr = staticCFunction { self: PyObjectT ->
    val selfObj: CPointer<KtPyObject> = self?.reinterpret() ?: return@staticCFunction null
    val ref = selfObj.pointed.ktObject!!.asStableRef<Any>()
    return@staticCFunction PyUnicode_FromString(ref.get().toString())
}
