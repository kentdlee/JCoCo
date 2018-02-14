import turtle
import disassembler
import sys

def main():
    t = turtle.Turtle()
    for k in range(4):
        t.forward(100)
        t.right(90)
    t.reset()
    t = turtle.Turtle()
    t.rt(90)
    for k in range(4):
        t.forward(100)
        t.right(90)
    t.reset()
    t = turtle.Turtle()
    t.rt(180)
    for k in range(4):
        t.forward(100)
        t.right(90)
    t.reset()
    t = turtle.Turtle()
    t.rt(270)
    for k in range(4):
        t.forward(100)
        t.right(90)
    t.reset()
    t = turtle.Turtle()
    t.goto(100,100)
    t.goto(-100,100)
    t.goto(0,0)
    t.goto(10, 10)
    print('towards angle: ', t.towards(0,30))
    print('position:', t.position())
    t.reset()
    print(t.position())
    #t.showturtle()
    #print(t.getpen())
    t.fd(50)
    t.rt(45)
    #t.home()
    print(t.distance(30,40))
    

    screen = t.getscreen()
    screen.exitonclick()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)