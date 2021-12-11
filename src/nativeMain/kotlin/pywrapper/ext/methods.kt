package pywrapper.ext

import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import python.METH_NOARGS
import python.PyMethodDef
import pywrapper.FuncPtr
import pywrapper.PyMethodT
import pywrapper.builders.makeString

internal inline fun FuncPtr<PyMethodT>.pydef(name: String, doc: String? = null, flags: Int = METH_NOARGS): CValue<PyMethodDef> {
    return cValue {
        ml_name = makeString(name)
        ml_doc = doc?.let(::makeString)
        ml_flags = flags
        ml_meth = this@pydef
    }
}
