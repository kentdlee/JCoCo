import disassembler
import sys

def main():
	x = 5
	y = 6
	print(x if x > y else y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)