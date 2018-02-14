import disassembler
import sys

def main():

	name = input("Enter your name: ")
	age = int(input("Enter your age: "))
	print(name + ", a year from now you will be", age+1, "years old.")

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)