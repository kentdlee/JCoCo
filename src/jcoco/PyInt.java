/**
 * PyInt.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The JCoCo implementation of Integer objects.
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;
import java.util.HashMap;

public class PyInt extends PyPrimitiveTypeAdapter {

    private int val;

    public PyInt(int iVal) {
        super("int",PyTypeId.PyIntType);
        initMethods(funs());
        this.val = iVal;
    }

    @Override
    public String str() {
        return this.val + "";
    }

    public int getVal() {
        return this.val;
    }

    public static HashMap<String, PyCallable> funs() {

        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);

                return new PyInt(Math.abs(self.val));
            }
        });

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyObject arg = args.get(0);
                PyInt self = (PyInt) args.get(args.size() - 1);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        // Math.addExact throws an Arithmetic overflow exception 
                        // if it overflows. 
                        return new PyInt(Math.addExact(self.val, x.val));
                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val + y.val);
                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val + z.getIntVal());
                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for +: 'int' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__sub__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyInt(Math.subtractExact(self.val,x.getVal()));
                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val - y.getVal());
                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val - z.getIntVal());
                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for -: 'int' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__mul__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        // Math.multiplyExact throws an Arithmetic overflow exception 
                        // if it overflows.
                        return new PyInt(Math.multiplyExact(self.val, x.getVal()));
                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val * y.getVal());
                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val * z.getIntVal());
                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for *: 'int' and '" + arg.getType().str() + "'");
                }

            }
        });

        funs.put("__pow__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyInt((int) Math.pow(self.val, x.getVal()));

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(Math.pow(self.val, y.getVal()));

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for *: 'int"
                                + "' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__truediv__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        if (x.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return new PyFloat(((double) self.val) / x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        if (y.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return new PyFloat(self.val / y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        if (z.getIntVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return new PyFloat(self.val);

                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for /: 'int' and '" + arg.getType().str() + "'");
                }

            }
        });

        funs.put("__floordiv__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        if (x.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return new PyInt(self.val / x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        if (y.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return new PyFloat((int) (self.val / y.getVal()));

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        if (z.getIntVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division by zero");
                        }
                        return self;

                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for //: 'int' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__mod__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        if (x.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        return new PyInt(self.val % x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        if (y.getVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        return new PyFloat((self.val - y.getVal()) - Math.floor((double) self.val - y.getVal()));

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        if (z.getIntVal() == 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        return new PyInt(0);

                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for %: 'int' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val == x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val == y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val == z.getIntVal());

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

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val != x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val != y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val != z.getIntVal());

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

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val < x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val < y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val < z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unorderable types: int() < " + arg.getType().str() + "()");
                }
            }
        });

        funs.put("__le__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val <= x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val <= y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val <= z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unorderable types: int() <= " + arg.getType().str() + "()");
                }
            }
        });

        funs.put("__gt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val > x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val > y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val > z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unorderable types: int() > " + arg.getType().str() + "()");
                }
            }
        });

        funs.put("__ge__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                PyObject arg = args.get(0);
                PyInt x;
                double d;
                PyFloat y;

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyBool(self.val >= x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyBool(self.val >= y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyBool(self.val >= z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unorderable types: int() >= " + arg.getType().str() + "()");
                }
            }
        });

        funs.put("__float__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }
                PyInt self = (PyInt) args.get(args.size() - 1);

                return new PyFloat(self.getVal());
            }
        });

        funs.put("__int__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);
                return self;
            }
        });

        funs.put("__bool__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);

                if (self.getVal() == 0) {
                    return new PyBool(false);
                }
                return new PyBool(true);
            }
        });

        funs.put("__str__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyInt self = (PyInt) args.get(args.size() - 1);

                return new PyStr(self.str());
            }
        });
        
        // This new magic method is not intended to be used this way, but it JCoCo
        // uses it to return a new instance of an object. This is used in PyType
        // when a type is called with no argument or actually any number of arguments
        // except 1. When a type is called with 1 argument, the argument is converted 
        // to the specified type.
        funs.put("__new__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 0) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 0 arguments, got " + args.size());
                }

                return new PyInt(0);
            }
        });

        return funs;
    }
}
