/**
 * PyList.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyList class implements lists similarly to the way they are
 * implemented in Python. PyLists have O(1) access time to any element
 * of the list. They are implemented using the java ArrayList class so they
 * exhibit the same characteristics as a java ArrayList. For instance,
 * appending to a PyList has the same running time as the ArrayList add method.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyList extends PyPrimitiveTypeAdapter {

    private ArrayList<PyObject> data;

    public PyList(ArrayList<PyObject> data) {
        super("list", PyTypeId.PyListType);
        this.data = data;
        initMethods(funs());
    }

    public PyObject getVal(int index) {
        if (index >= this.data.size()) {
            throw new PyException(ExceptionType.PYSTOPITERATIONEXCEPTION,
                    "Stop iteration: index out of range");
        }
        return this.data.get(index);
    }

    public void setVal(int index, PyObject val) {
        this.data.set(index, val);
    }

    public int len() {
        return this.data.size();
    }

    public ArrayList<PyObject> list() {
        return this.data;
    }

    @Override
    public String str() {
        String str = "[";
        ArrayList<PyObject> args = new ArrayList<PyObject>();
        for (int i = 0; i < this.data.size(); i++) {
            // Creating a new call stack here is a trade-off. This simplifies
            // the interface to the str() method, for instance. It only affects
            // usage of the debugger. Exceptions will still have the full traceback
            // but if the debugger is used, the call stack will stop at this call
            // for calls to "__repr__" in this case.
            str += ((PyStr) this.data.get(i).callMethod(new PyCallStack(), "__repr__", args)).str();

            if (i < data.size() - 1) {
                str += ", ";
            }
        }

        str += "]";

        return str;
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                PyInt intObj = (PyInt) args.get(0);

                return self.getVal(intObj.getVal());
            }
        });
        funs.put("__setitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 3) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                PyInt index = (PyInt) args.get(1);

                if (index.getVal() >= self.data.size()) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "List index out of bounds, size=" + self.data.size() + ", index=" + index.str());
                }
                //set the object at the index of the first argument to the second arg
                self.setVal(index.getVal(), args.get(0));

                return new PyNone();
            }
        });
        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                return new PyInt(self.data.size());
            }
        });
        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                return new PyListIterator(self);
            }
        });

        funs.put("append", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                self.data.add(args.get(0));

                return new PyNone();
            }
        });

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                if (args.get(0).getType().typeId() != PyType.PyTypeId.PyListType) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "TypeError: unsupported operand type(s) for +: 'list' and '" + arg.getType().str() + "'");
                }
                PyList other = (PyList) arg;

                ArrayList<PyObject> result = (ArrayList<PyObject>) self.data.clone();
                result.addAll(other.data);
                return new PyList(result);
            }
        });

        funs.put("__mul__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                PyObject arg = args.get(0);

                if (args.get(0).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "TypeError: unsupported operand type(s) for +: 'list' and '" + arg.getType().str() + "'");
                }
                PyInt other = (PyInt) arg;

                ArrayList<PyObject> result = new ArrayList<PyObject>();

                for (int k = 0; k < other.getVal(); k++) {
                    result.addAll(self.data);
                }

                return new PyList(result);
            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    return new PyBool(false);
                }

                PyList other = (PyList) args.get(0);
                ArrayList<PyObject> newargs = new ArrayList<PyObject>();

                if (self.data.size() != other.data.size()) {
                    return new PyBool(false);
                }

                for (int i = 0; i < self.data.size(); i++) {
                    newargs.add(other.data.get(i));
                    PyBool result = (PyBool) self.data.get(i).callMethod(callStack, "__eq__", newargs);
                    if (!result.getVal()) {
                        return result;
                    }

                    newargs.remove(newargs.size() - 1); // remove the argument from the vector
                }

                return new PyBool(true);

            }
        });

        funs.put("__ne__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyList self = (PyList) args.get(args.size() - 1);
                PyBool result = (PyBool) self.callMethod(callStack, "__eq__", selflessArgs(args));
                boolean v = result.getVal();

                if (v) {
                    return new PyBool(false);
                }

                return new PyBool(true);
            }
        });

        return funs;
    }
}
