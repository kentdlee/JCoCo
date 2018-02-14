import turtle
import disassembler
import sys

def main():
    t = turtle.Turtle()
    def dragged(x,y):
        t.goto(x,y)
        screen.update()


    t.ondrag(dragged,1)
    screen = t.getscreen()
    screen.tracer(0)
    screen.screensize(2000,2000,"green");
    screen.setworldcoordinates(-1,-1,1,1);
    t.goto(0,0)
    t.goto(0.5,0.5)
    screen.update()
    screen.mainloop()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
