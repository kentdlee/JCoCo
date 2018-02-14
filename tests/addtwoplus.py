from disassembler import *
import sys

def main():
	x = 5
	y = 6
	z = x + y
	u = int.__add__(z,x)
	print(z, u)
	print(int.__str__(5))
	print(str(int))

if len(sys.argv) == 1:
	main()
else:
	disassemble(main)