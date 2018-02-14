/**
 * PyTupleIterator.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
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

public class PyTupleIterator extends PyPrimitiveTypeAdapter {

    private PyTuple tuple;
    private int index;

    public PyTupleIterator(PyTuple tuple) {
        super("tuple_iterator", PyType.PyTypeId.PyTupleIteratorType);
        initMethods(funs());

        this.tuple = tuple;
        this.index = 0;
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

                PyTupleIterator self = (PyTupleIterator) args.get(args.size() - 1);

                return self;
            }
        });

        funs.put("__next__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTupleIterator self = (PyTupleIterator) args.get(args.size() - 1);

                return self.tuple.getVal(self.index++);
            }
        });

        return funs;
    }
}
