/**
 * PyBaseCallable.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This class is an implementation of PyCallable and PyObject only used in the
 * implementation of abstract classes in the dict object in PyObjectAdapter. This
 * class was necessary in order to avoid a circular dependency between
 * PyCallableAdapter and PyObjectAdapter; as PyCallableAdapter extends
 * PyObjectAdapter, the PyObjects put into dict cannot be anonymous instances of
 * the PyCallableAdapter class as that would result in a circular dependency. Thus,
 * PyBaseCallable is used so that when we create a new PyObjectAdapter
 * instance, we do not create a PyCallableAdapter instance for each entry in the
 * dictionary, which would then create a PyObjectAdapter instance and so on.
 *
 * THIS IS ONLY USED IN THE DICTIONARY IN PyObjectAdapter.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyBaseCallable implements PyCallable {

    private HashMap<String, PyCallable> dict = new HashMap<String, PyCallable>();

    public PyBaseCallable() {
        PyBaseCallable self = this;
        this.dict.put("__str__", new PyBaseCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return new PyStr(self.str());
            }
        });

        this.dict.put("__hash__", new PyBaseCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unhashable type: '" + self.getType().str() + "'");
            }

  
        });

        this.dict.put("__repr__", new PyBaseCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return self.callMethod(callStack,"__str__", args);
            }
        });

        this.dict.put("__type__", new PyBaseCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 argument, got " + args.size());
                }

                return (PyObject) self.getType();
            }

            @Override
            public PyObject callMethod(PyCallStack callStack, String s, ArrayList<PyObject> args) {
                throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call __call__ on PyBaseCallable object"); 
            }
        });
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call PyBaseCallableAdapter");
    }
    
    @Override
    public boolean equals(Object obj) {
        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call PyBaseCallableAdapter");
    }
    
    @Override
    public int hashCode() {
        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call PyBaseCallableAdapter");
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBaseCallable);
    }

    @Override
    public PyObject callMethod(PyCallStack callStack, String name, ArrayList<PyObject> args) {
        if (!this.dict.containsKey(name)) {
            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "TypeError: '" + this.getType().str() + "' object has no attribute '" + name + "'");
        }

        PyCallable mbr = (PyCallable) this.dict.get(name);

        return mbr.__call__(callStack, args);
    }

    @Override
    public void set(String key, PyObject value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PyObject get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String str() {
        return "<PyBaseCallable Object>";
    }
}
