import disassembler
import sys

def main():
	x = "Test"
	print(x)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main);
