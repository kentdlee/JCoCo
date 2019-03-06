package jcoco;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

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
/**
 *
 * @author kentd
 */
public class PyCallStack {

    private Stack<PyFrame> theStack = new Stack<PyFrame>();

    public PyCallStack() {
    }

    public Stack<PyFrame> getCallStack() {
        return theStack;
    }

    public void pushFrame(PyFrame frame) {
        theStack.push(frame);
        if (theStack.size() >= 1000) {
            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Call Stack Overflow.");
        }
    }
    
    public PyFrame peek() {
        return theStack.peek();
    }

    public void popFrame() throws EmptyStackException {
        theStack.pop();
    }

    public void printCallStack() {
        for (int k = 0; k < theStack.size(); k++) {
            System.err.println("=========> PC=" + (theStack.get(k).getPC()) + " in this function. ");
            if (theStack.get(k).getCode().getType().typeId() == PyType.PyTypeId.PyCodeType) {
                try {
                    System.err.println(theStack.get(k).getCode().prettyString("", true));
                } catch (PyException e) {
                    System.err.println("Unable to print traceback : " + e.getMessage());
                }
            }
        }
    }
}
