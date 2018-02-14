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
import static jcoco.PyList.funs;

/**
 *
 * @author leekentd
 */
public class PyTurtle extends PyPrimitiveTypeAdapter {

    private static PyNone none = new PyNone();
    private static PyTurtleScreen screen = null;
    double x, y;
    double direction; //in degrees
    boolean penDown = true;

    //private ArrayList<PyObject> data;
    public PyTurtle() {
        super("Turtle", PyType.PyTypeId.PyTurtleType);
        initMethods(funs());

        x = 0;
        y = 0;

        if (screen == null) {
            screen = new PyTurtleScreen();
        }

        screen.registerTurtle(this);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void pendown() {
        penDown = true;
    }

    public void penup() {
        penDown = false;
    }

    public boolean isdown() {
        return penDown;
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("forward", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.screen.move(self, distance);
                return none;
            }
        });

        funs.put("right", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double amount = Double.valueOf(args.get(0).str());
                double rx = 3;
                double endRotation = self.direction + amount;

                while (self.direction < endRotation) {
                    self.direction = self.direction + rx;
                    screen.update();
                }
                
                self.direction = endRotation;
                screen.update();
                return none;
            }
        });

        funs.put("getscreen", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);

                return self.screen;
            }
        });

        return funs;
    }
}
