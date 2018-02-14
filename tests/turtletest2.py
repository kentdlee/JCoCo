import turtle
import disassembler
import sys

def main():

    t = turtle.Turtle()

    #t.up()
    t.speed(0)
    t.rt(360)
    t.left(360)
    t.right(45)
    t.fd(30)
    t.dot(20)
    t.home()
    t.ht()
    print(t.isvisible())
    print(t.isdown())
    t.left(45)
    t.goto(-50,-100)
    t.home()
    s = turtle.Turtle()
    s.pencolor("red")
    #s.speed(0)
    s.hideturtle()
    s.width(10)
    s.left(45)
    s.goto(0,0)
    s.hideturtle()
    s.begin_fill()
    s.circle(100)
    s.end_fill()
    s.pencolor("blue")
    s.write("Hello World!!!!!",False,"right",("Arial",24,"bold"))
    t.hideturtle()
    t.home()
    t.fd(50)
    stamp_id = t.stamp()
    for i in range(7):
        t.fd(10)
        t.rt(50)
        stamp_id = t.stamp()
    t.undo()
    t.clearstamps()
    t.home()
    t.goto(-75,100)
    for i in range(7):
        t.fd(10)
        t.rt(50)
        stamp_id = t.stamp()
    t.goto(-200,200)
    for i in range(7):
        t.fd(10)
        t.rt(50)
        stamp_id = t.stamp()
    t.undo()


    screen = t.getscreen()
    screen.exitonclick()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
