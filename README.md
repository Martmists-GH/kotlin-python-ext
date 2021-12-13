# Kotlin Python Ext

This is a proof of concept for a Python extension in Kotlin.    
It is recommended to read the [Official Python C API Documentation](https://docs.python.org/3/c-api/index.html) before getting started with this.    
If you have any other questions, feel free to contact me on Discord at `Martmists#3740`.    
For an (undocumented) example, you can refer to [kaudio-python](https://github.com/martmists-gh/kaudio-python)

## Usage

This is going to be a messy readme since I'll be cramming in everything I can without a good structure, feel free to PR a better one.

To start off you'll need to make a module definition:
```kotlin
val moduleDef = makeModule(
    km_name = "my_module",
)
```
then, in your initializer:
```kotlin
fun createPyKtModule(): PyObjectT {
    val obj = PyModule_Create2(moduleDef, PYTHON_API_VERSION)
    
    return obj
}
```
Now you can import your module!
To create a static function, you need to create a function first:
```kotlin
val helloFunc = staticCFunction { self: PyObjectT, args: PyObjectT ->
    println("Hello from Kotlin!")
    Py_None.incref()
}
```
Then we need a function definition:
```kotlin
val helloDef = helloFunc.pydef(
    "hello",  // function name
    "A simple function to say hello from Kotlin", // docstring
    METH_NOARGS,  // flags
)
```
Now we can add our function to our module:
```kotlin
val moduleDef = makeModule(
    km_name = "my_module",
    km_methods= listOf(
        helloDef,
    ),
)
```

Next up are classes! To create a class, you need to create a PyType:
```kotlin
class MyKotlinImpl {
    val n: Int = 0
    fun increment() {
        println(n++)
    }
}

val initImpl = staticCFunction { self: PyObjectT, args: PyObjectT, kwargs: PyObjectT ->
    // These generally work with StableRefs, and the default free implementation takes care of these.
    
    val selfObj: CPointer<KtPyObject> = self?.reinterpret() ?: return@staticCFunction -1
    
    val instance = MyKotlinImpl()  // initialize here, you can take parameters from args/kwargs using python C API if needed
    
    val ref = StableRef.create(instance)
    selfObj.pointed.ktObject = ref.asCPointer()
    return@staticCFunction 0
}

val myPyType = makePyType<MyKotlinImpl>(
    ktp_init=initImpl  // This is usually the only required field
)
```

Then we can add this to our module:
```kotlin
fun createPyKtModule(): PyObjectT {
    val obj = PyModule_Create2(moduleDef, PYTHON_API_VERSION)

    if (PyType_Ready(myPyType.ptr) < 0) {
        // An error occurred
        return null
    } else {
        // The class works, so add it to the module
        PyModule_AddType(obj, myPyType.ptr)
    }

    return obj
}
```

## Utilities

There's a couple functions that are generally useful when working with the Python C API, such as reference counting:

```kotlin
fun doSomething(arg: PyObjectT) {
    PyObject_Print(arg.incref())
}

fun freeOtherThing() {
    this.arg.decref()
}
```

Other utilities are present in this repo, specifically Configurable.    
Configurable allows you to define properties that get converted between kotlin and python automatically, though by default this only supports primitives and strings.    
To make a class extend configurable:
```kotlin
class MyConfigurableType: Configurable() {
    var prop by attribute("prop", true)  // second arg is default value, avoid nulls!
    
    fun thing() {
        println("prop: $prop")
    }
}

val myConfPyType = makePyType<MyConfigurableType>(
    ktp_base=PyType_Configurable.ptr,  // Extend Configurable (or a class that extends Configurable)
    ...
)
```
