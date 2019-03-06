/**
 * PyFloat.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This class defines the floating point numnbers of CoCo. While the type is
 * called float, the values are actually doubles as shown below.
 */
package jcoco;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyType.PyTypeId;

public class PyFloat extends PyPrimitiveTypeAdapter {

    protected double val;
    private PyFloat self;

    public PyFloat(double fVal) {
        super("float",PyTypeId.PyFloatType);
        self = this;
        this.val = fVal;
        initMethods(funs());

    }

    public double getVal() {
        return this.val;
    }

    @Override
    public String str() {
        DecimalFormat df = new DecimalFormat("0.0###############");
        //df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(this.val);
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);

                return new PyInt(new Double(self.val).hashCode());
            }
        });

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyFloat(self.val + x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val + y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val + z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for +: 'float' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__sub__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyFloat(self.val - x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val - y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val - z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for -: 'float' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__mul__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyFloat(self.val * x.getVal());

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(self.val * y.getVal());

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        return new PyFloat(self.val * z.getIntVal());

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for *: 'float' and '" + arg.getType().str() + "'");
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        return new PyFloat(Math.pow(self.val, x.getVal()));

                    case PyFloatType:
                        y = (PyFloat) arg;
                        return new PyFloat(Math.pow(self.val, y.getVal()));

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for *: 'float' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__mod__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyInt x;
                PyFloat y;
                double d;
                int i;

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                switch (arg.getType().typeId()) {
                    case PyIntType:
                        x = (PyInt) arg;
                        if (x.getVal() == 0) {
                            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        i = (int)(self.val / x.getVal());
                        d = self.val - i * x.getVal();
                        return new PyFloat(d);

                    case PyFloatType:
                        y = (PyFloat) arg;
                        if (y.getVal() == 0) {
                            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        i = (int)(self.val / y.getVal());
                        d = self.getVal() - i * y.getVal();
                        return new PyFloat(d);

                    case PyBoolType:
                        PyBool z = (PyBool) arg;
                        if (z.getIntVal() == 0) {
                            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "ZeroDivisionError: division or modulo by zero");
                        }
                        return new PyFloat(self.val - (int) self.val);

                    default:
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "TypeError: unsupported operand type(s) for %: 'float' and '" + arg.getType().str() + "'");
                }
            }
        });

        funs.put("__int__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);

                return new PyInt((int) self.val);
            }
        });

        funs.put("__bool__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);

                if (self.val == 0.0) {
                    return new PyBool(false);
                }

                return new PyBool(true);
            }
        });

        funs.put("__float__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);

                return self;
            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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
                                "TypeError: unorderable types: float() < " + arg.getType().str() + "()");
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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
                                "TypeError: unorderable types: float() <= " + arg.getType().str() + "()");
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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
                                "TypeError: unorderable types: float() > " + arg.getType().str() + "()");
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

                PyFloat self = (PyFloat) args.get(args.size() - 1);
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
                                "TypeError: unorderable types: float() >= " + arg.getType().str() + "()");
                }
            }
        });

        return funs;
    }

}
