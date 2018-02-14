/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 1, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This implements the Turtle's screen that is drawn on. The
 * coordinates of this screen are set by the setworldcoordinates method. But,
 * the screen internally converts all coordinates to an 8000x8000 pixel canvas
 * with (0,0) centered vertically and horizontally.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;
import java.math.*;
import java.util.ConcurrentModificationException;
import java.awt.BasicStroke;
import java.util.HashSet;
import java.util.Set;
import javafx.util.Pair;

/**
 *
 * @author leekentd
 */
public class PyTurtleScreen extends PyPrimitiveTypeAdapter {

    private static final int ScreenWidth = 8000;
    private static final int ScreenHeight = 8000;
    private double screenXFactor;
    private double screenYFactor;
    private static PyNone none = new PyNone();
    private static int stampNum = 0;
    private static int n;
    private TurtlePanel panel = null;
    private JFrame frame = null;

    private ArrayList<PyTurtle> turtles = new ArrayList<PyTurtle>();
    private HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>> turtleOnClickHandlers = new HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>>();
    private HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>> turtleOnReleaseHandlers = new HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>>();
    private HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>> turtleOnDragHandlers = new HashMap<Pair<PyTurtle, Integer>, ArrayList<PyFunction>>();
    private boolean exitonclick = false;
    private int updateCount = 0;
    int updateThreshold = 1;
    public boolean pendown = false;
    public boolean hideTurtle = false;
    private boolean painting = false;
    private double llx, lly, urx, ury;
    public double xScale, yScale;
    AffineTransform btnxform;
    private double canvasWidth, canvasHeight;
    private int xTrans;
    private int yTrans;
    private int bgr, bgg, bgb;

    public Point2D.Double convertToScreen(double x, double y) {
        double screenX = (x - llx) * screenXFactor - ScreenWidth / 2;
        double screenY = (y - lly) * screenYFactor - ScreenHeight / 2;

        return new Point2D.Double(screenX, screenY);
    }

    public Point2D.Double convertFromScreen(double x, double y) {
        double worldX = (x + ScreenWidth / 2) / screenXFactor + llx;
        double worldY = (y + ScreenHeight / 2) / screenYFactor + lly;

        return new Point2D.Double(worldX, worldY);
    }

    public PyTurtleScreen() {
        super("turtle._Screen", PyType.PyTypeId.PyTurtleScreenType);
        initMethods(funs());

        JFrame.setDefaultLookAndFeelDecorated(false);
        frame = new JFrame("Turtle Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);
        frame.setSize(1000, 1000);
        llx = -500;
        lly = -500;
        urx = 500;
        ury = 500;
        xScale = 1 / 8.0;
        yScale = -1 / 8.0;
        canvasWidth = 1000;
        canvasHeight = 1000;
        screenXFactor = ScreenWidth / (urx - llx);
        screenYFactor = ScreenHeight / (ury - lly);
        xTrans = (int) round(canvasWidth / 2, 0);
        yTrans = (int) round(canvasHeight / 2, 0);
        bgr = 255;
        bgg = 255;
        bgb = 255;

        panel = new TurtlePanel();

        frame.add(panel);

        frame.setVisible(true);
    }

    public void registerTurtle(PyTurtle t) {
        turtles.add(t);
        update();
    }

    private int[] toIntArray(ArrayList<Integer> al) {
        int[] nl = new int[al.size()];
        for (int i = 0; i < al.size(); i++) {
            nl[i] = al.get(i).intValue();
        }

        return nl;
    }

    private interface GraphicsObject {

        public void draw(Graphics2D g2);

        public int getId();

        public PyTurtle turtle();
    }

    public void makeDot(PyTurtle t, int diameter) {
        shapes.add(new Dot(t, diameter));
        this.update();
    }

    private class Dot implements GraphicsObject {

        private double radius;
        private PyTurtle t;
        private int x, y;
        private int r, g, b;

        public Dot(PyTurtle t, double diameter) {
            Point2D.Double coord = convertToScreen(t.x, t.y);
            this.radius = (diameter * screenXFactor) / 2.0;
            this.t = t;
            this.x = (int) round(coord.getX(), 0);
            this.y = (int) round(coord.getY(), 0);
            this.r = t.rPen;
            this.g = t.gPen;
            this.b = t.bPen;
        }

        public PyTurtle turtle() {
            return t;
        }

        public void draw(Graphics2D g2) {
            double dx, dy, radians;
            int nextx;
            int nexty;
            int k;

            ArrayList<Integer> xP = new ArrayList<Integer>();
            ArrayList<Integer> yP = new ArrayList<Integer>();

            for (k = 0; k < 360; k++) {
                radians = Math.toRadians(k);
                dx = radius * Math.cos(radians);
                dy = radius * Math.sin(radians);
                nextx = x + (int) round(dx, 0);
                nexty = y + (int) round(dy, 0);
                xP.add(nextx);
                yP.add(nexty);
            }

            g2.setColor(new Color(r, g, b));
            g2.fillPolygon(toIntArray(xP), toIntArray(yP), 360);
        }

        public int getId() {
            return -1;
        }
    }

    private class Stamp implements GraphicsObject {

        private PyTurtle t;
        private PyTurtle originalTurtle;
        private int id;

        public Stamp(PyTurtle t) {
            originalTurtle = t;
            this.t = new PyTurtle(t);
            this.t.showturtle();
            id = stampNum;
            stampNum += 1;
        }

        public PyTurtle turtle() {
            return originalTurtle;
        }

        public void draw(Graphics2D g2) {
            g2.setColor(new Color(t.rPen, t.gPen, t.bPen));
            t.draw(g2);
        }

        public int getId() {
            return id;
        }
    }

    public int makeStamp(PyTurtle t) {
        Stamp p = new Stamp(t);
        shapes.add(p);
        this.update();
        return p.getId();
    }

    class FillArea implements GraphicsObject {

        private ArrayList<Integer> xP;
        private ArrayList<Integer> yP;
        private int[] xPa;
        private int[] yPa;
        private int r, g, b;
        private PyTurtle t;
        private boolean completed = false;

        public FillArea(PyTurtle t, int r, int g, int b) {
            this.xP = new ArrayList<Integer>();
            this.yP = new ArrayList<Integer>();
            this.r = r;
            this.g = g;
            this.b = b;
            this.t = t;
        }

        public PyTurtle turtle() {
            return t;
        }

        public void draw(Graphics2D g2) {
            if (completed) {
                g2.setColor(new Color(r, g, b));
                g2.fillPolygon(xPa, yPa, xPa.length);
            }
        }

        public int getId() {
            return -1;
        }

        public void complete() {
            completed = true;
            xPa = toIntArray(xP);
            yPa = toIntArray(yP);
            update();
        }

        public void addPoint(Point2D.Double coord) {
            if (!completed) {
                xP.add((int) round(coord.getX(), 0));
                yP.add((int) round(coord.getY(), 0));
            }
        }
    }

    public FillArea createFillArea(PyTurtle t) {
        FillArea fa = new FillArea(t, t.rFill, t.gFill, t.bFill);
        shapes.add(fa);
        return fa;
    }

    private class Text implements GraphicsObject {

        PyTurtle turtle = null;
        String theText;
        int x, y;
        int r, g, b;
        Font theFont;
        int alignment; //-2 = right aligned, -1 = centered, 0 = left aligned
        final static int leftAligned = 0;
        final static int rightAligned = -2;
        final static int centerAligned = -1;

        public Text(PyTurtle t, String text, String align, String fontName, int pointSize, String style) {
            Point2D.Double coord = convertToScreen(t.x, t.y);
            this.x = (int) round(coord.getX(), 0);
            this.y = (int) round(coord.getY(), 0);
            theText = text;

            int theStyle;
            if (style.equals("normal")) {
                theStyle = Font.PLAIN;
            } else if (style.equals("bold")) {
                theStyle = Font.BOLD;
            } else if (style.equals("italics")) {
                theStyle = Font.ITALIC;
            } else {
                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unknown Font Style");
            }

            if (align.equals("left")) {
                alignment = leftAligned;
            } else if (align.equals("right")) {
                alignment = rightAligned;
            } else if (align.equals("center")) {
                alignment = centerAligned;
            } else {
                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unknown Text Alignment");
            }

            try {
                this.theFont = new Font(fontName, theStyle, pointSize);
            } catch (Exception e) {
                throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, e.getMessage());
            }

            this.r = t.rPen;
            this.g = t.gPen;
            this.b = t.bPen;
            turtle = t;
        }

        public int getId() {
            return -1;
        }

        public PyTurtle turtle() {
            return turtle;
        }

        public void draw(Graphics2D g2) {
            g2.setColor(new Color(r, g, b));
            g2.setFont(theFont);
            FontMetrics fm = g2.getFontMetrics();
            int width = fm.stringWidth(theText);
            AffineTransform origTrans = g2.getTransform();
            AffineTransform tform = AffineTransform.getTranslateInstance(xTrans, yTrans);
            g2.setTransform(tform);
            g2.drawString(theText, x + (width / 2) * alignment, y);
            g2.setTransform(origTrans);
        }
    }

    public void write(PyTurtle t, String text, String align, String fontName, int pointSize, String style) {
        shapes.add(new Text(t, text, align, fontName, pointSize, style));
        this.update();
    }

    private class Line implements GraphicsObject {

        PyTurtle turtle = null;
        int x1, y1, x2, y2, width, r, g, b;

        public Line(PyTurtle t, Point2D.Double first, Point2D.Double last) {
            this.x1 = (int) round(first.getX(), 0);
            this.y1 = (int) round(first.getY(), 0);
            this.x2 = (int) round(last.getX(), 0);
            this.y2 = (int) round(last.getY(), 0);
            this.r = t.rPen;
            this.g = t.gPen;
            this.b = t.bPen;
            this.width = (int) round(t.width() * screenXFactor, 0);
            turtle = t;
        }

        public int getId() {
            return -1;
        }

        public PyTurtle turtle() {
            return turtle;
        }

        public void draw(Graphics2D g2) {
            // Lines of 1 pixel will not be drawn on the screen. This 
            // removes artifacts that appear. A line must actually have 
            // some length for it to appear and 1 pixel lines have no 
            // length.

            g2.setColor(new Color(r, g, b));

            if (this.x1 != this.x2 || this.y1 != this.y2) {
                g2.setStroke(new BasicStroke(width));
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private ArrayList<GraphicsObject> shapes = new ArrayList<GraphicsObject>();

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void doTheMove(PyTurtle t, double distance, double angleOffset) {

        if (round(distance, 10) > 0) {
            double dx = t.speed;

            if (dx == 0) {
                dx = 999999999;
            }

            double distSoFar = dx;
            double x1 = t.x;
            double y1 = t.y;
            double x2 = t.getX();
            double y2 = t.getY();
            double radians = Math.toRadians(t.direction + angleOffset);

            shapes.add(new Line(t, convertToScreen(t.x, t.y), convertToScreen(x2, y2)));

            if (this.updateThreshold > 0) {

                while (distSoFar < distance) {

                    x2 = Math.cos(radians) * dx + t.x;
                    y2 = Math.sin(radians) * dx + t.y;
                    if (t.isdown()) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(new Line(t, convertToScreen(x1, y1), convertToScreen(x2, y2)));
                    }
                    t.setLocation(x2, y2);
                    this.update();
                    distSoFar += dx;
                }
            }
            x2 = round(Math.cos(radians) * distance + x1, 10);
            y2 = round(Math.sin(radians) * distance + y1, 10);

            //x2 = Math.cos(radians) * distance + x1;
            //y2 = Math.sin(radians) * distance + y1;
            if (t.isdown()) {
                shapes.remove(shapes.size() - 1);
                shapes.add(new Line(t, convertToScreen(x1, y1), convertToScreen(x2, y2)));
            }
            t.setLocation(x2, y2);

            if (t.speed != 0) {
                this.update();
            }

        }

    }

    public void redraw(Graphics2D g2) {
        try {

            for (GraphicsObject shape : shapes) {
                shape.draw(g2);
            }

            for (PyTurtle turtle : turtles) {
                turtle.draw(g2);
            }

        } catch (ConcurrentModificationException ex) {
            //if this occurs, the shapes or turtles array was modified
            //by another thread as this was executing, but a repaint
            //will cause this to be called again in a moment anyway.
        }

    }

    public void undo(PyTurtle t) {
        int k = -1;
        int i;

        // search through the list and remember 
        // the last position that we found a 
        // drawing command from turtle t.
        for (i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).turtle().equals(t)) {
                k = i;
            }
        }

        // if k >= 0 then we found at least one
        // drawing command from turtle t and k 
        // is the index of the last one. So remove 
        // it.
        if (k >= 0) {
            shapes.remove(k);
            this.update();
        }
    }

    public void resetTurtle(PyTurtle t) {
        ArrayList<GraphicsObject> newShapeList = new ArrayList<GraphicsObject>();
        for (GraphicsObject shape : shapes) {
            if (!shape.turtle().equals(t)) {
                newShapeList.remove(shape);
            }
        }

        shapes = newShapeList;
        this.update();
    }

    public void clearStamp(int stampID) {
        ArrayList<GraphicsObject> newShapeList = new ArrayList<GraphicsObject>();
        for (GraphicsObject shape : shapes) {
            if (shape.getId() != stampID) {
                newShapeList.add(shape);
            }
        }

        shapes = newShapeList;
        this.update();
    }

    public void clearStamps(PyTurtle t, int numStamps) {
        ArrayList<GraphicsObject> newShapeList = new ArrayList<GraphicsObject>();
        for (GraphicsObject shape : shapes) {
            if (!shape.turtle().equals(t) || shape.getId() < 0) {
                newShapeList.add(shape);
            }
        }

        shapes = newShapeList;
        this.update();
    }

    private synchronized void doUpdate() {
        // utitlity function. do not call
        panel.revalidate();
        panel.repaint();
        try {
            Thread.sleep(6);
        } catch (Exception ex) {
        }
    }

    public void update() {
        //if screen tracer is 0 then update must be called explicitly
        if (updateThreshold == 0) {
            return;
        }
        updateCount += 1;
        if (updateCount == updateThreshold) {
            updateCount = 0;
            doUpdate();
        }
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("exitonclick", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(args.size() - 1);
                self.exitonclick = true;
                self.doUpdate();
                return none;
            }
        });
        funs.put("tracer", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() < 1 || args.size() > 3) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1,2 or 3 arguments, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(args.size() - 1);
                self.updateThreshold = Integer.valueOf(args.get(0).str());
                return none;
            }
        }
        );
        funs.put("update", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(args.size() - 1);
                self.doUpdate();
                return none;
            }
        }
        );

        funs.put("mainloop", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                return none;
            }
        }
        );

        funs.put("setworldcoordinates", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args
            ) {
                if (args.size() != 5) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 5 argument, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(4);
                self.ury = (double) (Double.valueOf(args.get(0).str()));
                self.urx = (double) (Double.valueOf(args.get(1).str()));
                self.lly = (double) (Double.valueOf(args.get(2).str()));
                self.llx = (double) (Double.valueOf(args.get(3).str()));

                self.screenXFactor = self.ScreenWidth / (self.urx - self.llx);
                self.screenYFactor = self.ScreenHeight / (self.ury - self.lly);
                self.xTrans = (int) round(self.canvasWidth / 2, 0);
                self.yTrans = (int) round(self.canvasHeight / 2, 0);

                self.update();
                return none;
            }
        }
        );

        funs.put("screensize", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() < 3 || args.size() > 4) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 3 or 4 arguments, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(args.size() - 1);
                self.canvasHeight = (Integer.valueOf(args.get(args.size() - 2).str())).intValue();
                self.canvasWidth = (Integer.valueOf(args.get(args.size() - 3).str())).intValue();
                self.xScale = self.canvasWidth / ScreenWidth;
                self.yScale = -self.canvasHeight / ScreenHeight;
                self.frame.setSize((int) self.canvasWidth, (int) self.canvasHeight);
                self.xTrans = (int) round(((0 - self.llx) / (self.urx - self.llx)) * self.canvasWidth, 0);
                self.yTrans = (int) round(((0 - self.lly) / (self.ury - self.lly)) * self.canvasHeight, 0);

                if (args.size() == 4) {
                    PyObject theColor = args.get(0);

                    if (theColor.getType().typeId() == PyType.PyTypeId.PyStrType) {
                        self.bgr = PyTurtle.rColorMap.get(theColor.str());
                        self.bgg = PyTurtle.gColorMap.get(theColor.str());
                        self.bgb = PyTurtle.bColorMap.get(theColor.str());

                    } else {
                        PyTuple rgbTuple = (PyTuple) args.get(0);
                        self.bgr = Integer.valueOf(rgbTuple.data.get(0).str());
                        self.bgg = Integer.valueOf(rgbTuple.data.get(1).str());
                        self.bgb = Integer.valueOf(rgbTuple.data.get(2).str());

                    }
                }

                self.update();
                return none;
            }
        });

        return funs;

    }

    void registerTurtleOnClickHandler(PyTurtle t, int button, PyFunction f, boolean add) {
        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, button);
        ArrayList<PyFunction> handlers = null;

        if (turtleOnClickHandlers.containsKey(key)) {
            handlers = turtleOnClickHandlers.get(key);
            if (!add) {
                handlers.remove(0);
            }
        } else {
            handlers = new ArrayList<PyFunction>();
            turtleOnClickHandlers.put(key, handlers);
        }

        handlers.add(f);

    }

    void registerTurtleOnReleaseHandler(PyTurtle t, int button, PyFunction f, boolean add) {
        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, button);
        ArrayList<PyFunction> handlers = null;

        if (turtleOnReleaseHandlers.containsKey(key)) {
            handlers = turtleOnReleaseHandlers.get(key);
            if (!add) {
                handlers.remove(0);
            }
        } else {
            handlers = new ArrayList<PyFunction>();
            turtleOnReleaseHandlers.put(key, handlers);
        }

        handlers.add(f);
    }

    void registerTurtleOnDragHandler(PyTurtle t, int button, PyFunction f, boolean add) {

        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, button);
        ArrayList<PyFunction> handlers = null;

        if (turtleOnDragHandlers.containsKey(key)) {
            handlers = turtleOnDragHandlers.get(key);
            if (!add) {
                handlers.remove(0);
            }
        } else {
            handlers = new ArrayList<PyFunction>();
            turtleOnDragHandlers.put(key, handlers);
        }

        handlers.add(f);

    }

    private class TurtlePanel extends JPanel {

        public TurtlePanel() {
            super();
            MouseAdapter myListener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (exitonclick) {
                        frame.setVisible(false);
                        frame.dispose();
                    } else {
                        boolean found = false;

                    }

                    int buttonId = 0;
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        buttonId = 1;
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        buttonId = 2;
                    } else {
                        buttonId = 3;
                    }

                    Point2D p = btnxform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    p = convertFromScreen(p.getX(), p.getY());
                    double x = p.getX();
                    double y = p.getY();

                    for (PyTurtle t : turtles) {
                        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, buttonId);

                        if (turtleOnReleaseHandlers.containsKey(key)) {
                            if (t.distance(x, y) < t.radius()) {
                                for (PyFunction handler : turtleOnReleaseHandlers.get(key)) {
                                    ArrayList<PyObject> args = new ArrayList<PyObject>();
                                    args.add(new PyFloat(y));
                                    args.add(new PyFloat(x));
                                    try {
                                        // A new call stack needs to be created here because
                                        // this code runs under a different thread.
                                        handler.__call__(new PyCallStack(), args);
                                    } catch (PyException ex) {

                                        System.err.print("\n\n");
                                        System.err.println("*********************************************************");
                                        System.err.println("        An Uncaught Exception Occurred");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("---------------------------------------------------------");
                                        System.err.println("              The Exception's Traceback");
                                        System.err.println("---------------------------------------------------------");
                                        ex.printTraceBack();
                                        System.err.println("*********************************************************");
                                        System.err.println("            An Uncaught Exception Occurred (See Above) ");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("*********************************************************");
                                    }
                                }
                            }
                        }
                    }

                    // check for screen onRelease Handler.
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    int buttonId = 0;
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        buttonId = 1;
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        buttonId = 2;
                    } else {
                        buttonId = 3;
                    }

                    Point2D p = btnxform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    p = convertFromScreen(p.getX(), p.getY());
                    double x = p.getX();
                    double y = p.getY();

                    for (PyTurtle t : turtles) {
                        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, buttonId);

                        if (turtleOnClickHandlers.containsKey(key)) {
                            if (t.distance(x, y) < t.radius()) {
                                for (PyFunction handler : turtleOnClickHandlers.get(key)) {
                                    ArrayList<PyObject> args = new ArrayList<PyObject>();
                                    args.add(new PyFloat(y));
                                    args.add(new PyFloat(x));
                                    try {
                                        handler.__call__(new PyCallStack(), args);
                                    } catch (PyException ex) {

                                        System.err.print("\n\n");
                                        System.err.println("*********************************************************");
                                        System.err.println("        An Uncaught Exception Occurred");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("---------------------------------------------------------");
                                        System.err.println("              The Exception's Traceback");
                                        System.err.println("---------------------------------------------------------");
                                        ex.printTraceBack();
                                        System.err.println("*********************************************************");
                                        System.err.println("            An Uncaught Exception Occurred (See Above) ");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("*********************************************************");
                                    }
                                }
                            }
                        }
                    }

                    // check for screen onClick Handler.
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int buttonId = 0;
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        buttonId = 1;
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        buttonId = 2;
                    } else {
                        buttonId = 3;
                    }

                    Point2D p = btnxform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    p = convertFromScreen(p.getX(), p.getY());
                    double x = p.getX();
                    double y = p.getY();

                    for (PyTurtle t : turtles) {
                        Pair<PyTurtle, Integer> key = new Pair<PyTurtle, Integer>(t, buttonId);
                        if (turtleOnDragHandlers.containsKey(key)) {
                            if (t.distance(x, y) < 1.5 * t.radius()) {
                                for (PyFunction handler : turtleOnDragHandlers.get(key)) {
                                    ArrayList<PyObject> args = new ArrayList<PyObject>();
                                    args.add(new PyFloat(y));
                                    args.add(new PyFloat(x));
                                    try {
                                        handler.__call__(new PyCallStack(), args);
                                    } catch (PyException ex) {

                                        System.err.print("\n\n");
                                        System.err.println("*********************************************************");
                                        System.err.println("        An Uncaught Exception Occurred");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("---------------------------------------------------------");
                                        System.err.println("              The Exception's Traceback");
                                        System.err.println("---------------------------------------------------------");
                                        ex.printTraceBack();
                                        System.err.println("*********************************************************");
                                        System.err.println("            An Uncaught Exception Occurred (See Above) ");
                                        System.err.println("*********************************************************");
                                        System.err.println(ex.str());
                                        System.err.println("*********************************************************");
                                    }
                                }
                            }
                        }
                    }

                }
            };

            addMouseListener(myListener);
            addMouseMotionListener(myListener);

        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(bgr, bgg, bgb));
            g2.fillRect(0, 0, (int) canvasWidth, (int) canvasHeight);
            AffineTransform origTrans = g2.getTransform();
            AffineTransform tform = AffineTransform.getTranslateInstance(xTrans, yTrans);
            tform.scale(xScale, yScale);
            g2.setTransform(tform);
            redraw(g2);
            btnxform = tform;
            try {
                btnxform.invert();
            } catch (Exception ex) {
            }
            g2.setTransform(origTrans);
        }
    }
}
