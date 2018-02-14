import disassembler
import sys

def main():
	x = 3
	y = 4
	x,y = y,x
	print(x,y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)