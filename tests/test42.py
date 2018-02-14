import disassembler
import sys
def main():

	d = 2147483647
	e = d * d

	print(e)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)