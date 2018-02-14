import turtle
import disassembler
import sys

def main():
    t = turtle.Turtle()
    for k in range(4):
        t.forward(100)
        t.right(90)

    screen = t.getscreen()
    screen.exitonclick()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
