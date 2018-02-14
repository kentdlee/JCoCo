import disassembler
import sys

def main():
	d = {}

	d[d] = d

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)