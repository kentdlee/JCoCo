import disassembler
import sys

def f(x,y):
    if x==0:
        return y
    else:
        return g(x, x*y)

def g(x,y):
    return f(x-1,y)

def main():
    print(f(10,5))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(f)
	disassembler.disassemble(g)
	disassembler.disassemble(main)
