import disassembler
import sys

def main():
	s = input("Enter a list of integers:")
	lst = s.split()

	print(lst)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)