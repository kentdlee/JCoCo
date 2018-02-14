/**
 * PyTuple.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * PyTuple objects are like PyList objects except that tuples are
 * immutable. We can get the length of a tuple, iterate over a tuple,
 * and index into a tuple. Tuples are constructed in Python as shown
 * below.
 * >>> t = ('a','b','c')
 * >>> type(t)
 * <class 'tuple'>
 * >>> for x in t: ... print(x) ... a b c >>>
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

class PyTuple extends PyPrimitiveTypeAdapter {

    protected ArrayList<PyObject> data;

    public PyTuple(ArrayList<PyObject> data) {
        super("tuple", PyTypeId.PyTupleType);
        initMethods(funs());
        this.data = data;
    }

    @Override
    public String str() {
        ArrayList<PyObject> args = new ArrayList<PyObject>();

        String str = "(";

        try {
            for (int i = 0; i < this.data.size(); i++) {
                // Creating a new call stack here is a trade-off. This simplifies
                // the interface to the str() method, for instance. It only affects
                // usage of the debugger. Exceptions will still have the full traceback
                // but if the debugger is used, the call stack will stop at this call
                // for calls to "__repr__" in this case.
                str += (this.data.get(i).callMethod(new PyCallStack(), "__repr__", args)).str();

                if (i < this.data.size() - 1) {
                    str += ", ";
                }
            }
        } catch (PyException e) {
            System.err.println(e.getMessage());
        }

        str += ")";

        return str;
    }

    public PyObject getVal(int index) {
        if (index >= this.data.size()) {
            throw new PyException(ExceptionType.PYSTOPITERATIONEXCEPTION, "Stop Iteration");
        }

        return data.get(index);
    }

    public int size() {
        return this.data.size();
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                int total = 0;
                int val;
                int k;

                PyTuple self = (PyTuple) args.get(args.size() - 1);

                for (k = 0; k < self.data.size(); k++) {
                    val = ((PyInt) (self.data.get(k).callMethod(callStack, "__hash__", args))).getVal();
                    total = (total + (val % Integer.MAX_VALUE / 2) % (Integer.MAX_VALUE / 2));
                }

                return new PyInt(total);
            }
        }
        );

        funs.put(
                "__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTuple self = (PyTuple) args.get(args.size() - 1);

                PyInt intObj = (PyInt) args.get(0);

                return self.getVal(intObj.getVal());
            }
        }
        );

        funs.put(
                "__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTuple self = (PyTuple) args.get(args.size() - 1);

                return new PyInt(self.data.size());
            }
        }
        );

        funs.put(
                "__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTuple self = (PyTuple) args.get(args.size() - 1);

                return new PyTupleIterator(self);
            }
        }
        );

        return funs;
    }
}
