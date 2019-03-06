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

public class PyFunListIterator extends PyPrimitiveTypeAdapter {

    private PyFunListElm element;

    public PyFunListIterator(PyFunList lst) {
        super("funlist_iterator", PyType.PyTypeId.PyFunListIteratorType);
        initMethods(funs());
        this.element = lst.getElm();
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

                PyFunListIterator self = (PyFunListIterator) args.get(args.size() - 1);

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

                PyFunListIterator self = (PyFunListIterator) args.get(args.size() - 1);

                if (self.element == null) {
                    throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION,
                            "Stopping Iteration");
                }

                PyObject item = self.element.getHead();
                self.element = self.element.getTail();
                return item;
            }
        });

        return funs;
    }

}
