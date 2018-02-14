/**
 * PyObjectAdapter.java Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This is the class implementation of PyObject. It is the super
 * class for all of the object classes in CoCo (PyInt, PyString, etc.).
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;

public class PyObjectAdapter implements PyObject {

    // These are the attributes of the object, whatever type of object this is.
    protected HashMap<String, PyObject> dict = new HashMap<String, PyObject>();

    // The attrs are the attributes to be set in each instance of a class. See
    // PyClass' initInstance for where PyMethod objects are installed in each
    // instance. KDL
    protected HashMap<String, PyObject> attrs = new HashMap<String, PyObject>();
    protected String name;
    protected PyType.PyTypeId type;

    public PyObjectAdapter(String name, PyType.PyTypeId type) {
        this();
        this.name = name;
        this.type = type;
    }

    public PyObjectAdapter() {

        name = "PyObject()";
        type = PyType.PyTypeId.PyClassType;
        PyObjectAdapter self = this;
        this.dict.put("__str__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return new PyStr(self.str());
            }
        });

        this.dict.put("__hash__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unhashable type: '" + self.getType().str() + "'");
            }
        });

        this.dict.put("__repr__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return self.callMethod(callStack, "__str__", args);
            }
        });

        this.dict.put("__iter__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: '" + self.getType().str() + "' object is not iterable");
            }
        });

        this.dict.put("__type__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return (PyObject) self.getType();
            }
        });
    }

    @Override
    public PyObject callMethod(PyCallStack callStack, String name, ArrayList<PyObject> args) {
        PyCallable mbr = null;

        if (this.dict.containsKey(name)) {
            mbr = (PyCallable) this.dict.get(name);
            return mbr.__call__(callStack, args);
        }

        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "TypeError: '" + this.getType().str() + "' object has no attribute '" + name + "'");
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(type);
    }

    @Override
    public String str() {
        return "<" + this.name + " object at 0x" + Integer.toHexString(System.identityHashCode(this)) + ">";
    }

    @Override
    public String toString() {
        // Creating a new call stack here is a trade-off. This simplifies
        // the interface to the str() method, for instance. It only affects
        // usage of the debugger. Exceptions will still have the full traceback
        // but if the debugger is used, the call stack will stop at this call
        // for calls to "__repr__" in this case.
        PyStr s = (PyStr) callMethod(new PyCallStack(), "__repr__", new ArrayList<PyObject>());
        return s.str();
    }

    @Override
    public void set(String key, PyObject value) {
        this.dict.put(key, value);
    }

    @Override
    public PyObject get(String key) {
        if (this.attrs.containsKey(key)) {
            return this.attrs.get(key);
        }

        if (this.dict.containsKey(key)) {
            return this.dict.get(key);
        }

        throw new PyException(ExceptionType.PYATTRERROR,
                "AttributeError: '" + this.getType().str() + "' object has no attribute '" + key + "'");

    }

    // This method and equals need to be overridden to allow them to be 
    // added to HashMaps if the __hash__ and the __eq__ are both implemented.
    @Override
    public int hashCode() {
        ArrayList<PyObject> args = new ArrayList<PyObject>();

        // Creating a new call stack here is a trade-off. This simplifies
        // the interface to the str() method, for instance. It only affects
        // usage of the debugger. Exceptions will still have the full traceback
        // but if the debugger is used, the call stack will stop at this call
        // for calls to "__hash__" in this case.
        PyInt val = (PyInt) this.callMethod(new PyCallStack(), "__hash__", args);

        return val.getVal();
    }

    @Override
    public boolean equals(Object o) {
        ArrayList<PyObject> args = new ArrayList<PyObject>();

        PyObject other = (PyObject) o;

        args.add(other);
        // Creating a new call stack here is a trade-off. This simplifies
        // the interface to the str() method, for instance. It only affects
        // usage of the debugger. Exceptions will still have the full traceback
        // but if the debugger is used, the call stack will stop at this call
        // for calls to "__eq__" in this case.
        PyBool bool = (PyBool) this.callMethod(new PyCallStack(), "__eq__", args);

        return bool.getVal();
    }

    public void initMethods(HashMap<String, PyCallable> funs) {
        for (String key : funs.keySet()) {
            this.dict.put(key, new PyMethod(key, this, funs.get(key)));
        }
    }

    public static ArrayList<PyObject> newargs() {
        return new ArrayList<PyObject>();
    }

    public static ArrayList<PyObject> selflessArgs(ArrayList<PyObject> args) {
        ArrayList<PyObject> newargs = ((ArrayList<PyObject>) args.clone());
        newargs.remove(newargs.size() - 1);
        return newargs;
    }
}
