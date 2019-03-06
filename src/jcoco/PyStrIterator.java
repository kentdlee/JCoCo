/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Mar 3, 2017.
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

public class PyStrIterator extends PyPrimitiveTypeAdapter {

    private PyStr str;
    private int index = 0;

    PyStrIterator(PyStr str) {
        super("str_iterator",PyType.PyTypeId.PyStrIteratorType);
        initMethods(funs());
        this.str = str;
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

                PyStrIterator self = (PyStrIterator) args.get(args.size() - 1);

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

                PyStrIterator self = (PyStrIterator) args.get(args.size() - 1);

                if (self.index == self.str.str().length()) {
                    throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION, "Stopping iteration");
                }

                return new PyStr((new Character(self.str.str().charAt(self.index++)).toString()));
            }
        });

        return funs;
    }
}
