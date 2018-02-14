/**
 * PyFunList.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * Not Yet Implemented
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PyFunList extends PyPrimitiveTypeAdapter {

    private PyFunListElm data = null;

    public PyFunList() {
        super("funlist", PyType.PyTypeId.PyFunListType);
        PyFunList self = this;
        initMethods(funs());

    }

    public PyFunList(ArrayList<PyObject> lst) {
        this();

        int k;
        PyFunListElm tmp = null;
        for (k = lst.size() - 1; k >= 0; k--) {
            tmp = new PyFunListElm(lst.get(k), tmp);
        }
        data = tmp;

    }

    public PyFunList(PyFunListElm data) {
        this();
        this.data = data;
    }

    public PyFunList(PyObject h, PyFunList t) {
        this();
        this.data = new PyFunListElm(h, t.getElm());
    }

    public PyFunListElm getElm() {
        return data;
    }

    @Override
    public String str() {
        String s = "[";
        ArrayList<PyObject> args = new ArrayList<PyObject>();

        if (data != null) {
            s += data.repr();
        }

        s += "]";

        return s;
    }

    public PyObject getHead() {
        return data.getHead();
    }

    public PyObject getTail() {
        return new PyFunList(data.getTail());
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                if (self.data == null) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Attempt to index into an empty funlist.");
                }

                PyInt intObj = (PyInt) args.get(0);
                int index = intObj.getVal();

                if (index >= self.data.getLen()) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Index out of range on funlist.");
                }

                PyFunListElm tmp = self.data;

                for (int k = 0; k < index; k++) {
                    tmp = tmp.getTail();
                }

                return tmp.getHead();
            }
        });

        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                if (self.data == null) {
                    return new PyInt(0);
                }

                return new PyInt(self.data.getLen());
            }
        });

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                return new PyFunListIterator(self);
            }
        });

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);
                PyFunList other = (PyFunList) args.get(0);
                PyStack<PyObject> tmpStack = new PyStack<PyObject>();
                PyFunListElm tmp = self.data;
                PyObject val;

                while (tmp != null) {
                    tmpStack.push(tmp.getHead());
                    tmp = tmp.getTail();
                }

                tmp = other.data;

                while (!tmpStack.isEmpty()) {
                    val = tmpStack.pop();
                    tmp = new PyFunListElm(val, tmp);
                }

                return new PyFunList(tmp);
            }
        });

        funs.put("head", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                if (self.data == null) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "Attempt to get head of empty funlist");
                }

                return self.getElm().getHead();
            }
        });

        funs.put("tail", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                if (self.data == null) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "Attempt to get tail of empty funlist");
                }

                return new PyFunList(self.getElm().getTail());

            }
        });

        funs.put("concat", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);
                String s = "";

                PyFunListElm tmp = self.data;

                while (self.data != null) {
                    s += self.data.getHead().str();
                    self.data = self.data.getTail();
                }

                return new PyStr(s);
            }
        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    return new PyBool(false);
                }

                PyFunList other = (PyFunList) args.get(0);
                PyFunListElm tmp = self.data;
                PyFunListElm otherTmp = other.data;

                if (tmp == null) {
                    return new PyBool(otherTmp == null);
                }

                if (tmp.getLen() != otherTmp.getLen()) {
                    return new PyBool(false);
                }
                ArrayList<PyObject> newargs = new ArrayList<PyObject>();

                while (tmp != null) {
                    newargs.add(otherTmp.getHead());

                    PyBool result = (PyBool) tmp.getHead().callMethod(callStack,"__eq__", newargs);
                    newargs.remove(newargs.size() - 1);

                    if (!result.getVal()) {
                        return result;
                    }

                    tmp = tmp.getTail();
                    otherTmp = otherTmp.getTail();
                }

                return new PyBool(true);
            }
        });

        funs.put("__ne__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);

                PyBool result = (PyBool) self.callMethod(callStack,"__eq__", selflessArgs(args));
                boolean v = result.getVal();

                if (v) {
                    return new PyBool(false);
                }

                return new PyBool(true);
            }
        });

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFunList self = (PyFunList) args.get(args.size() - 1);
                PyFunListElm current = self.data;
                int total = 0;
                int val;

                while (current != null) {
                    val = ((PyInt) (current.getHead().callMethod(callStack,"__hash__", args))).getVal();

                    total = (total + (val % (Integer.MAX_VALUE / 2))) % (Integer.MAX_VALUE / 2);
                    current = current.getTail();
                }

                return new PyInt(total);
            }
        });

        return funs;
    }
}
