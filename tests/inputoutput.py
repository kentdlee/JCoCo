import disassembler
import sys

def main():
	x = int(input("Please enter an integer: "))
	y = float(input("Please enter a number: "))
	print("The product is", x*y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)