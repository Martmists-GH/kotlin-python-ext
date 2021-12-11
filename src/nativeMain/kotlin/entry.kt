import kotlinx.cinterop.CPointer
import pykt.createPyKtModule
import python.PyObject

fun initialize(): CPointer<PyObject>? {
    return createPyKtModule()
}
