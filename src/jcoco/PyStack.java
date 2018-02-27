/**
 * PyStack.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * Here we have an example of a generic class. This generic class is used
 * for both the operand stack (in PyFrame) and the block stack.
 * The code is written generically so that a stack is a stack of some other
 * type of element. The __PyStackElement defines a linked list of
 * the elements to be put on the stack. The toString method can
 * be used in debugging to see the contents of a stack.
 *
 * Since we really don't know the type of T, we cannot delete the
 * elements that are pushed on the stack when deleting a stack.
 * This is a potential memory leak. It might be safer to throw an
 * exception if a non-empty stack is deleted. However, if we have
 * stack of ints, then the ints would not have to be deleted. So
 * this is left up to the programmer to know what they are doing.
 * If the stack is a stack of pointers to dynamically allocated
 * objects, the stack should be emptied before it is deleted.
 * Otherwise, if T is anything else then explicitly deleting the
 * T elements is not necessary and is implicitly handled in
 * this implementation (when deleting the __PyStackElements).
 */
package jcoco;

import jcoco.PyException.ExceptionType;

public class PyStack<T> {

    private class __PyStackElement<T> {

        public T object;
        public __PyStackElement<T> next;

        public __PyStackElement(T obj) {
            this.object = obj;
            this.next = null;
        }
    }

    private int count;
    private __PyStackElement<T> tos;

    public PyStack() {
        this.count = 0;
        this.tos = null;
    }

    public String toString() {
        // temporarily turn off stepping if it is on.
        boolean debugging = JCoCo.stepOverInstructions;
        JCoCo.stepOverInstructions = false;

        StringBuffer out = new StringBuffer();

        out.append("top\n---\n");

        __PyStackElement<T> cursor;

        cursor = this.tos;

        while (cursor != null) {
            try {
                out.append(cursor.object + "\n");
            } catch (PyException ex) {
                try {
                    out.append(((PyObject) cursor.object).str() + "\n");
                } catch (PyException ex2) {
                    out.append("<" + ((PyObject) cursor.object).getType() + " object at 0x" + Integer.toHexString(System.identityHashCode(this)) + ">\n");
                }
            }
            cursor = cursor.next;
        }

        out.append("---\n");

        // restore step over debugging if active.
        JCoCo.stepOverInstructions = debugging;

        return out.toString();
    }

    // This is used only by debugging to leave out marker objects when looking at the 
    // operand stack. This is specific to JCoCo.
    public String toStringNoMarkers() {
        // temporarily turn off stepping if it is on.
        boolean debugging = JCoCo.stepOverInstructions;
        JCoCo.stepOverInstructions = false;

        StringBuffer out = new StringBuffer();

        out.append("top\n---\n");

        __PyStackElement<T> cursor;

        cursor = this.tos;

        while (cursor != null) {
            if (!cursor.object.toString().equals("Marker")) {
                try {
                    out.append(cursor.object + "\n");
                } catch (PyException ex) {
                    try {
                        out.append(((PyObject) cursor.object).str() + "\n");
                    } catch (PyException ex2) {
                        out.append("<" + ((PyObject) cursor.object).getType() + " object at 0x" + Integer.toHexString(System.identityHashCode(this)) + ">\n");
                    }
                }
            }
            cursor = cursor.next;
        }

        out.append("---\n");

        // restore step over debugging if active.
        JCoCo.stepOverInstructions = debugging;

        return out.toString();
    }

    public int getCount() {
        return this.count;
    }

    public T pop() {
        if (this.tos != null) {
            __PyStackElement<T> elem = this.tos;
            T val = elem.object;
            this.tos = this.tos.next;

            count--;
            return val;
        }
        throw new PyException(ExceptionType.PYEMPTYSTACKEXCEPTION, "Attempt to pop empty stack.");
    }

    public void push(T obj) {
        __PyStackElement<T> elem = new __PyStackElement<T>(obj);
        elem.next = this.tos;
        this.tos = elem;
        count++;
    }

    public T top() {
        if (this.tos != null) {
            T val = this.tos.object;
            return val;
        }
        throw new PyException(ExceptionType.PYEMPTYSTACKEXCEPTION, "Attempt to get top of empty stack.");
    }

    public boolean isEmpty() {
        return (this.tos == null);
    }
}
