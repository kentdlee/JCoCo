import disassembler
import sys

def main():
	print("how","are","you?")

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)