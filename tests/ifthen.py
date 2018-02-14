import disassembler
import sys

def main():
	x = 5
	y = 6
	if x > y:
		print(x)

	print(y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)