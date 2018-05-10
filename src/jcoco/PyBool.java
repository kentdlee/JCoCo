/**
 * PyBool.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyBool class is the JCoCo implementation of boolean values.
 *
 * The magic methods of this class are not implemented yet.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyType.PyTypeId;

public class PyBool extends PyPrimitiveTypeAdapter {

    private boolean val;

    public PyBool(boolean b) { 
        super("bool", PyTypeId.PyBoolType);
        this.val = b;
        initMethods(funs());
    }

    @Override
    public String str() {
        if (val)
            return "True";
       
        return "False";
    }
    
    public boolean getVal() {
        return this.val;
    }

    public int getIntVal() {
        if (val) {
            return 1;
        }

        return 0;
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }
                
                PyBool self = (PyBool) args.get(args.size() - 1);

                return new PyInt(self.getIntVal());
            }
        });

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__add__", newargs);

            }
        });

        funs.put("__sub__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__sub__", newargs);
            }
        });

        funs.put("__mul__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__mul__", newargs);

            }
        });

        funs.put("__truediv__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__truediv__", newargs);

            }
        });

        funs.put("__floordiv__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__floordiv__", newargs);

            }
        });

        funs.put("__mod__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__mod__", newargs);

            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__eq__", newargs);
            }
        });

        funs.put("__ne__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__ne__", newargs);
            }
        });

        funs.put("__lt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__lt__", newargs);
            }
        });

        funs.put("__le__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__le__", newargs);
            }
        });

        funs.put("__gt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__gt__", newargs);
            }
        });

        funs.put("__ge__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);
                ArrayList<PyObject> newargs = new ArrayList<>();
                newargs.add(args.get(0));

                return (new PyInt(self.getIntVal())).callMethod("__ge__", newargs);
            }
        });

        funs.put("__float__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);

                return new PyFloat(self.getIntVal());
            }
        });

        funs.put("__int__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeErro: expected 1 argument, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);

                return new PyInt(self.getIntVal());
            }
        });

        funs.put("__bool__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeErro: expected 1 argument, got " + args.size());
                }

                PyBool self = (PyBool) args.get(args.size() - 1);

                return self;
            }
        });
        
        return funs;
    }
}