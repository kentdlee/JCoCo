import disassembler
import sys

def main():

  x = input("Please enter a string: ")
  for a in x.split():
    print(a)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main) 
