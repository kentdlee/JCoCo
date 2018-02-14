import disassembler
import sys

def main():
	x = 5.0
	y = 2.0

	print(x/y)
	print(x//y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)