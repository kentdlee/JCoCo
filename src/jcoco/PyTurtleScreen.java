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
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;

/**
 *
 * @author leekentd
 */
public class PyTurtleScreen extends PyPrimitiveTypeAdapter {

    private static PyNone none = new PyNone();

    private TurtlePanel panel = null;
    private JFrame frame = null;
    private ArrayList<GraphicsObject> shapes = new ArrayList<GraphicsObject>();
    private ArrayList<GraphicsObject> turtles = new ArrayList<GraphicsObject>();
    private boolean exitonclick = false;

    public PyTurtleScreen() {
        super("turtle._Screen", PyType.PyTypeId.PyTurtleType);
        initMethods(funs());

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Turtle Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);
        frame.setSize(1000, 1000);

        panel = new TurtlePanel();

        frame.add(panel);

        frame.setVisible(true);
    }

    public void registerTurtle(PyTurtle t) {
        turtles.add(new TurtleObject(t));
    }

    public void move(PyTurtle t, double distance) {
        double dx = 3;
        double distSoFar = 0;
        double x1 = t.x;
        double y1 = t.y;
        double radians = Math.toRadians(t.direction);

        double x2 = t.getX();
        double y2 = t.getY();
        shapes.add(new Line(t.x, t.y, x2, y2));

        while (distSoFar < distance) {
            distSoFar += dx;

            x2 = Math.cos(radians) * dx + t.x;
            y2 = Math.sin(radians) * dx + t.y;
            if (t.isdown()) {
                shapes.remove(shapes.size() - 1);
                shapes.add(new Line(x1, y1, x2, y2));
            }
            t.setLocation(x2, y2);
            this.update();
        }

        x2 = Math.cos(radians) * distance + x1;
        y2 = Math.sin(radians) * distance + y1;
        if (t.isdown()) {
            shapes.remove(shapes.size() - 1);
            shapes.add(new Line(x1, y1, x2, y2));
        }
        t.setLocation(x2, y2);
        this.update();
    }

    public void update() {
        panel.invalidate();
        panel.repaint();
        try {
            Thread.sleep(6);
        } catch (Exception ex) {
        }
    }

    public void redraw(Graphics2D g2) {
        for (GraphicsObject obj : shapes) {
            obj.draw(g2);
        }
        for (GraphicsObject turtle : turtles) {
            turtle.draw(g2);
        }
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("exitonclick", new PyCallableAdapter() {
            @Override
            public PyObject __call__(ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 arguments, got " + args.size());
                }

                PyTurtleScreen self = (PyTurtleScreen) args.get(args.size() - 1);
                self.exitonclick = true;
                return none;
            }
        });

        return funs;
    }

    private class TurtlePanel extends JPanel {

        public TurtlePanel() {
            super();
            addMouseListener(new MouseAdapter() {
                private Color background;

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (exitonclick) {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                }
            });
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform origTrans = g2.getTransform();
            AffineTransform tform = AffineTransform.getTranslateInstance(500, 500);
            g2.setTransform(tform);
            redraw(g2);
            g2.setTransform(origTrans);
        }

    }

    private interface GraphicsObject {

        public void draw(Graphics2D g2);
    }

    private class Line implements GraphicsObject {

        int x1, y1, x2, y2;

        public Line(double x1, double y1, double x2, double y2) {
            this.x1 = (int) x1;
            this.y1 = (int) y1;
            this.x2 = (int) x2;
            this.y2 = (int) y2;
        }

        public void draw(Graphics2D g2) {
            g2.drawLine(x1, y1, x2, y2);
        }

    }

    private class TurtleObject implements GraphicsObject {

        private PyTurtle turtle;
        private BufferedImage image = null;

        public TurtleObject(PyTurtle t) {
            turtle = t;
            java.net.URL imageURL = this.getClass().getResource("resources/arrow.png");
            //File imageFile = new File(imageURL);
            try {
                image = ImageIO.read(imageURL);

            } catch (Exception ex) {
                System.out.println("Unable to read arrow.png");
                ex.printStackTrace();
            }
        }

        public void draw(Graphics2D g2) {
            AffineTransform tx = new AffineTransform();
            tx.translate(turtle.x, turtle.y);

            tx.rotate(Math.toRadians(turtle.direction));
            tx.translate(-1 * image.getWidth(), -1 * image.getHeight() / 2);

            g2.drawImage(image, tx, null);
        }
    }

}
