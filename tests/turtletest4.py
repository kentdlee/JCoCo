import turtle
import disassembler
import sys

def main():

    t = turtle.Turtle()

    t.forward(100)

    screen = t.getscreen()
    screen.exitonclick()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
