import disassembler
import sys

def main():
	#try:
	print(list(range(0,int(input("Please enter a non-negative integer:"))+1,2)))
	#except:
	#	print("You didn't enter an integer.")

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)