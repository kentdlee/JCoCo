import disassembler
import sys

def main():
	x = "How's it going?"

	y = x + " I am trying to get this \"program\" to work!"

	print(y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)