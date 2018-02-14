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

public class PyFunListElm {

    private PyObject head;
    private PyFunListElm tail;
    private int len;

    public PyFunListElm(PyObject head, PyFunListElm tail) {
        this.head = head;
        this.tail = tail;
        if (tail != null) {
            len = 1 + tail.len;
        } else {
            len = 1;
        }
    }

    public String str()  {
        String s = head.str();

        if (tail != null) {
            s += ", " + tail.str();
        }

        return s;
    }

    public String repr()  {
        ArrayList<PyObject> args = new ArrayList<PyObject>();

        String s = head.callMethod("__repr__", args).str();

        if (tail != null) {
            s += ", " + tail.str();
        }

        return s;
    }

    public PyObject getHead() {
        return head;
    }

    public PyFunListElm getTail() {
        return tail;
    }

    public int getLen() {
        return len;
    }
}