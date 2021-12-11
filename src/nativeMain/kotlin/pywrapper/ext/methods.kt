package pywrapper.ext

import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import python.METH_NOARGS
import python.PyMethodDef
import pywrapper.PyMethodT
import pywrapper.builders.makeString

internal inline fun CPointer<CFunction<PyMethodT>>.pydef(name: String, doc: String? = null, flags: Int = METH_NOARGS): CValue<PyMethodDef> {
    return cValue {
        ml_name = makeString(name)
        ml_doc = doc?.let(::makeString)
        ml_flags = flags
        ml_meth = this@pydef
    }
}
