/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 1, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyPrimitiveTypeAdapter exists because trying to combine this class
 * and PyObjectAdapter will lead to a circular dependency where 
 * PyObjectAdapter will end up creating other PyObjectAdapter 
 * instances (indirectly) when PyObjectAdapters are created through
 * inheritance. So, to break this cycle, classes looking to extend 
 * PyObjectAdapter can instead extend PyPrimitiveTypeAdapter. 
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author leekentd
 */
public class PyPrimitiveTypeAdapter extends PyObjectAdapter {

    public PyPrimitiveTypeAdapter(String name, PyType.PyTypeId type) {
        super(name, type);
        initMethods(primitiveFuns());
    }

    public static HashMap<String, PyCallable> primitiveFuns() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__str__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyPrimitiveTypeAdapter self = (PyPrimitiveTypeAdapter) args.get(args.size() - 1);

                return new PyStr(self.str());
            }
        });

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }
                
                PyPrimitiveTypeAdapter self = (PyPrimitiveTypeAdapter) args.get(args.size() - 1);

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unhashable type: '" + self.getType().str() + "'");
            }
        });

        funs.put("__repr__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyPrimitiveTypeAdapter self = (PyPrimitiveTypeAdapter) args.get(args.size() - 1);

                return self.callMethod(callStack,"__str__", newargs());
            }
        });

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyPrimitiveTypeAdapter self = (PyPrimitiveTypeAdapter) args.get(args.size() - 1);

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: '" + self.getType().str() + "' object is not iterable");
            }
        });

        funs.put("__type__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyPrimitiveTypeAdapter self = (PyPrimitiveTypeAdapter) args.get(args.size() - 1);

                return (PyObject) self.getType();
            }
        });

        return funs;
    }
}
