package pykt

import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import python.*
import pywrapper.PyObjectT
import pywrapper.builders.makePyType
import pywrapper.ext.cast
import pywrapper.ext.incref
import pywrapper.ext.kt
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.reflect.KProperty


abstract class Configurable {
    internal val attrs = mutableMapOf<String, Property<*>>()

    inner class Property<T : Any>(private val name: String, private var value: T) {
        fun get(): T {
            return value
        }

        fun set(value: T) {
            this.value = value
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }
    }

    fun <T : Any> attribute(name: String, value: T) : Property<T> {
        val prop = Property(name, value)
        attrs[name] = prop
        return prop
    }
}

private val initConfigurable = staticCFunction { self: PyObjectT, args: PyObjectT, kwargs: PyObjectT ->
    PyErr_SetString(PyExc_NotImplementedError, "Cannot instantiate ${self!!.pointed.ob_type!!.pointed.tp_name!!.toKString()}")
    -1
}

private val getattroConfigurable = staticCFunction { self: PyObjectT, attr: PyObjectT ->
    val obj = self!!.kt.cast<Configurable>()
    val name = _PyUnicode_AsString!!.invoke(attr)!!.toKString()
    val attrObj = obj.attrs[name]

    if (attrObj != null) {
        val nativeValue = attrObj.get()
        when (nativeValue) {
            is Int -> PyLong_FromLong(nativeValue.toLong())
            is Long -> PyLong_FromLong(nativeValue)
            is Float -> PyFloat_FromDouble(nativeValue.toDouble())
            is Double -> PyFloat_FromDouble(nativeValue)
            is String -> PyUnicode_FromString(nativeValue)
            is Boolean -> PyBool_FromLong(if (nativeValue) 1 else 0)
            else -> {
                PyErr_SetString(PyExc_NotImplementedError, "Cannot convert ${nativeValue::class} to Python yet!")
                null
            }
        }
    } else {
        PyObject_GenericGetAttr(self, attr)
    }.incref()
}

private val setattroConfigurable = staticCFunction { self: PyObjectT, attr: PyObjectT, value: PyObjectT ->
    val obj = self!!.kt.cast<Configurable>()
    val name = _PyUnicode_AsString!!.invoke(attr)!!.toKString()
    val attrObj = obj.attrs[name]

    if (attrObj != null) {
        val new = when (val nativeValue = attrObj.get()) {
            is Int -> PyLong_AsLong(value).toInt()
            is Long -> PyLong_AsLong(value)
            is Float -> PyFloat_AsDouble(value).toFloat()
            is Double -> PyFloat_AsDouble(value)
            is String -> _PyUnicode_AsString!!.invoke(value)!!.toKString()
            is Boolean -> PyObject_IsTrue(value) == 1
            else -> {
                PyErr_SetString(PyExc_NotImplementedError, "Cannot convert ${nativeValue::class} to Native yet!")
                return@staticCFunction -1
            }
        }
        (attrObj as Configurable.Property<Any>).set(new)
        0
    } else {
        PyObject_GenericSetAttr(self, attr, value)
    }
}

val PyType_Configurable = makePyType<Configurable>(
    ktp_init = initConfigurable,
    ktp_getattro = getattroConfigurable,
    ktp_setattro = setattroConfigurable,
)
