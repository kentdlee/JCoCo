package jcoco;

import java.util.ArrayList;

/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on March 7, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This is the range type which is called by the writing range
 * in a Python program. The call method creates a range object. 
 */

public class PyRangeType extends PyType {

    public PyRangeType() {
        super("range", PyType.PyTypeId.PyRangeTypeId);

    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

        if (args.size() == 0) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1, 2, or 3 arguments, got " + args.size());
        }

        if (args.size() > 3) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1, 2, or 3 arguments, got " + args.size());
        }

        int start;
        int stop;
        int increment;

        switch (args.size()) {
            case 1:
                if (args.get(0).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }

                start = 0;
                stop = ((PyInt) args.get(0)).getVal();
                increment = 1;
                break;
            case 2:
                if (args.get(0).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }
                if (args.get(1).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }

                start = ((PyInt) args.get(1)).getVal();
                stop = ((PyInt) args.get(0)).getVal();
                increment = 1;
                break;
            case 3:
                if (args.get(0).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }
                if (args.get(1).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }

                if (args.get(2).getType().typeId() != PyType.PyTypeId.PyIntType) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "range arguments must be of int type.");
                }

                start = ((PyInt) args.get(2)).getVal();
                stop = ((PyInt) args.get(1)).getVal();
                increment = ((PyInt) args.get(0)).getVal();

                break;
            default:
                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Incorrect number of arguments for built-in range function.");
        }

        return new PyRange(start, stop, increment);
    }

}
