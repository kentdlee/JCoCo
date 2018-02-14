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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import static jcoco.PyList.funs;
import java.math.*;
import java.awt.geom.Point2D;

/**
 *
 * @author leekentd
 */
public class PyTurtle extends PyPrimitiveTypeAdapter {

    public static HashMap<String, Integer> rColorMap = new HashMap<String, Integer>();
    public static HashMap<String, Integer> gColorMap = new HashMap<String, Integer>();
    public static HashMap<String, Integer> bColorMap = new HashMap<String, Integer>();

    static {
        rColorMap.put("red", 255);
        gColorMap.put("red", 0);
        bColorMap.put("red", 0);

        rColorMap.put("green", 0);
        gColorMap.put("green", 255);
        bColorMap.put("green", 0);

        rColorMap.put("blue", 0);
        gColorMap.put("blue", 0);
        bColorMap.put("blue", 255);

        rColorMap.put("white", 255);
        gColorMap.put("white", 255);
        bColorMap.put("white", 255);

        rColorMap.put("black", 0);
        gColorMap.put("black", 0);
        bColorMap.put("black", 0);

        rColorMap.put("yellow", 255);
        gColorMap.put("yellow", 255);
        bColorMap.put("yellow", 0);
    }

    private static PyNone none = new PyNone();
    private static PyTurtleScreen screen = null;
    double x, y;
    double direction; //in degrees
    boolean ioInDegrees = true;
    boolean penDown = true;
    boolean hideTurtle = false;

    BufferedImage image = null;
    int width;
    int speed = 6;
    PyObject penColor; // this will either be a color string or a tuple of r,g,b
    PyObject fillColor;
    int rPen, gPen, bPen;
    int rFill, gFill, bFill;
    boolean filling = false;
    private PyTurtleScreen.FillArea fillArea = null;
    private int id;
    private static int turtleCount = 0;

    //private ArrayList<PyObject> data;
    public PyTurtle() {
        super("Turtle", PyType.PyTypeId.PyTurtleType);
        initMethods(funs());
        rPen = rColorMap.get("black");
        gPen = gColorMap.get("black");
        bPen = bColorMap.get("black");
        penColor = new PyStr("black");
        fillColor = new PyStr("black");
        id = turtleCount++;

        rFill = rPen;
        gFill = gPen;
        bFill = bPen;

        x = 0;
        y = 0;

        if (screen == null) {
            screen = new PyTurtleScreen();
        }

        java.net.URL imageURL = this.getClass().getResource("resources/arrow.png");
        //File imageFile = new File(imageURL);
        try {
            image = ImageIO.read(imageURL);

        } catch (Exception ex) {
            System.out.println("Unable to read arrow.png");
            ex.printStackTrace();
        }
        screen.registerTurtle(this);

    }

    public PyTurtle(PyTurtle orig) {
        //This is only called to make a Stamp.
        super("Stamp", PyType.PyTypeId.PyTurtleType);

        x = orig.x;
        y = orig.y;
        speed = orig.speed;
        direction = orig.direction;
        ioInDegrees = orig.ioInDegrees;
        penDown = orig.penDown;
        hideTurtle = orig.hideTurtle;
        image = orig.image;
        rPen = orig.rPen;
        gPen = orig.gPen;
        bPen = orig.bPen;
        rFill = orig.rFill;
        gFill = orig.gFill;
        bFill = orig.bFill;
    }

    public int width() {
        return width;
    }

    public int radius() {
        return image.getHeight() / 2;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distance(double x, double y) {

        double yd = y - this.y;
        double xd = x - this.x;
        double distance = Math.sqrt(xd * xd + yd * yd);
        return distance;
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void pencheck() {
        if (penDown = true) {
        }
    }

    public void pendown() {
        penDown = true;
    }

    public double heading() {
        return direction;
    }

    public void setHeading(double angle) {
        if (!ioInDegrees) {
            angle = Math.toDegrees(angle);
        }
        direction = angle;
    }

    public void penup() {
        penDown = false;
    }

    public boolean isdown() {
        return penDown;
    }

    public void hideturtle() {
        hideTurtle = true;
        screen.update();
    }

    public void showturtle() {
        hideTurtle = false;
        screen.update();
    }

    public boolean isVisible() {
        return !hideTurtle;
    }

    public boolean equals(Object obj) {
        PyTurtle t = (PyTurtle) obj;
        return this.id == t.id;
    }
    
    public int hashCode() {
        return this.id;
    }

    public void move(double distance) {
        this.move(distance, 0);
    }

    public void move(double distance, double angleOffset) {
        screen.doTheMove(this, distance, angleOffset);
        if (filling) {
            fillArea.addPoint(screen.convertToScreen(this.x, this.y));
        }
    }

    public void right(double amount) {
        double rx = this.speed;
        if (this.speed == 0) {
            rx = 999999999;
        }

        if (!ioInDegrees) {
            amount = Math.toDegrees(amount);
        }
        double endRotation = direction - amount;

        while (direction - rx > endRotation) {
            direction = direction - rx;
            screen.update();
        }

        direction = PyTurtleScreen.round(endRotation % 360, 5);
        screen.update();
    }

    public void left(double amount) {
        double rx = this.speed;
        if (this.speed == 0) {
            rx = 999999999;
        }
        if (!ioInDegrees) {
            amount = Math.toDegrees(amount);
        }
        double endRotation = direction + amount;

        while (direction + rx < endRotation) {
            direction = direction + rx;
            screen.update();
        }

        direction = PyTurtleScreen.round(endRotation % 360, 5);
        screen.update();
    }

    public void reset() {
        x = 0;
        y = 0;
        direction = 0;
        screen.resetTurtle(this);
    }

    public void clear() {
        screen.resetTurtle(this);
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("isvisible", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                return new PyBool(self.isVisible());
            }
        });

        funs.put("undo", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }
                PyTurtle self = (PyTurtle) args.get(0);
                self.screen.undo(self);
                return none;
            }
        });

        funs.put("stamp", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                return new PyInt(screen.makeStamp(self));
            }
        });

        funs.put("clearstamp", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                int stampID = Integer.valueOf(args.get(0).str());
                self.screen.clearStamp(stampID);
                return none;
            }

            private int resetStamp(PyTurtle self) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        funs.put("clearstamps", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }
                PyTurtle self;
                int n;
                if (args.size() == 1) {
                    self = (PyTurtle) args.get(0);
                    n = 0;

                } else {
                    self = (PyTurtle) args.get(1);
                    n = Integer.valueOf(args.get(0).str());

                }

                self.screen.clearStamps(self, n);
                return none;
            }
        });

        funs.put("heading", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                double heading = self.heading();
                if (!self.ioInDegrees) {
                    heading = Math.toRadians(heading);
                }
                return new PyFloat(heading);

            }
        });

        funs.put("pensize", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }
                if (args.size() == 1) {
                    PyTurtle self = (PyTurtle) args.get(0);
                    return new PyInt(self.width);
                }
                //Otherwise, setting self.width
                PyTurtle self = (PyTurtle) args.get(1);
                self.width = Integer.valueOf(args.get(0).str());
                return none;
            }
        });

        funs.put("width", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }
                if (args.size() == 1) {
                    PyTurtle self = (PyTurtle) args.get(0);
                    return new PyInt(self.width);
                }
                //Otherwise, setting self.width
                PyTurtle self = (PyTurtle) args.get(1);
                self.width = Integer.valueOf(args.get(0).str());
                return none;
            }
        });

        funs.put("distance", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 3) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(2);
                double y = Double.valueOf(args.get(1).str());
                double x = Double.valueOf(args.get(0).str());
                double distance = self.distance(x, y);
                return new PyFloat(distance);
            }
        });

        funs.put("towards", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 3 || args.size() > 3) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 arguments, got " + args.size());
                }
                PyTurtle self = (PyTurtle) args.get(2);
                double x = Double.valueOf(args.get(1).str());
                double y = Double.valueOf(args.get(0).str());
                double dx = (self.x - x);
                double dy = (self.y - y);
                double angle = Math.toDegrees(Math.atan(dy / dx));
                if (self.y > y) {
                    angle = angle + 180;
                }

                if (!self.ioInDegrees) {
                    angle = Math.toRadians(angle);
                }

                return new PyFloat(angle);

            }

        });

        funs.put("getpen", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                System.out.println(self);
                return self;
            }
        });

        funs.put("reset", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.reset();
                return none;
            }
        });

        funs.put("clear", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.clear();
                return none;
            }
        });

        funs.put("home", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.setHeading(0);
                double distance = self.distance(0, 0);
                double angle = 180 - Math.toDegrees(Math.acos(self.x / distance));

                if (self.y < 0) {
                    angle = -angle;
                }
                angle = PyTurtleScreen.round(angle, 5);
                self.move(distance, -angle);
                return none;
            }
        });

        funs.put("xcor", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                return new PyFloat(self.getX());

            }
        });

        funs.put("ycor", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                return new PyFloat(self.getY());

            }
        });

        funs.put("position", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                ArrayList<PyObject> arguments = new ArrayList<PyObject>();
                arguments.add(new PyFloat(self.x));
                arguments.add(new PyFloat(self.y));
                return new PyTuple(arguments);

            }
        });

        funs.put("pos", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                ArrayList<PyObject> arguments = new ArrayList<PyObject>();
                arguments.add(new PyFloat(self.x));
                arguments.add(new PyFloat(self.y));
                return new PyTuple(arguments);

            }
        });

        funs.put("setheading", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double angle = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    angle = Math.toDegrees(angle);
                }
                self.setHeading(angle);
                return none;
            }
        });

        funs.put("seth", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double angle = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    angle = Math.toDegrees(angle);
                }
                self.setHeading(angle);
                return none;
            }
        });

        funs.put("setx", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                self.x = Double.valueOf(args.get(0).str());

                return none;
            }
        });

        funs.put("sety", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                self.y = Double.valueOf(args.get(0).str());
                return none;
            }
        });

        funs.put("forward", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.move(distance);
                return none;
            }
        }
        );

        funs.put("fd", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.move(distance);
                return none;
            }
        });

        funs.put("backward", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 or 3 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.move(distance, 180);
                return none;
            }
        });

        funs.put("back", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 or 3 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.move(distance, 180);
                return none;
            }
        });

        funs.put("bk", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 or 3 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double distance = Double.valueOf(args.get(0).str());
                self.move(distance, 180);
                return none;
            }
        });

        funs.put("right", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double amount = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    amount = Math.toDegrees(amount);
                }
                self.right(amount);
                screen.update();
                return none;
            }
        });

        funs.put("rt", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double amount = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    amount = Math.toDegrees(amount);
                }
                self.right(amount);
                screen.update();
                return none;
            }
        });

        funs.put("left", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double amount = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    amount = Math.toDegrees(amount);
                }
                self.left(amount);
                screen.update();
                return none;
            }
        });

        funs.put("lt", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(1);
                double amount = Double.valueOf(args.get(0).str());
                if (!self.ioInDegrees) {
                    amount = Math.toDegrees(amount);
                }
                self.left(amount);
                screen.update();
                return none;
            }
        });

        funs.put("getscreen", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }
                //penDown = true;                                                                                        
                PyTurtle self = (PyTurtle) args.get(0);
                return self.screen;
            }
        });

        funs.put("isdown", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                return new PyBool(self.isdown());

            }
        });

        funs.put("penup", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.penup();
                return none;
            }
        });

        funs.put("up", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.penup();
                return none;
            }
        });

        funs.put("pendown", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.pendown();
                return none;
            }
        });

        funs.put("down", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.pendown();
                return none;
            }
        });

        funs.put("hideturtle", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.hideturtle();

                return none;
            }
        });

        funs.put("ht", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.hideturtle();

                return none;
            }
        });

        funs.put("showturtle", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.showturtle();
                return none;
            }
        });

        funs.put("st", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(0);
                self.showturtle();
                return none;
            }
        });

        funs.put("dot", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                int size = Math.max(self.width + 4, self.width * 2);

                if (args.size() == 2) {
                    size = ((PyInt) args.get(0)).getVal();
                }

                screen.makeDot(self, size);

                return none;
            }
        });

        funs.put("circle", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 4 || args.size() < 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2, 3 or 4 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                double radius = Double.valueOf(args.get(args.size() - 2).str());
                double extent;
                int steps;
                if (args.size() > 2) {
                    extent = Double.valueOf(args.get(args.size() - 3).str());

                } else {
                    extent = 360;
                }
                if (args.size() > 3) {
                    steps = Integer.valueOf(args.get(args.size() - 4).str());

                } else {
                    steps = 100;
                }

                double dTheta = 2 * Math.PI / steps;
                double dThetaDegrees = 360.0 / steps;
                double dx = radius * Math.cos(dTheta);
                double dy = radius * Math.sin(dTheta);
                double distance = Math.sqrt(Math.pow(dx - radius, 2) + dy * dy);
                double angleRadians = Math.acos(dx / radius);
                double angleDegrees = Math.toDegrees(angleRadians);

                double totalExtent = 0;
                double oldDirection = self.direction;
                double oldX = self.x;
                double oldY = self.y;
                while (totalExtent < extent) {
                    self.direction += angleDegrees;
                    self.move(distance);
                    totalExtent += dThetaDegrees;
                }
                self.direction = oldDirection;
                self.x = oldX;
                self.y = oldY;
                return none;
            }
        });

        funs.put("goto", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 3) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 arguments, got " + args.size());
                }
                double x = 0;
                double y = 0;
                PyTurtle self = null;

                //args[0] is y, args[1] is x, args[2] is self;
                self = (PyTurtle) args.get(2);
                x = Double.valueOf(args.get(1).str());
                y = Double.valueOf(args.get(0).str());

                double distance = self.distance(x, y);
                double angle = Math.toDegrees(Math.acos((x - self.x) / distance));
                if (self.y > y) {
                    angle = -angle;
                }
                double offset = angle - self.direction;

                self.move(distance, offset);
                return none;
            }

        });

        funs.put("speed", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                int theSpeed = 6;
                if (args.size() > 1) {
                    theSpeed = Integer.valueOf(args.get(0).str());
                }

                if (theSpeed > 10) {
                    theSpeed = 10;
                }

                if (theSpeed < 0) {
                    theSpeed = 0;
                }

                self.speed = theSpeed;

                return none;
            }

        });

        funs.put("color", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 3) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1, 2, or 3 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                if (args.size() == 1) {
                    ArrayList<PyObject> colors = new ArrayList<PyObject>();
                    colors.add(self.penColor);
                    colors.add(self.fillColor);
                    return new PyTuple(colors);
                }

                PyObject theColor = args.get(0);
                self.penColor = theColor;
                self.fillColor = theColor;

                if (theColor.getType().typeId() == PyType.PyTypeId.PyStrType) {
                    self.rPen = rColorMap.get(theColor.str().toLowerCase());
                    self.gPen = gColorMap.get(theColor.str().toLowerCase());
                    self.bPen = bColorMap.get(theColor.str().toLowerCase());

                } else {
                    PyTuple rgbTuple = (PyTuple) args.get(0);
                    self.rPen = Integer.valueOf(rgbTuple.data.get(0).str());
                    self.gPen = Integer.valueOf(rgbTuple.data.get(1).str());
                    self.bPen = Integer.valueOf(rgbTuple.data.get(2).str());
                }

                if (args.size() == 3) {
                    theColor = args.get(1);
                    self.fillColor = theColor;

                    if (theColor.getType().typeId() == PyType.PyTypeId.PyStrType) {
                        self.rFill = rColorMap.get(theColor.str().toLowerCase());
                        self.gFill = gColorMap.get(theColor.str().toLowerCase());
                        self.bFill = bColorMap.get(theColor.str().toLowerCase());

                    } else {
                        PyTuple rgbTuple = (PyTuple) args.get(0);
                        self.rFill = Integer.valueOf(rgbTuple.data.get(0).str());
                        self.gFill = Integer.valueOf(rgbTuple.data.get(1).str());
                        self.bFill = Integer.valueOf(rgbTuple.data.get(2).str());
                    }

                } else {
                    self.rFill = self.rPen;
                    self.gFill = self.gPen;
                    self.bFill = self.bPen;
                }

                return none;
            }

        });

        funs.put("pencolor", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                if (args.size() == 1) {
                    return self.penColor;
                }

                PyObject theColor = args.get(0);
                self.penColor = theColor;

                if (theColor.getType().typeId() == PyType.PyTypeId.PyStrType) {
                    self.rPen = rColorMap.get(theColor.str().toLowerCase());
                    self.gPen = gColorMap.get(theColor.str().toLowerCase());
                    self.bPen = bColorMap.get(theColor.str().toLowerCase());

                } else {
                    PyTuple rgbTuple = (PyTuple) args.get(0);
                    self.rPen = Integer.valueOf(rgbTuple.data.get(0).str());
                    self.gPen = Integer.valueOf(rgbTuple.data.get(1).str());
                    self.bPen = Integer.valueOf(rgbTuple.data.get(2).str());
                }

                return none;
            }

        });

        funs.put("fillcolor", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                if (args.size() == 1) {
                    return self.fillColor;
                }

                PyObject theColor = args.get(0);
                self.fillColor = theColor;

                if (theColor.getType().typeId() == PyType.PyTypeId.PyStrType) {
                    self.rFill = rColorMap.get(theColor.str().toLowerCase());
                    self.gFill = gColorMap.get(theColor.str().toLowerCase());
                    self.bFill = bColorMap.get(theColor.str().toLowerCase());

                } else {
                    PyTuple rgbTuple = (PyTuple) args.get(0);
                    self.rFill = Integer.valueOf(rgbTuple.data.get(0).str());
                    self.gFill = Integer.valueOf(rgbTuple.data.get(1).str());
                    self.bFill = Integer.valueOf(rgbTuple.data.get(2).str());
                }

                return none;
            }

        });

        funs.put("begin_fill", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);

                self.filling = true;
                self.fillArea = screen.createFillArea(self);
                return none;
            }

        });

        funs.put("end_fill", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);

                self.filling = false;
                self.fillArea.complete();
                return none;
            }

        });

        funs.put("filling", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() > 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);

                return new PyBool(self.filling);
            }

        });

        //public void write(PyTurtle t, String text, String align, String fontName, int pointSize, String style) {
        funs.put("write", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() < 2 || args.size() > 5) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 to 5 argument, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                String text = ((PyStr) args.get(args.size() - 2)).str();
                String fontName = "Arial";
                int pointSize = 8;
                String style = "normal";
                String align = "left";

                if (args.size() == 5) {
                    PyTuple tup = (PyTuple) args.get(0);
                    fontName = ((PyStr) tup.data.get(0)).str();
                    pointSize = ((PyInt) tup.data.get(1)).getVal();
                    style = ((PyStr) tup.data.get(2)).str();
                }

                if (args.size() >= 3) {
                    boolean move = ((PyBool) args.get(args.size() - 3)).getVal();
                    if (move) {
                        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                "Text turtle move value must be false, true is not supported.");
                    }
                }

                if (args.size() >= 4) {
                    align = ((PyStr) args.get(args.size() - 4)).str();
                }

                self.screen.write(self, text, align, fontName, pointSize, style);

                return none;
            }

        });

        funs.put("ondrag", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() < 2 || args.size() > 4) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2, 3, or 4 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                PyFunction handler = (PyFunction) args.get(args.size() - 2);
                int button = 1;

                if (args.size() >= 3) {
                    button = ((PyInt) args.get(args.size() - 3)).getVal();
                }

                boolean add = false;

                if (args.size() >= 4) {
                    add = ((PyBool) args.get(args.size() - 4)).getVal();
                }

                self.screen.registerTurtleOnDragHandler(self, button, handler, add);
                return none;
            }

        });

        funs.put("onclick", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() < 2 || args.size() > 4) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2, 3, or 4 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                PyFunction handler = (PyFunction) args.get(args.size() - 2);
                int button = 1;

                if (args.size() >= 3) {
                    button = ((PyInt) args.get(args.size() - 3)).getVal();
                }

                boolean add = false;

                if (args.size() >= 4) {
                    add = ((PyBool) args.get(args.size() - 4)).getVal();
                }

                self.screen.registerTurtleOnClickHandler(self, button, handler, add);
                return none;
            }

        });

        funs.put("onrelease", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() < 2 || args.size() > 4) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2, 3, or 4 arguments, got " + args.size());
                }

                PyTurtle self = (PyTurtle) args.get(args.size() - 1);
                PyFunction handler = (PyFunction) args.get(args.size() - 2);
                int button = 1;

                if (args.size() >= 3) {
                    button = ((PyInt) args.get(args.size() - 3)).getVal();
                }

                boolean add = false;

                if (args.size() >= 4) {
                    add = ((PyBool) args.get(args.size() - 4)).getVal();
                }

                self.screen.registerTurtleOnReleaseHandler(self, button, handler, add);
                return none;
            }

        });

        return funs;
    }

    public void draw(Graphics2D g2) {

        if (!hideTurtle) {
            AffineTransform tx = new AffineTransform();
            Point2D.Double coord = screen.convertToScreen(x, y);
            tx.translate(coord.getX(), coord.getY());

            tx.rotate(Math.toRadians(this.direction));
            tx.translate((-1 * image.getWidth())/screen.xScale, (-1 * image.getHeight() / (2 * screen.yScale)));
            tx.scale(1/screen.xScale,1/screen.yScale);

            g2.setColor(new Color(rPen, gPen, bPen));
            g2.drawImage(image, tx, null);
        }

    }
}
