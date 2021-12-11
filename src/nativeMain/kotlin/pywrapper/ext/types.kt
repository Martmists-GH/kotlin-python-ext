package pywrapper.ext

import kotlinx.cinterop.*
import python.KtPyObject
import python.PyObject

internal val CPointer<PyObject>.kt
    get(): CPointer<KtPyObject> = reinterpret()

internal inline fun <reified T: Any> CPointer<KtPyObject>.cast(): T {
    val ref = this.pointed.ktObject!!.asStableRef<T>()
    return ref.get()
}

