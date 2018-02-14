import disassembler
import sys

def main():
	x = 6.2
	y = 4.8
	print(x % y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)