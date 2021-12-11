package pykt

import kotlinx.cinterop.*
import python.KtPyObject
import python.PyErr_SetString
import python.PyExc_MemoryError
import python.PyLong_FromLong
import pywrapper.KtType_StableRefFree
import pywrapper.KtType_StableRefRepr
import pywrapper.PyObjectT
import pywrapper.builders.makePyType
import pywrapper.ext.*

class NativeCounter : Configurable() {
    private var realValue by attribute("value", 0L)

    fun increment(): Long {
        realValue++
        return realValue
    }
}

private val increment = staticCFunction { self: PyObjectT, arg: PyObjectT ->
    val obj = self?.kt?.cast<NativeCounter>() ?: run {
        PyErr_SetString(PyExc_MemoryError, "increment called with null self")
        return@staticCFunction null
    }
    val result = obj.increment()
    return@staticCFunction PyLong_FromLong(result).incref()
}.pydef("increment", "increments the counter")

private val initNativeCounter = staticCFunction { self: PyObjectT, args: PyObjectT, kwargs: PyObjectT ->
    val selfObj: CPointer<KtPyObject> = self?.reinterpret() ?: return@staticCFunction -1
    val instance = NativeCounter()
    val ref = StableRef.create(instance)
    selfObj.pointed.ktObject = ref.asCPointer()
    return@staticCFunction 0
}

val PyType_NativeCounter = makePyType<NativeCounter>(
    ktp_methods=listOf(
        increment
    ),
    ktp_init=initNativeCounter,
    ktp_free=KtType_StableRefFree,
    ktp_repr= KtType_StableRefRepr,
    ktp_base=PyType_Configurable.asPointer()
)
