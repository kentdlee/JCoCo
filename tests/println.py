import disassembler
import sys

def main():
	print("Hello there\'s\"\n\n\nWorld!")

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)