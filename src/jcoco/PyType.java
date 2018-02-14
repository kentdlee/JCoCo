/**
 * PyType.java Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: Most of the types in JCoCo are instances of this class. The
 * exceptions are range objects and exception objects. All other types of
 * objects share a common behavior and are instances of PyType. The jcoco.java
 * file contains the code that creates the type instances. There is one instance
 * of each different type of CoCo value. For instance, there is one instance of
 * PyType for all PyInt objects. All PyInt objects, when their type is requested
 * (via the type function) return the one instance of the PyInt type. This is
 * found by looking up the instance in the PyTypes map that is declared at the
 * bottom of this header file. The main.cpp code initializes this map.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;

public class PyType extends PyCallableAdapter {

    public enum PyTypeId {

        PyTypeType,
        PyClassType,
        PyNoneType,
        PyBoolType,
        PyIntType,
        PyFloatType,
        PyStrType,
        PyFunctionType,
        PyMethodType,
        PyBuiltInType,
        PyRangeTypeId,
        PyRangeIteratorType,
        PyListType,
        PyListIteratorType,
        PyFunListType,
        PyFunListIteratorType,
        PyStrIteratorType,
        PyCodeType,
        PyTupleType,
        PyTupleIteratorType,
        PyCellType,
        PyExceptionTypeId,
        PyDictType,
        PyDictKeyIteratorType,
        PyBaseCallable,
        PyMapType,
        PySuperTypeId,
        PyFileType,
        PyModuleType,
        PyMarkerType,
        PyTurtleType,
        PyTurtleScreenType
    }

    private String typeString;
    private PyTypeId index;

    public PyType(String typeString, PyTypeId id) {
        PyType self = this;
        this.typeString = typeString;
        this.index = id;

        this.dict.put("__str__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return new PyStr("<class '" + self.str() + "'>");
            }
        });

    }

    @Override
    public String str() {
        return this.typeString;
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyTypeType);
    }

    public PyTypeId typeId() {
        return index;
    }

    @Override
    public PyObject __call__(ArrayList<PyObject> args) {

        if (args.size() == 1) {
            PyObject arg = args.get(0);
            args.remove(0);
            String funName = "__" + this.str() + "__";
            return arg.callMethod(funName, args);
        }

        throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 argument, got " + args.size());
    }

    public void setInstanceFuns(HashMap<String, PyCallable> funs) {
        attrs.putAll(funs);
    }
}
