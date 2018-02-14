import disassembler
import sys

def f(x):
	def g(y): 
		return x + y

	return g(x)

def main():
	print(f(3))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(f)
	disassembler.disassemble(main)