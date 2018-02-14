import disassembler
import sys
def main():

	d = {}
	d[4.5] = "hello world"

	print(d[4.5])

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)