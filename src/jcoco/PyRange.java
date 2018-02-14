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
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;

public class PyRange extends PyPrimitiveTypeAdapter {

    private int start, stop, increment;

    public PyRange(int start, int stop, int increment) {
        super("range", PyType.PyTypeId.PyRangeTypeId);
        initMethods(funs());

        this.start = start;
        this.stop = stop;
        this.increment = increment;

    }

    @Override
    public String str() {
        return "range(" + start + "," + stop + "," + increment + ")";
    }

    public PyObject indexOf(int index) throws PyException {
        int val = start + index * increment;

        if (increment > 0 && val >= stop) {
            throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION, "Stop Iteration");
        }

        if (increment < 0 && val <= stop) {
            throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION, "Stop Iteration");
        }

        return new PyInt(start + increment * index);

    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyRange self = (PyRange) args.get(args.size() - 1);

                return new PyRangeIterator(self);
            }
        });

        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyRange self = (PyRange) args.get(args.size() - 1);

                return new PyInt((self.stop - self.start) / self.increment);
            }
        });

        funs.put("__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyRange self = (PyRange) args.get(args.size() - 1);
                PyInt indexObj = (PyInt) args.get(0);
                int index = indexObj.getVal();

                return self.indexOf(index);
            }
        });

        funs.put("__list__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                ArrayList<PyObject> largs = new ArrayList<PyObject>();

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyRange self = (PyRange) args.get(args.size() - 1);

                int k;
                for (k = self.start; k < self.stop; k = k + self.increment) {
                    largs.add(new PyInt(k));
                }

                return new PyList(largs);
            }
        });

        return funs;
    }
}
