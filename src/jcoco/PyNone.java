/**
 * PyNone.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This is the special value None that is returned by all functions if they
 * do not explicitly return a value. It is also the special value used as a
 * null reference.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyType.PyTypeId;

/**
 *
 * @author Jonathan Opdahl
 */
public class PyNone extends PyPrimitiveTypeAdapter {

    public PyNone() {
        super("None", PyTypeId.PyNoneType);
        initMethods(funs());
    }

    @Override
    public String str() {
        return "None";
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                return new PyInt(0);
            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyNoneType:
                        return new PyBool(true);

                    default:
                        return new PyBool(false);
                }
            }
        });

        funs.put("__ne__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyNoneType:
                        return new PyBool(false);

                    default:
                        return new PyBool(true);
                }
            }
        });

        funs.put("__lt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unorderable types: NoneType() < " + args.get(0).getType().str() + "()");
            }
        });

        funs.put("__le__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unorderable types: NoneType() <= " + arg.getType().str() + "()");
            }
        });
        funs.put("__gt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unorderable types: NoneType() > " + arg.getType().str() + "()");
            }
        });
        funs.put("__ge__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unorderable types: NoneType() >= " + arg.getType().str() + "()");
            }
        });

        funs.put("__float__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: float() argument must be a string or a number, not 'NoneType'");
            }
        });

        funs.put("__int__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: int() argument must be a string or a number, not 'NoneType'");
            }
        });

        funs.put("__bool__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                return new PyBool(false);
            }
        });

        return funs;
    }
}
