import disassembler
import sys

def main():
	print('hello world')

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)