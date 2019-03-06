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

/**
 *
 * @author leekentd
 */
public class PyBaseCallableAdapter implements PyCallable {

    public PyBaseCallableAdapter() {
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call __call__ on PyCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PyFunctionType);
    }

    @Override
    public PyObject callMethod(PyCallStack callStack, String name, ArrayList<PyObject> args) {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call __call__ on PyCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String str() {
        return "PyBaseCallableAdapter()";
    }

    @Override
    public void set(String key, PyObject value) {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call set on PyBaseCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PyObject get(String key) {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call get on PyBaseCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.
    }

    // This method and equals need to be overridden to allow them to be 
    // added to HashMaps if the __hash__ and the __eq__ are both implemented.
    @Override
    public int hashCode() {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call hashCode on PyBaseCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object o) {
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call equals on PyBaseCallableAdapter object"); //To change body of generated methods, choose Tools | Templates.

    }

}
