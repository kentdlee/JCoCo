import disassembler
import sys

def main():
	d = { "Kent":"Denise", "Mesa":"Sequoia"}

	print(d)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
